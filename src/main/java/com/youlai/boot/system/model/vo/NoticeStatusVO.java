package com.youlai.boot.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户公告状态VO
 *
 * @author Theo
 * @since 2024-08-28 16:56
 */
@Data
@Schema(description = "用户公告状态VO")
public class NoticeStatusVO {

    @Schema(description = "公告ID")
    private Long id;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "是否已读")
    private Integer readStatus;

}
