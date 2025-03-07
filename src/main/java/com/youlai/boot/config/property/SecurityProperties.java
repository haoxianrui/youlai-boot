package com.youlai.boot.config.property;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全配置属性
 *
 * @author Ray.Hao
 * @since 2024/4/18
 */
@Data
@Validated
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * 免认证请求路径白名单
     */
    private List<String> ignoreUrls = new ArrayList<>();

    /**
     * 静态资源路径（不经过安全过滤器）
     */
    private List<String> unsecuredUrls = new ArrayList<>();

    /**
     * 认证核心配置
     */
    private Auth auth = new Auth();

    @Data
    public static class Auth {
        /**
         * 认证策略类型
         */
        @NotNull
        private AuthType type = AuthType.JWT;

        /**
         * 访问令牌有效期（秒）
         */
        @Min(-1)
        private int accessTokenTtl = 3600;

        /**
         * 刷新令牌有效期（秒）
         */
        @Min(-1)
        private int refreshTokenTtl = 604800;

        /**
         * JWT 配置
         */
        private JwtConfig jwtConfig = new JwtConfig();

        /**
         * Redis Token 配置
         */
        private RedisTokenConfig redisTokenConfig = new RedisTokenConfig();

        @Data
        public static class JwtConfig {
            /**
             * JWT 密钥
             */
            @NotBlank
            @Size(min = 32, message = "HS256算法密钥至少需要32字符")
            private String key;
        }

        @Data
        public static class RedisTokenConfig {
            /**
             * 最大并发会话数
             */
            @Min(-1)
            private int maxSessions = 1;

            /**
             * 会话超限处理策略
             */
            private SessionControlStrategy sessionControl = SessionControlStrategy.REVOKE_OLDEST;
        }
    }

    /**
     * 认证策略类型枚举
     */
    public enum AuthType {
        JWT, REDIS_TOKEN
    }

    /**
     * 会话控制策略枚举
     */
    public enum SessionControlStrategy {
        REVOKE_OLDEST, DENY_NEW
    }
}
