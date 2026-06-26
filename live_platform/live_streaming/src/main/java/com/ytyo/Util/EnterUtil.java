package com.ytyo.Util;

import com.ytyo.Model.User;
import com.ytyo.Option.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.util.Objects;

@Slf4j
public class EnterUtil {

    public static Option<String> handleUri(URI uri, int pathIndex) {
        if (pathIndex < 0) {
            System.out.println("？？？");
            return Option.None();
        }
        //   uri.getPath() 会以 / 开头,所以split后第一位为空串
        String[] split = uri.getPath().split("/");
        if (split.length >= pathIndex + 2) {
            return Option.from(split[pathIndex + 1]);
        }
        return Option.None();
    }

    public static long takeIdOf(WebSocketSession session) {
        if (Objects.isNull(session)) {
            throw new IllegalArgumentException("session is null!");
        }
        Object id = session.getAttributes().get("id");
        if (Objects.nonNull(id)) {
            return Long.parseLong(id.toString());
        } else {
            throw new RuntimeException("不可能");
        }
    }

    public static short takeWhoOf(WebSocketSession session) {
        if (Objects.isNull(session)) {
            throw new IllegalArgumentException("session is null");
        }
        Object who = session.getAttributes().get("who");
        if (Objects.nonNull(who)) {
            return (Short) who;
        } else {
            throw new RuntimeException("不可能");
        }
    }

    public static Option<User> takeUserOf(WebSocketSession session) {
        if (session == null) {
            return Option.None();
        }
        Object user = session.getAttributes().get("user");
        if (user == null) {
            return Option.None();
        } else if (user instanceof User u) {
            return Option.Some(u);
        } else {
            return Option.None();
        }
    }

    /**
     * @param session
     * @return
     */
    public static long takeRoomIdOf(WebSocketSession session) {
        if (Objects.isNull(session)) {
            throw new IllegalArgumentException("session is null!");
        }
        Object roomId = session.getAttributes().get("roomId");
        if (Objects.nonNull(roomId)) {
            return Integer.parseInt(roomId.toString());
        } else {
            throw new RuntimeException("不可能");
        }
    }
}
