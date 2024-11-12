package com.youlai.boot.shared.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统消息体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    /**
     * 发送者
     */
    private String sender;

    /**
     * 消息内容
     */
    private String content;

}
