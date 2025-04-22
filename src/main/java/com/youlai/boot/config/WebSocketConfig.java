package com.youlai.boot.config;

import cn.hutool.core.util.StrUtil;
import com.youlai.boot.core.security.model.SysUserDetails;
import com.youlai.boot.core.security.token.TokenManager;
import com.youlai.boot.system.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket配置
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@EnableWebSocketMessageBroker
@Configuration
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final TokenManager tokenManager;
  private final WebSocketService webSocketService;

  public WebSocketConfig(TokenManager tokenManager, @Lazy WebSocketService webSocketService) {
    this.tokenManager = tokenManager;
    this.webSocketService = webSocketService;
  }

  /**
   * 注册一个端点，客户端通过这个端点进行连接
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
      // 注册 /ws 的端点
      .addEndpoint("/ws")
      // 允许跨域
      .setAllowedOriginPatterns("*");
  }


  /**
   * 配置消息代理
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // 客户端发送消息的请求前缀
    registry.setApplicationDestinationPrefixes("/app");

    // 客户端订阅消息的请求前缀，topic一般用于广播推送，queue用于点对点推送
    registry.enableSimpleBroker("/topic", "/queue");

    // 服务端通知客户端的前缀，可以不设置，默认为user
    registry.setUserDestinationPrefix("/user");
  }


  /**
   * 配置客户端入站通道拦截器
   * <p>
   * 核心功能：
   * 1. 连接建立时解析令牌并绑定用户身份
   * 2. 连接关闭时触发下线通知
   * 3. 异常Token的防御性处理
   */
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      @Override
      public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
          return ChannelInterceptor.super.preSend(message, channel);
        }

        try {
          // 处理客户端连接请求
          if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            /*
             * 安全校验流程：
             * 1. 从HEADER中获取Authorization值
             * 2. 校验Bearer Token格式合法性
             * 3. 解析并验证JWT有效性
             * 4. 绑定用户身份到当前会话
             */
            String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

            // 防御性校验：确保Authorization头存在且格式正确
            if (StrUtil.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
              log.warn("非法连接请求：缺少有效的Authorization头");
              throw new AuthenticationCredentialsNotFoundException("Missing authorization header");
            }

            // 提取并处理JWT令牌（移除Bearer前缀）
            String token = authorization.substring(7);
            Authentication authentication = tokenManager.parseToken(token);

            // 令牌解析失败处理
            if (authentication == null) {
              log.error("令牌解析失败：{}", token);
              throw new BadCredentialsException("Invalid token");
            }

            // 获取用户详细信息
            SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
            if (userDetails == null || StrUtil.isBlank(userDetails.getUsername())) {
              log.error("无效的用户凭证：{}", token);
              throw new BadCredentialsException("Invalid user credentials");
            }

            String username = userDetails.getUsername();
            log.info("WebSocket连接建立：用户[{}]", username);

            // 绑定用户身份到当前会话（重要：用于@SendToUser等注解）
            accessor.setUser(authentication);

            // 记录用户上线状态
            webSocketService.userConnected(username, accessor.getSessionId());

          }
          // 处理客户端断开请求
          else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            /*
             * 注意：只有成功建立过认证的连接才会触发下线事件
             * 防止未认证成功的连接产生脏数据
             */
            Authentication authentication = (Authentication) accessor.getUser();
            if (authentication != null && authentication.isAuthenticated()) {
              String username = ((SysUserDetails) authentication.getPrincipal()).getUsername();
              log.info("WebSocket连接关闭：用户[{}]", username);

              // 记录用户下线状态
              webSocketService.userDisconnected(username);
            }
          }
        } catch (AuthenticationException ex) {
          // 认证失败时强制关闭连接
          log.error("连接认证失败：{}", ex.getMessage());
          throw ex;
        } catch (Exception ex) {
          // 捕获其他未知异常
          log.error("WebSocket连接处理异常：", ex);
          throw new MessagingException("Connection processing failed");
        }

        return ChannelInterceptor.super.preSend(message, channel);
      }
    });
  }
}
