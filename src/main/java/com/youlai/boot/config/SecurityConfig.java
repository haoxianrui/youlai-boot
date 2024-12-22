package com.youlai.boot.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.collection.CollectionUtil;
import com.youlai.boot.common.enums.RequestMethodEnum;
import com.youlai.boot.common.util.AnonymousUtils;
import com.youlai.boot.config.property.SecurityProperties;
import com.youlai.boot.core.filter.RateLimiterFilter;
import com.youlai.boot.core.security.exception.MyAccessDeniedHandler;
import com.youlai.boot.core.security.exception.MyAuthenticationEntryPoint;
import com.youlai.boot.core.security.extension.WechatAuthenticationProvider;
import com.youlai.boot.core.security.filter.CaptchaValidationFilter;
import com.youlai.boot.core.security.filter.JwtAuthenticationFilter;
import com.youlai.boot.core.security.service.SysUserDetailsService;
import com.youlai.boot.shared.auth.service.impl.JwtTokenService;
import com.youlai.boot.system.service.ConfigService;
import com.youlai.boot.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;
import java.util.Set;

/**
 * Spring Security 安全配置
 *
 * @author Ray.Hao
 * @since 2023/2/17
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenService jwtTokenService;
    private final WxMaService wxMaService;
    private final UserService userService;
    private final SysUserDetailsService userDetailsService;

    private final CodeGenerator codeGenerator;
    private final SecurityProperties securityProperties;
    private final ConfigService configService;

    private final MyAuthenticationEntryPoint authenticationEntryPoint; // 项目内安全类
    private final MyAccessDeniedHandler accessDeniedHandler;

    private final ApplicationContext applicationContext;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 获取所有匿名访问路径
        Map<String, Set<String>> anonymousUrls = AnonymousUtils.getAllAnonymousUrls(applicationContext);

        http

                .authorizeHttpRequests(requestMatcherRegistry ->
                        requestMatcherRegistry
                                // GET
                                .requestMatchers(HttpMethod.GET, anonymousUrls.get(RequestMethodEnum.GET.getType()).toArray(new String[0])).permitAll()
                                // POST
                                .requestMatchers(HttpMethod.POST, anonymousUrls.get(RequestMethodEnum.POST.getType()).toArray(new String[0])).permitAll()
                                // PUT
                                .requestMatchers(HttpMethod.PUT, anonymousUrls.get(RequestMethodEnum.PUT.getType()).toArray(new String[0])).permitAll()
                                // PATCH
                                .requestMatchers(HttpMethod.PATCH, anonymousUrls.get(RequestMethodEnum.PATCH.getType()).toArray(new String[0])).permitAll()
                                // DELETE
                                .requestMatchers(HttpMethod.DELETE, anonymousUrls.get(RequestMethodEnum.DELETE.getType()).toArray(new String[0])).permitAll()
                                // 所有类型的接口都放行
                                .requestMatchers(anonymousUrls.get(RequestMethodEnum.ALL.getType()).toArray(new String[0])).permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                // 限流过滤器
                .addFilterBefore(new RateLimiterFilter(redisTemplate, configService), UsernamePasswordAuthenticationFilter.class)
                // 验证码校验过滤器
                .addFilterBefore(new CaptchaValidationFilter(redisTemplate, codeGenerator), UsernamePasswordAuthenticationFilter.class)
                // JWT 验证和解析过滤器
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 不走过滤器链的放行配置
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            if (CollectionUtil.isNotEmpty(securityProperties.getIgnoreUrls())) {
                web.ignoring().requestMatchers(securityProperties.getIgnoreUrls().toArray(new String[0]));
            }
        };
    }

    /**
     * 默认密码认证的 Provider
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        return daoAuthenticationProvider;
    }

    /**
     * 微信认证 Provider
     */
    @Bean
    public WechatAuthenticationProvider weChatAuthenticationProvider() {
        return new WechatAuthenticationProvider(userService, wxMaService);
    }

    /**
     * 手动注入 AuthenticationManager，支持多种认证方式
     * - DaoAuthenticationProvider：用户名密码认证
     * - WeChatAuthenticationProvider：微信认证
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(daoAuthenticationProvider(), weChatAuthenticationProvider());
    }
}
