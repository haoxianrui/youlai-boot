package com.youlai.system.model.query;

import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 通知公告分页查询对象
 *
 * @author youlaitech
 * @since 2024-08-08 11:46
 */
@Schema(description ="通知公告查询对象")
@Getter
@Setter
public class NoticeQuery extends BasePageQuery {

    private static final long serialVersionUID = 1L;
    private Long id;
    @Schema(description = "通知标题")
    private String title;
    @Schema(description = "通知内容")
    private String content;
    @Schema(description = "通知类型")
    private Integer noticeType;
    @Schema(description = "发布人")
    private Long release;
    @Schema(description = "优先级(0-低 1-中 2-高)")
    private Integer priority;
    @Schema(description = "目标类型(0-全体 1-指定)")
    private Integer tarType;
    @Schema(description = "发布状态(0-未发布 1已发布 2已撤回)")
    private Integer sendStatus;
    @Schema(description = "发布时间")
    private LocalDateTime sendTime;
    @Schema(description = "撤回时间")
    private LocalDateTime recallTime;
    @Schema(description = "创建人ID")
    private Long createBy;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新人ID")
    private Long updateBy;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "逻辑删除标识(0-未删除 1-已删除)")
    private Integer isDelete;
}
