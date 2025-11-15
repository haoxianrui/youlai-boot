package com.youlai.boot.config;

import cn.hutool.core.util.StrUtil;
<<<<<<< HEAD
import com.youlai.boot.core.security.model.SysUserDetails;
import com.youlai.boot.core.security.token.TokenManager;
import com.youlai.boot.system.service.UserOnlineService;
import lombok.RequiredArgsConstructor;
=======
import com.youlai.boot.security.model.SysUserDetails;
import com.youlai.boot.security.token.TokenManager;
import com.youlai.boot.system.service.WebSocketService;
>>>>>>> 95412501fc69777ad7db6fef970b479c9651984d
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
<<<<<<< HEAD
 * WebSocket配置
 *
 * @author You Lai
=======
 * WebSocket 配置类
 * 
 * 核心功能：
 * - 配置 WebSocket 端点
 * - 配置消息代理
 * - 实现连接认证与授权
 * - 管理用户会话生命周期
 *
 * @author Ray.Hao
>>>>>>> 95412501fc69777ad7db6fef970b479c9651984d
 * @since 3.0.0
 */
@EnableWebSocketMessageBroker
@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

<<<<<<< HEAD
    private final TokenManager tokenManager;
    private final UserOnlineService userOnlineService;
=======
    private static final String WS_ENDPOINT = "/ws";
    private static final String APP_DESTINATION_PREFIX = "/app";
    private static final String USER_DESTINATION_PREFIX = "/user";
    private static final String[] BROKER_DESTINATIONS = {"/topic", "/queue"};

    private final TokenManager tokenManager;
    private final WebSocketService webSocketService;

    public WebSocketConfig(TokenManager tokenManager, @Lazy WebSocketService webSocketService) {
        this.tokenManager = tokenManager;
        this.webSocketService = webSocketService;
        log.info("✓ WebSocket 配置已加载");
    }
>>>>>>> 95412501fc69777ad7db6fef970b479c9651984d

    /**
     * 注册 STOMP 端点
     * 
     * 客户端通过该端点建立 WebSocket 连接
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
<<<<<<< HEAD
                // 注册 /ws 的端点
                .addEndpoint("/ws")
                // 允许跨域
                .setAllowedOriginPatterns("*")
                // 开启SockJS支持，用于不支持WebSocket的浏览器
                .withSockJS();
    }
=======
                .addEndpoint(WS_ENDPOINT)
                .setAllowedOriginPatterns("*"); // 允许跨域（生产环境建议配置具体域名）
>>>>>>> 95412501fc69777ad7db6fef970b479c9651984d

        log.info("✓ STOMP 端点已注册: {}", WS_ENDPOINT);
    }

    /**
     * 配置消息代理
     * 
     * - /app 前缀：客户端发送消息到服务端的前缀
     * - /topic 前缀：用于广播消息
     * - /queue 前缀：用于点对点消息
     * - /user 前缀：服务端发送给特定用户的消息前缀
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 客户端发送消息的请求前缀
        registry.setApplicationDestinationPrefixes(APP_DESTINATION_PREFIX);

<<<<<<< HEAD
        // 客户端订阅消息的请求前缀，topic一般用于广播推送，queue用于点对点推送
        registry.enableSimpleBroker("/topic", "/queue");
        
        // 服务端通知客户端的前缀，可以不设置，默认为user
        registry.setUserDestinationPrefix("/user");
    }
=======
        // 启用简单消息代理，处理 /topic 和 /queue 前缀的消息
        registry.enableSimpleBroker(BROKER_DESTINATIONS);

        // 服务端通知客户端的前缀
        registry.setUserDestinationPrefix(USER_DESTINATION_PREFIX);
>>>>>>> 95412501fc69777ad7db6fef970b479c9651984d

        log.info("✓ 消息代理已配置: app={}, broker={}, user={}",
                APP_DESTINATION_PREFIX, BROKER_DESTINATIONS, USER_DESTINATION_PREFIX);
    }

    /**
     * 配置客户端入站通道拦截器
     * 
     * 核心功能：
     * 1. 连接建立时：解析 JWT Token 并绑定用户身份
     * 2. 连接关闭时：触发用户下线通知
     * 3. 安全防护：拦截无效连接请求
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // 防御性检查：确保 accessor 不为空
                if (accessor == null) {
                    log.warn("⚠ 收到异常消息：无法获取 StompHeaderAccessor");
                    return ChannelInterceptor.super.preSend(message, channel);
                }

                StompCommand command = accessor.getCommand();
                if (command == null) {
                    return ChannelInterceptor.super.preSend(message, channel);
                }

                try {
<<<<<<< HEAD
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
                        userOnlineService.userConnected(username, accessor.getSessionId());

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
                            userOnlineService.userDisconnected(username);
                        }
=======
                    switch (command) {
                        case CONNECT:
                            handleConnect(accessor);
                            break;

                        case DISCONNECT:
                            handleDisconnect(accessor);
                            break;

                        case SUBSCRIBE:
                            handleSubscribe(accessor);
                            break;

                        default:
                            // 其他命令不需要特殊处理
                            break;
>>>>>>> 95412501fc69777ad7db6fef970b479c9651984d
                    }
                } catch (AuthenticationException ex) {
                    // 认证失败时强制关闭连接
                    log.error("❌ 连接认证失败: {}", ex.getMessage());
                    throw ex;
                } catch (Exception ex) {
                    // 捕获其他未知异常
                    log.error("❌ WebSocket 消息处理异常", ex);
                    throw new MessagingException("消息处理失败: " + ex.getMessage());
                }

                return ChannelInterceptor.super.preSend(message, channel);
            }
        });

        log.info("✓ 客户端入站通道拦截器已配置");
    }
<<<<<<< HEAD
=======

    /**
     * 处理客户端连接请求
     * 
     * 安全校验流程：
     * 1. 提取 Authorization 头
     * 2. 验证 Bearer Token 格式
     * 3. 解析并验证 JWT 有效性
     * 4. 绑定用户身份到当前会话
     * 5. 记录用户上线状态
     */
    private void handleConnect(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

        // 安全检查：确保 Authorization 头存在且格式正确
        if (StrUtil.isBlank(authorization)) {
            log.warn("⚠ 非法连接请求：缺少 Authorization 头");
            throw new AuthenticationCredentialsNotFoundException("缺少 Authorization 头");
        }

        if (!authorization.startsWith("Bearer ")) {
            log.warn("⚠ 非法连接请求：Authorization 头格式错误");
            throw new BadCredentialsException("Authorization 头格式错误");
        }

        // 提取 JWT Token（移除 "Bearer " 前缀）
        String token = authorization.substring(7);

        if (StrUtil.isBlank(token)) {
            log.warn("⚠ 非法连接请求：Token 为空");
            throw new BadCredentialsException("Token 为空");
        }

        // 解析并验证 Token
        Authentication authentication;
        try {
            authentication = tokenManager.parseToken(token);
        } catch (Exception ex) {
            log.error("❌ Token 解析失败", ex);
            throw new BadCredentialsException("Token 无效: " + ex.getMessage());
        }

        // 验证解析结果
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("⚠ Token 解析失败：认证对象无效");
            throw new BadCredentialsException("Token 解析失败");
        }

        // 获取用户详细信息
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof SysUserDetails)) {
            log.error("❌ 无效的用户凭证类型: {}", principal.getClass().getName());
            throw new BadCredentialsException("用户凭证类型错误");
        }

        SysUserDetails userDetails = (SysUserDetails) principal;
        String username = userDetails.getUsername();

        if (StrUtil.isBlank(username)) {
            log.warn("⚠ 用户名为空");
            throw new BadCredentialsException("用户名为空");
        }

        // 绑定用户身份到当前会话（重要：用于 @SendToUser 等注解）
        accessor.setUser(authentication);

        // 获取会话 ID
        String sessionId = accessor.getSessionId();
        if (sessionId == null) {
            log.warn("⚠ 会话 ID 为空，使用临时 ID");
            sessionId = "temp-" + System.nanoTime();
        }

        // 记录用户上线状态
        try {
            webSocketService.userConnected(username, sessionId);
            log.info("✓ WebSocket 连接建立成功: 用户[{}], 会话[{}]", username, sessionId);
        } catch (Exception ex) {
            log.error("❌ 记录用户上线状态失败: 用户[{}], 会话[{}]", username, sessionId, ex);
            // 不抛出异常，允许连接继续
        }
    }

    /**
     * 处理客户端断开连接事件
     * 
     * 注意：
     * - 只有成功建立过认证的连接才会触发下线事件
     * - 防止未认证成功的连接产生脏数据
     */
    private void handleDisconnect(StompHeaderAccessor accessor) {
        Authentication authentication = (Authentication) accessor.getUser();

        // 防御性检查：只处理已认证的连接
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("未认证的连接断开，跳过处理");
            return;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof SysUserDetails)) {
            log.warn("⚠ 断开连接时用户凭证类型异常");
            return;
        }

        SysUserDetails userDetails = (SysUserDetails) principal;
        String username = userDetails.getUsername();

        if (StrUtil.isNotBlank(username)) {
            try {
                webSocketService.userDisconnected(username);
                log.info("✓ WebSocket 连接断开: 用户[{}]", username);
            } catch (Exception ex) {
                log.error("❌ 记录用户下线状态失败: 用户[{}]", username, ex);
            }
        }
    }

    /**
     * 处理客户端订阅事件（可选）
     * 
     * 用于记录订阅信息或实施订阅级别的权限控制
     */
    private void handleSubscribe(StompHeaderAccessor accessor) {
        Authentication authentication = (Authentication) accessor.getUser();

        if (authentication != null && authentication.isAuthenticated()) {
            String destination = accessor.getDestination();
            String username = authentication.getName();

            log.debug("用户[{}]订阅主题: {}", username, destination);

            // TODO: 这里可以实现订阅级别的权限控制
            // 例如：检查用户是否有权限订阅某个主题
        }
    }
>>>>>>> 95412501fc69777ad7db6fef970b479c9651984d
}
