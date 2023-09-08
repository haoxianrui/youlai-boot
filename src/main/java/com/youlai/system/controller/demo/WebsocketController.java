package com.youlai.system.controller.demo;

import com.youlai.system.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * WebSocket 测试控制器
 *
 * @author haoxr
 * @since 2.3.0
 */
@RestController
@RequestMapping("/websocket")
@RequiredArgsConstructor
@Slf4j
public class WebsocketController {

    /**
     * 广播发送消息
     *
     * @param message 消息内容
     */
    @MessageMapping("/sendToAll")
    @SendTo("/topic/all")
    public String sendToAll(String message) {
        // 处理消息
        return "Hello, " + message + "!";
    }

    /**
     * 点对点发送消息
     */
    // 处理发送到"/app/sendToUser/{username}"的消息
    @MessageMapping("/sendToUser/{username}")
    // 将消息处理器的返回值发送到指定用户
    @SendTo("/queue/user")
    public String sendToUser(@DestinationVariable("username") String username, String message) {
        // 处理消息
        return "Hello, " + username + ", your message is: " + message;
    }

}
