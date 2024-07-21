package com.youlai.system.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author haoxr
 * @since 2024/4/18
 */
@Data
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * 白名单 URL 集合
     */
    private List<String> ignoreUrls;

    /**
     * JWT 配置
     */
    private JwtProperty jwt;


    /**
     * JWT 配置
     */
    @Data
    public static class JwtProperty {

        /**
         * JWT 密钥
         */
        private String key;

        /**
         * JWT 过期时间
         */
        private Long ttl;

    }
}
