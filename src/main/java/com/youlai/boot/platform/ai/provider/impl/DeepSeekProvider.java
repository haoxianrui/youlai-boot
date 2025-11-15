package com.youlai.boot.platform.ai.provider.impl;

import com.youlai.boot.platform.ai.config.AiProperties;
import com.youlai.boot.platform.ai.provider.AbstractOpenAiCompatibleProvider;
import org.springframework.stereotype.Component;

/**
 * DeepSeek 提供商
 *
 * @author Ray.Hao
 */
@Component("deepseek")
public class DeepSeekProvider extends AbstractOpenAiCompatibleProvider {

    public DeepSeekProvider(AiProperties aiProperties) {
        super(aiProperties.getProviders().get("deepseek"));
    }

    @Override
    public String getProviderName() {
        return config.getDisplayName() != null ? config.getDisplayName() : "DeepSeek";
    }
}


