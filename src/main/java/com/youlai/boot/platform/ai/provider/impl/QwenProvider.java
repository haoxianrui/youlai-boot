package com.youlai.boot.platform.ai.provider.impl;

import com.youlai.boot.platform.ai.config.AiProperties;
import com.youlai.boot.platform.ai.provider.AbstractOpenAiCompatibleProvider;
import org.springframework.stereotype.Component;

/**
 * 阿里通义千问提供商
 *
 * @author Ray.Hao
 */
@Component("qwen")
public class QwenProvider extends AbstractOpenAiCompatibleProvider {

    public QwenProvider(AiProperties aiProperties) {
        super(aiProperties.getProviders().get("qwen"));
    }

    @Override
    public String getProviderName() {
        return config.getDisplayName() != null ? config.getDisplayName() : "阿里通义千问";
    }
}


