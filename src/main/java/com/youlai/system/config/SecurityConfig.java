package com.youlai.system.config;

import com.youlai.system.common.constant.SecurityConstants;
import com.youlai.system.filter.JwtAuthenticationFilter;
import com.youlai.system.security.exception.MyAccessDeniedHandler;
import com.youlai.system.security.exception.MyAuthenticationEntryPoint;
import com.youlai.system.security.JwtTokenManager;
import com.youlai.system.filter.VerifyCodeFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 权限配置
 *
 * @author haoxr
 * @since 2023/2/17
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MyAuthenticationEntryPoint authenticationEntryPoint;
    private final MyAccessDeniedHandler accessDeniedHandler;
    private final JwtTokenManager jwtTokenManager;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requestMatcherRegistry ->
                        requestMatcherRegistry.requestMatchers(SecurityConstants.LOGIN_PATH).permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler)
                )
                .csrf(AbstractHttpConfigurer::disable)

        ;

        // 验证码校验过滤器
        http.addFilterBefore(new VerifyCodeFilter(),UsernamePasswordAuthenticationFilter.class);
        // JWT 校验过滤器
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenManager), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 不走过滤器链的放行配置
     *
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/api/v1/auth/captcha",
                        "/webjars/**",
                        "/doc.html",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/ws/**"
                );
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 无法直接注入 AuthenticationManager
     *
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
