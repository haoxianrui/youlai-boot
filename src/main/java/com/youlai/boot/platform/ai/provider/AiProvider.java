package com.youlai.boot.platform.ai.provider;

/**
 * AI 提供商接口
 * 
 * 策略模式：不同提供商实现各自的调用逻辑
 *
 * @author Ray.Hao
 */
public interface AiProvider {

    /**
     * 调用 AI API
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return AI 响应内容
     */
    String call(String systemPrompt, String userPrompt);

    /**
     * 获取提供商名称
     */
    String getProviderName();

    /**
     * 检查配置是否有效
     */
    boolean isConfigValid();
}


