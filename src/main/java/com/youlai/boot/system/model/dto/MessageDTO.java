package com.youlai.boot.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

/**
 * 消息载体
 *
 * @author Theo
 * @since 2024-9-2 14:32:58
 * @version 1.0.0
 */
@Data
public class MessageDTO {

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "发送者")
    private String sender;

    @Schema(description = "接收者")
    private Set<String> receivers;



}
