package com.ytyo.Worker.Relay;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Util.EnterUtil;
import com.ytyo.Worker.RoomWebRtcStoreManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.ytyo.CommonConst.GlobalTool.mapper;


@Component
@Slf4j
public class WebrtcRelay implements Relay {

    @Autowired
    RoomWebRtcStoreManager webRtcStoreManager;

    //存观众Id和roomId
    private final ConcurrentHashMap<WebSocketSession, Long> map = new ConcurrentHashMap<>();

    @Override
    public void relay(WebSocketSession session, WebSocketMessage<?> message) throws NoneException {
        if (!(message instanceof TextMessage stringMessage)) {
            throw new UnsupportedOperationException("不支持的参数类型!");
        }
        long id = EnterUtil.takeIdOf(session);
        short who = EnterUtil.takeWhoOf(session);
        Long targetIdRes = null; //用于记录日志
        try {
            //先看看你这个session是否有已注册的roomId了,有就说明是观众的webrtc连接发过来的,那么就直接转发给主播;如果不存在对应的target roomId,那就说明是主播发过来的消息，那么就根据消息判断是否有target user,如果都没有，那么就输出错误，不进行转发。
            Long targetRoomId = map.get(session);
            if (Objects.nonNull(targetRoomId)) {//说明一定是已登录用户或者游客
                targetIdRes = targetRoomId;
                //找到这个id的房间的webrtcSession 即主播的webrtcSession，发给他消息，并带有观众id和who字段
                Option<WebSocketSession> targetSession = webRtcStoreManager.getMember(targetRoomId, (short) 1, id);

                //若该房间存在,则send
                if (targetSession.isSome()) {
                    targetSession.unwrap().sendMessage(new TextMessage(mapper.writeValueAsString(new Letter<>(id, who, message.getPayload()))));
                } else {//即房间已经关闭或者主播创建了房间而没播
                    log.info("{}尝试转发给{}房间的webrtc消息失败!房间已关闭或主播不在线!", id, targetRoomId);
                }
            } else {//是主播
                Option<Letter<String>> targetLetterOp = parse(stringMessage);
                if (targetLetterOp.isSome()) {
                    Letter<String> targetLetter = targetLetterOp.unwrap();
                    long targetId = targetLetter.getTargetId();
                    targetIdRes = targetId;
                    short targetWho = targetLetter.getTargetWho();
                    long roomId = EnterUtil.takeRoomIdOf(session);
                    //找到这个id和who的观众,发给他消息
                    Option<WebSocketSession> targetSession = webRtcStoreManager.getMember(roomId, targetWho, targetId);
                    if (targetSession.isSome()) {
                        targetSession.unwrap().sendMessage(new TextMessage(targetLetter.getMsg()));
                    } else {//没找到说明观众下线，那就不发。
                        log.info("没找到观众{},应该是下线了....", targetId);
                    }
                } else {
                    //说明既不是发给房间主播的，也不是发给观众的，那么就很神奇
                    log.error("转发消息没有目标!");
                }
            }

        } catch (IOException e) {
            log.error("{}转发给{}消息IO异常!", id, targetIdRes, e);
        }
    }

    public void register(WebSocketSession session, long roomId) {
        map.put(session, roomId);
    }

    public void unRegister(WebSocketSession session) {
        map.remove(session);
    }

    //用于解析消息以获得其中 target 信息
    public static <T> Option<Letter<T>> parse(TextMessage message) {
        String s = message.getPayload();

        try {
            Letter<T> letter = mapper.readValue(s, new TypeReference<>() {
            });
            //判断是否所有字段是否都被赋值
            if (letter.targetId == Integer.MIN_VALUE || letter.targetWho == Short.MIN_VALUE || letter.msg == null) {
                return Option.None();
            }
            return Option.Some(letter);
        } catch (JsonProcessingException e) {
            log.error("EnterUtil.parse  消息反序列化失败{},消息:{}", e, s);
            return Option.None();
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    //这里面不存type，因为Letter只在webrtc连接里工作，所以type一定为0,查session的时候 type 为 0就行
    public static class Letter<T> implements Serializable {
        private long targetId = Integer.MIN_VALUE;
        private short targetWho = Short.MIN_VALUE;
        private T msg;
    }
}
