package com.youlai.boot.platform.websocket.service;

import com.youlai.boot.common.enums.MessageTypeEnum;
import com.youlai.boot.system.model.dto.MessageDTO;

/**
 * 消息服务接口
 *
 * @author Theo
 * @since 2024-9-2 14:32:58
 */
public interface MessageService {


    /**
     * 检查消息类型
     *
     * @param messageType 消息类型
     * @return 是否支持
     */
    boolean check(MessageTypeEnum messageType);

    /**
     * 发送消息
     *
     * @param message 消息
     */
    void sendMessage(MessageDTO message);
}
