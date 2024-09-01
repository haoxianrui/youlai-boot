package com.youlai.boot.system.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 通知公告业务对象
 *
 * @author Theo
 * @since 2024-09-01 10:31
 * @version 1.0.0
 */
@Data
public class NoticeBO {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知类型")
    private Integer noticeType;

    @Schema(description = "发布人")
    private String releaseBy;

    @Schema(description = "优先级(0-低 1-中 2-高)")
    private Integer priority;

    @Schema(description = "目标类型(0-全体 1-指定)")
    private Integer tarType;

    @Schema(description = "发布状态(0-未发布 1已发布 2已撤回)")
    private Integer releaseStatus;

    @Schema(description = "发布时间")
    private LocalDateTime releaseTime;

    @Schema(description = "撤回时间")
    private LocalDateTime recallTime;
}
