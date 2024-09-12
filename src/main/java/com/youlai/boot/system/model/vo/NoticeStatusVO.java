package com.youlai.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户公告状态VO
 *
 * @author Theo
 * @since 2024-08-28 16:56
 */
@Data
@Schema(description = "用户公告状态VO")
public class NoticeStatusVO {

    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知类型")
    private String noticeTypeLabel;

    @Schema(description = "发布人")
    private String releaseBy;

    @Schema(description = "优先级(0-低 1-中 2-高)")
    private Integer priority;

    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime releaseTime;

    @Schema(description = "是否已读")
    private Integer readStatus;

}
