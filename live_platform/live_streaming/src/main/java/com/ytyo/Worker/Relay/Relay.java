package com.ytyo.Worker.Relay;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public interface Relay {
    void relay(WebSocketSession session, WebSocketMessage<?> message) throws Exception;
}
