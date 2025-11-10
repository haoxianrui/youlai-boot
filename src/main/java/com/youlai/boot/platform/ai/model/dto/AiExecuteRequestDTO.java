package com.youlai.boot.platform.ai.model.dto;

import lombok.Data;

/**
 * AI 命令执行请求 DTO
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Data
public class AiExecuteRequestDTO {

    /**
     * 要执行的函数调用
     */
    private FunctionCallDTO functionCall;

    /**
     * 确认模式：auto=自动执行, manual=需要用户确认
     */
    private String confirmMode;

    /**
     * 用户确认标志
     */
    private Boolean userConfirmed;

    /**
     * 幂等性令牌（防止重复执行）
     */
    private String idempotencyKey;
}



