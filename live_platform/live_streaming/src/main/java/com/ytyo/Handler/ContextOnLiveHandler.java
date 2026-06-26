package com.ytyo.Handler;

import org.springframework.web.socket.WebSocketSession;

public interface ContextOnLiveHandler {
    default void preOpenHandler(WebSocketSession session) {
    }

    default void postOpenHandler(WebSocketSession session, Object attachment) {
    }

    default void preCloseHandler(WebSocketSession session) {

    }

    default void postCloseHandler(WebSocketSession session, Object attachment) {
    }

    default void catchCloseError(Throwable e) {
    }

    default void catchOpenError(Throwable e) {
    }
}
