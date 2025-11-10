package com.youlai.boot.platform.ai.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * AI 命令解析响应 DTO
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCommandResponseDTO {

    /**
     * 是否成功解析
     */
    private Boolean success;

    /**
     * 解析后的函数调用列表
     */
    private List<FunctionCallDTO> functionCalls;

    /**
     * AI 的理解和说明
     */
    private String explanation;

    /**
     * 置信度 (0-1)
     */
    private Double confidence;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 原始 LLM 响应（用于调试）
     */
    private String rawResponse;
}



