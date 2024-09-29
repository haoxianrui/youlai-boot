package com.youlai.boot.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户公告VO
 *
 * @author Theo
 * @since 2024-08-28 16:56
 */
@Data
@Schema(description = "用户公告VO")
public class UserUnreadNoticeVO {

    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "通知标题")
    private String title;

}
