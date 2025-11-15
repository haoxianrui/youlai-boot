package com.youlai.boot.platform.ai.provider.impl;

import com.youlai.boot.platform.ai.config.AiProperties;
import com.youlai.boot.platform.ai.provider.AbstractOpenAiCompatibleProvider;
import org.springframework.stereotype.Component;

/**
 * OpenAI 提供商（GPT-4、GPT-3.5 等）
 * 
 * 添加新提供商只需：
 * 1. 继承 AbstractOpenAiCompatibleProvider
 * 2. 实现 getProviderName()
 * 3. 在配置文件中添加配置
 *
 * @author Ray.Hao
 */
@Component("openai")
public class OpenAiProvider extends AbstractOpenAiCompatibleProvider {

    public OpenAiProvider(AiProperties aiProperties) {
        super(aiProperties.getProviders().get("openai"));
    }

    @Override
    public String getProviderName() {
        return config.getDisplayName() != null ? config.getDisplayName() : "OpenAI";
    }
}


