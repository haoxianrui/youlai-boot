package com.youlai.system.controller;

import com.youlai.system.model.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * WebSocket 测试控制层
 *
 * @author Ray
 * @since 2.3.0
 */
@RestController
@RequestMapping("/websocket")
@RequiredArgsConstructor
@Slf4j
public class WebsocketController {

    private final SimpMessagingTemplate messagingTemplate;


    /**
     * 广播发送消息
     *
     * @param message 消息内容
     */
    @MessageMapping("/sendToAll")
    @SendTo("/topic/notice")
    public String sendToAll(String message) {
        return "服务端通知: " + message;
    }

    /**
     * 点对点发送消息
     * <p>
     * 模拟 张三 给 李四 发送消息场景
     *
     * @param principal 当前用户
     * @param username  接收消息的用户
     * @param message   消息内容
     */
    @MessageMapping("/sendToUser/{username}")
    public void sendToUser(Principal principal, @DestinationVariable String username, String message) {

        String sender = principal.getName(); // 发送人
        String receiver = username; // 接收人

        log.info("发送人:{}; 接收人:{}", sender, receiver);
        // 发送消息给指定用户，拼接后路径 /user/{receiver}/queue/greeting
        messagingTemplate.convertAndSendToUser(receiver, "/queue/greeting", new ChatMessage(sender, message));
    }

}
