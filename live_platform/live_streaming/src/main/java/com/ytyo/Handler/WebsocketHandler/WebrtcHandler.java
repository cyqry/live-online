package com.ytyo.Handler.WebsocketHandler;


import com.ytyo.Handler.ContextOnLiveHandler;
import com.ytyo.Option.NoneException;
import com.ytyo.Worker.Relay.WebrtcRelay;
import com.ytyo.Worker.RoomWebRtcStoreManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import static com.ytyo.Util.EnterUtil.*;


//我们自定义一个什么类型都处理的处理类,在这里，我们集合所有的Handler
@Component
@Slf4j
public class WebrtcHandler implements WebSocketHandler {

    final
    WebrtcRelay webrtcRelay;


    @Autowired
    List<ContextOnLiveHandler> contextHandlers;

    @Autowired
    RoomWebRtcStoreManager webRtcStoreManager;

    public WebrtcHandler(WebrtcRelay relay) {
        this.webrtcRelay = relay;
    }

    /**
     * 建立连接
     *
     * @param session Session
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            if (contextHandlers != null)
                contextHandlers.forEach(contextOnLiveHandler -> {
                    if (contextOnLiveHandler != null)
                        contextOnLiveHandler.preOpenHandler(session);
                });


            short who = takeWhoOf(session);
            long id = takeIdOf(session);
            long roomId = takeRoomIdOf(session);

            if (who == 0 || who == -1)
                //若为观众或者游客，就注册这个通道
                webrtcRelay.register(session, roomId);

            if (!webRtcStoreManager.saveSession(roomId, who, id, session)) {
                session.close();
                log.error("存session时，房间不存在! id:{} who:{} roomId:{}  关闭!", id, who, roomId);
            }
        } catch (NoneException | IOException e) {
            if (contextHandlers != null)
                contextHandlers.forEach(contextOnLiveHandler -> {
                    if (contextOnLiveHandler != null)
                        contextOnLiveHandler.catchOpenError(e);
                });
        }

        if (contextHandlers != null)
            contextHandlers.forEach(contextOnLiveHandler -> {
                if (contextOnLiveHandler != null)
                    contextOnLiveHandler.postOpenHandler(session, null);
            });
    }


    /**
     * 接收消息
     *
     * @param session Session
     * @param message 消息
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        if (message instanceof TextMessage stringMessage)
            webrtcRelay.relay(session, stringMessage);
    }

    /**
     * 发生错误
     *
     * @param session   Session
     * @param exception 异常
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (exception instanceof EOFException) {
            log.info("与{}的连接发生EOF异常!", takeIdOf(session));
        } else {
            log.error("与{}的连接异常断开", takeIdOf(session), exception);
        }
    }

    /**
     * 关闭连接
     *
     * @param session     Session
     * @param closeStatus 关闭状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {

        if (contextHandlers != null)
            contextHandlers.forEach(contextOnLiveHandler -> {
                if (contextOnLiveHandler != null)
                    contextOnLiveHandler.preCloseHandler(session);
            });

        try {
            webRtcStoreManager.removeMember(session);
            short who = takeWhoOf(session);
            long id = takeIdOf(session);
            if (who == 0 || who == -1) {//说明还注册到了webrtcRelay中
                webrtcRelay.unRegister(session);
            }
            log.info("与{}的连接以{}状态断开", id, closeStatus);


        } catch (IOException e) {
            if (contextHandlers != null)
                contextHandlers.forEach(contextOnLiveHandler -> {
                    if (contextOnLiveHandler != null)
                        contextOnLiveHandler.catchCloseError(e);
                });
        }

        if (contextHandlers != null)
            contextHandlers.forEach(contextOnLiveHandler -> {
                if (contextOnLiveHandler != null)
                    contextOnLiveHandler.postCloseHandler(session, null);
            });
    }

    /**
     * 是否支持发送部分消息
     *
     * @return false
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
