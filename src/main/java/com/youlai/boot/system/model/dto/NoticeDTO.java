package com.youlai.boot.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 通知传送对象
 *
 * @author Theo
 * @since 2024-9-2 14:32:58
 */
@Data
public class NoticeDTO {

    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "通知类型")
    private Integer type;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知时间")
    private LocalDateTime publishTime;


}
