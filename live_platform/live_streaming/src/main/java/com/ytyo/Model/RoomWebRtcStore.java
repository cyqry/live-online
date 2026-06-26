package com.ytyo.Model;


import com.ytyo.Option.Option;
import com.ytyo.Util.EnterUtil;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


//不要在底层执行任何可能引发不可控操作的方法(如存储类就只执行增删改查)，将一切需要处理的东西抛给上层
//作为底层存储，一定要将 put前的旧值返回给最上层处理；delete的值底层不要处理，delete就行了，处理交给最上层;其实最好不要处理，交给最最上层的调用者处理

//在存储的时候，删除或put方法其实无需传对象引用 以保证删除或替换正确；也可采用版本号机制实现乐观锁，但若时间精度足够，也可采用createtime+updatetime的形式来实现乐观锁。
//若只使用版本号实现乐观锁，那么就要考虑从拿到id到删除这期间，是否有其他的增删操作，若其中出现删除和创建再修改的一系列操作，可能导致版本号与原来一致，那么就又删除错误；所以版本号要配合createtime或者任何一个 在当前字段每次创建 都是唯一 的字段，一起判断，才能保证能正确地 删或替换 的对的数据；使用精度足够的updatetime就没有这个问题
//这里显然就没有实现乐观锁，也懒得实现了；但是数据库操作的时候要注意，一定要带createtime和updatetime，并注意精度丢失问题。
public class RoomWebRtcStore {
    private WebSocketSession anchorSession;
    private final Map<Long, WebSocketSession> userSessionMap = new ConcurrentHashMap<>();
    private final Map<Long, WebSocketSession> visitorSessionMap = new ConcurrentHashMap<>();
    private final long roomId;
    private final Lock writeLock;
    private final Lock readLock;

    public RoomWebRtcStore(long roomId) {
        this.roomId = roomId;
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.writeLock = lock.writeLock();
        this.readLock = lock.readLock();
    }

    public Option<WebSocketSession> getAnchorSession() {
        readLock.lock();
        readLock.unlock();
        return Option.from(anchorSession);
    }

    public Option<WebSocketSession> saveSession(short who, long id, WebSocketSession session) {
        WebSocketSession oldSession;
        writeLock.lock();
        try {
            if (who == 1) {
                System.out.println("主播添加连接！");
                oldSession = anchorSession;
                anchorSession = session;
            } else {
                System.out.println("观众添加连接！");
                Map<Long, WebSocketSession> map = chooseMap(who);
                oldSession = map.put(id, session);
            }
        } finally {
            writeLock.unlock();
        }

        return Option.from(oldSession);
    }

    public void removeSession(WebSocketSession session) {
        long id = EnterUtil.takeIdOf(session);
        short who = EnterUtil.takeWhoOf(session);
        writeLock.lock();
        try {
            if (who == 1) {
                if (anchorSession == session) {
                    anchorSession = null;
                }
            } else {
                Map<Long, WebSocketSession> map = chooseMap(who);
                WebSocketSession removeSession = map.get(id);
                if (removeSession == session) {
                    map.remove(id);
                }
            }
        } finally {
            writeLock.unlock();
        }

    }

    public Option<WebSocketSession> getSession(short who, long id) {
        WebSocketSession result = null;
        readLock.lock();
        try {
            if (who == 1) {
                if (anchorSession != null && anchorSession.isOpen()) {
                    System.out.println("找到了主播");
                    result = anchorSession;
                }
            } else {
                System.out.println("不是找的主播1");
                Map<Long, WebSocketSession> map = chooseMap(who);
                result = map.get(id);
            }
        } finally {
            readLock.unlock();
        }

        return Option.from(result);
    }


    //只能由被丢弃的Room单线程的调用
    public void closeAll() throws IOException {
        rangeClose((short) 1);
        rangeClose((short) 0);
        rangeClose((short) -1);
    }

    private void rangeClose(short who) throws IOException {
        if (who == 1) {
            if (anchorSession != null && anchorSession.isOpen()) {
                anchorSession.close();
            }
        } else {
            Map<Long, WebSocketSession> map = chooseMap(who);
            for (WebSocketSession session : map.values()) {
                if (session != null && session.isOpen())
                    session.close();
            }
        }
    }

    public long getRoomId() {
        return roomId;
    }


    private Map<Long, WebSocketSession> chooseMap(short who) {
        return switch (who) {
            case 0 -> userSessionMap;
            case -1 -> visitorSessionMap;
            default -> throw new RuntimeException("错误的Who");
        };
    }

}
