package com.youlai.boot.platform.ai.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * AI命令解析日志分页查询对象
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Schema(description = "AI命令解析日志分页查询对象")
@Getter
@Setter
public class AiParseLogPageQuery extends BasePageQuery {

    @Schema(description = "关键字(原始命令/用户名)")
    private String keywords;

    @Schema(description = "解析是否成功(0-失败, 1-成功)")
    private Boolean parseSuccess;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "AI提供商(qwen/openai/deepseek/gemini等)")
    private String provider;

    @Schema(description = "AI模型(qwen-plus/qwen-max/gpt-4-turbo等)")
    private String model;

    @Schema(description = "创建时间范围")
    private List<String> createTime;
}

