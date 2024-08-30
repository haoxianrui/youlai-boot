package com.youlai.boot.module.system.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserConnectionEvent extends ApplicationEvent {
    private final String username;
    private final boolean connected;

    public UserConnectionEvent(Object source, String username, boolean connected) {
        super(source);
        this.username = username;
        this.connected = connected;
    }
}
