package com.youlai.system.listener.websocket;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

/**
 * Websocket 客户端事件监听器
 *
 * @author haoxr
 * @since 2023/10/10
 */
@Component
@Slf4j
public class WebSocketEventListener {

    /**
     * 监听客户端连接事件
     *
     * @param event 连接事件对象
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("客户端连接成功");
    }

    /**
     * 监听客户端断开连接事件
     *
     * @param event 断开连接事件对象
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("客户端断开连接");
    }


    /**
     * 监听客户端订阅事件
     *
     * @param event 订阅事件对象
     */
    @EventListener
    public void handleSubscription(SessionSubscribeEvent event) {
        log.info("客户端订阅：{}", JSONUtil.toJsonStr(event.getMessage()));
    }

    /**
     * 监听客户端取消订阅事件
     *
     * @param event 取消订阅事件对象
     */
    @EventListener
    public void handleUnSubscription(SessionUnsubscribeEvent event) {
        log.info("客户端取消订阅：{}", JSONUtil.toJsonStr(event.getMessage()));
    }


}
