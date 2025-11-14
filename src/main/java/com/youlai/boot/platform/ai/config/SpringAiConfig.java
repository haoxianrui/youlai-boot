package com.youlai.boot.platform.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.youlai.boot.platform.ai.tools.UserTools;

/**
 * Spring AI 配置类
 * 
 * 使用 Spring AI 自动配置，支持：
 * - OpenAI
 * - 通义千问（DashScope 兼容 OpenAI 协议）
 * - DeepSeek（兼容 OpenAI 协议）
 * - 其他兼容 OpenAI 协议的模型
 * 
 * 配置方式：
 * spring.ai.openai.api-key: xxx
 * spring.ai.openai.base-url: xxx
 * spring.ai.openai.chat.options.model: xxx
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.openai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAiConfig {

    /**
     * 创建 ChatClient（Spring AI 核心客户端）
     * <p>
     * OpenAiChatModel 由 Spring AI 自动配置创建
     * 根据 spring.ai.openai.* 配置自动初始化
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel, UserTools userTools) {
        log.info("✅ Spring AI ChatClient 初始化成功");
        log.info("📋 当前配置 - 模型: {}", chatModel.getDefaultOptions().getModel());
        // 将 UserTools 注册为默认工具，所有调用都可使用
        return ChatClient.builder(chatModel)
                .defaultTools(userTools)
                .build();
    }
}

