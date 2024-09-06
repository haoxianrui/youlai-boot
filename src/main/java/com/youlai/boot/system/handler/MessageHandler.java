package com.youlai.boot.system.handler;

import com.youlai.boot.platform.websocket.service.MessageService;
import com.youlai.boot.system.model.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息处理器
 *
 * @author Theo
 * @since 2024-9-2 14:32:58
 */
@Component
@RequiredArgsConstructor
public class MessageHandler {

    private final List<MessageService> messageServices;


    /**
     * 发送消息
     * 如果后面有多种消息发送方式，可以设置MessageDTO中的noticeWay，调用不同的消息发送方式，实现消息多种发送方式
     * @param messageDTO 消息载体
     */
    public void sendMessage(MessageDTO messageDTO) {
        messageServices.forEach(messageService -> {
            if (messageService.check(messageDTO.getNoticeWay())) {
                messageService.sendMessage(messageDTO);
            }
        });
    }
}
