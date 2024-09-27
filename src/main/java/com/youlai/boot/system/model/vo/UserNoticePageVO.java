package com.youlai.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户公告VO
 *
 * @author Theo
 * @since 2024-08-28 16:56
 */
@Data
@Schema(description = "用户公告VO")
public class UserNoticePageVO {

    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知类型")
    private String typeLabel;

    @Schema(description = "发布人姓名")
    private String publisherName;

    @Schema(description = "通知级别")
    private String levelLabel;

    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    @Schema(description = "是否已读")
    private String isReadLabel;

}
