package com.youlai.boot.platform.ai.provider;

import com.youlai.boot.platform.ai.config.AiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AI 提供商工厂
 * 
 * 职责：根据配置获取对应的提供商实例
 *
 * @author Ray.Hao
 */
@Component
@RequiredArgsConstructor
public class AiProviderFactory {

    private final AiProperties aiProperties;
    
    /**
     * Spring 自动注入所有 AiProvider 实现类
     * Key: Bean 名称（qwen、deepseek、openai）
     * Value: 提供商实例
     */
    private final Map<String, AiProvider> providers;

    /**
     * 获取当前配置的提供商
     */
    public AiProvider getCurrentProvider() {
        String providerName = aiProperties.getProvider();
        
        if (!providers.containsKey(providerName)) {
            throw new IllegalStateException("不支持的 AI 提供商: " + providerName 
                    + "，可用提供商: " + providers.keySet());
        }
        
        AiProvider provider = providers.get(providerName);
        
        if (!provider.isConfigValid()) {
            throw new IllegalStateException(provider.getProviderName() 
                    + " 配置无效，请检查 API Key、Base URL 和 Model 是否配置");
        }
        
        return provider;
    }
}


