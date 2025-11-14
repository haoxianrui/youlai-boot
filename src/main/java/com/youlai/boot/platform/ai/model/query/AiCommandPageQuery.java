package com.youlai.boot.platform.ai.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * AI命令记录分页查询对象
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Schema(description = "AI命令记录分页查询对象")
@Getter
@Setter
public class AiCommandPageQuery extends BasePageQuery {

    @Schema(description = "关键字(原始命令/函数名称/用户名)")
    private String keywords;

    @Schema(description = "执行状态(pending-待执行, success-成功, failed-失败)")
    private String executeStatus;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "是否危险操作")
    private Boolean isDangerous;

    @Schema(description = "创建时间范围")
    private List<String> createTime;

    @Schema(description = "函数名称")
    private String functionName;
}

