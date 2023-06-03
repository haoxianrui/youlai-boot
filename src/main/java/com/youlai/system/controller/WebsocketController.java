package com.youlai.system.controller;

import cn.hutool.json.JSONUtil;
import com.youlai.system.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 广播发送消息
     *
     * @param message 消息内容
     */
    @PostMapping("/sendToAll")
    // @SendTo("/topic/all")  // 使用SimpMessagingTemplate发送消息给所有订阅了"/topic/all"目标的客户端
    public Result sendToAll(String message) {
        log.info("【广播消息请求接收】消息：{}", message);
        // 处理接收到的消息逻辑
        // ...

        // 构造要发送的消息内容
        String content = "服务端广播消息： " + message;

        // 使用SimpMessagingTemplate发送消息给所有订阅了"/topic/all"目标的客户端
        messagingTemplate.convertAndSend("/topic/all", content);

        return Result.success("广播消息发送成功");
    }

    /**
     * 点对点发送消息
     *
     * @param userId 用户ID
     */
    @PostMapping("/sendToUser/{userId}")
    public Result sendToUser(@PathVariable Long userId, String message) {
        log.info("【点对点请求接收】用户：{};消息：{}", userId, JSONUtil.toJsonStr(message));
        // 发送主题目的(destination)= /user/{userId}/message
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/message", message);
        return Result.success("点对点消息发送成功");
    }

}
