package com.youlai.boot.platform.ai.provider;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.youlai.boot.platform.ai.config.AiProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * OpenAI 兼容协议的抽象提供商
 * 
 * 适用于：通义千问、DeepSeek、OpenAI、ChatGLM 等兼容 OpenAI API 的模型
 *
 * @author Ray.Hao
 */
@Slf4j
public abstract class AbstractOpenAiCompatibleProvider implements AiProvider {

    protected final AiProperties.ProviderConfig config;

    public AbstractOpenAiCompatibleProvider(AiProperties.ProviderConfig config) {
        this.config = config;
    }

    @Override
    public String call(String systemPrompt, String userPrompt) {
        if (!isConfigValid()) {
            throw new IllegalStateException(getProviderName() + " 配置无效");
        }

        try {
            // 构建请求体（OpenAI 标准格式）
            JSONObject requestBody = JSONUtil.createObj()
                    .set("model", config.getModel())
                    .set("messages", JSONUtil.createArray()
                            .put(JSONUtil.createObj()
                                    .set("role", "system")
                                    .set("content", systemPrompt))
                            .put(JSONUtil.createObj()
                                    .set("role", "user")
                                    .set("content", userPrompt))
                    )
                    .set("temperature", 0.7);

            log.info("📤 调用 {} API: {}/chat/completions", getProviderName(), config.getBaseUrl());
            log.debug("请求参数: {}", requestBody);

            // 发送 HTTP 请求
            HttpResponse response = HttpRequest.post(config.getBaseUrl() + "/chat/completions")
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .timeout((int) TimeUnit.SECONDS.toMillis(config.getTimeout()))
                    .execute();

            // 检查响应状态
            if (!response.isOk()) {
                String errorMsg = String.format("%s API 调用失败: HTTP %d - %s",
                        getProviderName(), response.getStatus(), response.body());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            // 解析响应
            JSONObject responseJson = JSONUtil.parseObj(response.body());
            String content = responseJson.getByPath("choices[0].message.content", String.class);

            // 记录 Token 使用情况
            JSONObject usage = responseJson.getJSONObject("usage");
            if (usage != null) {
                Integer inputTokens = usage.getInt("prompt_tokens");
                Integer outputTokens = usage.getInt("completion_tokens");
                Integer totalTokens = usage.getInt("total_tokens");
                log.info("✅ {} 响应成功，tokens: 输入={}, 输出={}, 总计={}",
                        getProviderName(), inputTokens, outputTokens, totalTokens);
            }

            log.debug("📥 {} 返回内容: {}", getProviderName(), content);
            return content;

        } catch (Exception e) {
            String errorMsg = String.format("%s API 调用失败: %s", getProviderName(), e.getMessage());
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public boolean isConfigValid() {
        return config != null
                && StrUtil.isNotBlank(config.getApiKey())
                && StrUtil.isNotBlank(config.getBaseUrl())
                && StrUtil.isNotBlank(config.getModel());
    }
}


