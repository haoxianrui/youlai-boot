package com.youlai.system.service.impl;

import com.youlai.system.service.WebsocketService;
import groovy.lang.Lazy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebsocketServiceImpl implements WebsocketService {

    @Lazy
    @Autowired
    private  SimpMessagingTemplate messagingTemplate;

    private static final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    @Override
    public void addUser(String username) {
        onlineUsers.add(username);
    }

    @Override
    public void removeUser(String username) {
        onlineUsers.remove(username);
    }

    @Override
    public int getOnlineUserCount() {
        return onlineUsers.size();
    }

    @Scheduled(fixedRate = 5000)
    public void sendOnlineUserCount() {
        int onlineUserCount = this.getOnlineUserCount();
        messagingTemplate.convertAndSend("/topic/onlineUserCount", onlineUserCount);
    }

}
