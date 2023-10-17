package com.youlai.system.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket 消息体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocketMessage {

    /**
     * 发送者
     */
    private String sender;

    /**
     * 消息内容
     */
    private String content;

}
