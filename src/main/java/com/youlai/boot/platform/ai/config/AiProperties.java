package com.youlai.boot.platform.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AI 配置属性
 * 
 * 优势：
 * 1. 统一管理所有提供商配置
 * 2. 添加新提供商只需在 yml 中添加配置，无需修改代码
 * 3. 类型安全，支持 IDE 提示
 *
 * @author Ray.Hao
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    /**
     * 是否启用 AI 功能
     */
    private Boolean enabled = false;

    /**
     * 当前使用的提供商（qwen、deepseek、openai 等）
     */
    private String provider = "qwen";

    /**
     * 所有提供商的配置
     * Key: 提供商名称（qwen、deepseek、openai）
     * Value: 提供商配置
     */
    private Map<String, ProviderConfig> providers;

    /**
     * 安全配置
     */
    private SecurityConfig security = new SecurityConfig();

    /**
     * 限流配置
     */
    private RateLimitConfig rateLimit = new RateLimitConfig();

    /**
     * 提供商配置
     */
    @Data
    public static class ProviderConfig {
        /**
         * API Key
         */
        private String apiKey;

        /**
         * Base URL（统一命名，符合行业惯例）
         */
        private String baseUrl;

        /**
         * 模型名称
         */
        private String model;

        /**
         * 提供商显示名称（可选）
         */
        private String displayName;

        /**
         * 超时时间（秒）
         */
        private Integer timeout = 30;
    }

    /**
     * 安全配置
     */
    @Data
    public static class SecurityConfig {
        private Boolean enableAudit = true;
        private Boolean dangerousOperationsConfirm = true;
        private java.util.List<String> functionWhitelist;
        private java.util.List<String> sensitiveParams;
    }

    /**
     * 限流配置
     */
    @Data
    public static class RateLimitConfig {
        private Integer maxExecutionsPerMinute = 10;
        private Integer maxExecutionsPerDay = 100;
    }

    /**
     * 获取当前提供商配置
     */
    public ProviderConfig getCurrentProviderConfig() {
        if (providers == null || !providers.containsKey(provider)) {
            throw new IllegalStateException("未找到提供商配置: " + provider);
        }
        return providers.get(provider);
    }
}


