package com.youlai.system.model.query;

import com.youlai.system.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知公告分页查询对象
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 */
@Schema(description ="通知公告查询对象")
@Getter
@Setter
public class NoticeQuery extends BasePageQuery {

    private static final long serialVersionUID = 1L;

    @Schema(description = "通知标题")
    private String title;
    @Schema(description = "通知内容")
    private String content;
    @Schema(description = "通知类型")
    private Integer noticeType;
    @Schema(description = "发布人")
    private Long releaseBy;
    @Schema(description = "优先级(0-低 1-中 2-高)")
    private Integer priority;
    @Schema(description = "目标类型(0-全体 1-指定)")
    private Integer tarType;
    @Schema(description = "发布状态(0-未发布 1已发布 2已撤回)")
    private Integer sendStatus;
    @Schema(description = "发布时间")
    private List<String> sendTime;
    @Schema(description = "撤回时间")
    private List<String> recallTime;
}
