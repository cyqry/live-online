package com.ytyo.Worker;

import com.ytyo.Model.RoomWebRtcStore;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Util.EnterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@Slf4j
public class RoomWebRtcStoreManager {

    private final List<RoomWebRtcStore> roomWebRtcStoreList = new CopyOnWriteArrayList<>();

    private final Lock writeLock;
    private final Lock readLock;

    public RoomWebRtcStoreManager() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.writeLock = lock.writeLock();
        this.readLock = lock.readLock();
    }


//    /**
//     * 根据 已有的房间信息 创建在线房间 并加入管理
//     * @param roomId
//     * @return
//     */
//    public Option<RoomWebRtcStore> createRoom(long roomId) {
//        try {
//            Option<RoomWebRtcStore> roomInfo = liveServiceApi.getRoomInfo(roomId);
//            RoomWebRtcStore.RoomInfo info = roomInfo.unwrap();
//            RoomWebRtcStore roomWebRtcStore = new RoomWebRtcStore(info);
//            if (liveServiceApi.online(roomId) && addRoom(roomWebRtcStore)) {
//                return Option.Some(roomWebRtcStore);
//            } else {
//                log.error("房间上线失败!");
//                return Option.None();
//            }
//        } catch (NoneException e) {
//            log.info("未找到该房间!");
//            return Option.None();
//        }
//    }

    /**
     * 根据 创建在线webrtc连接的房间 并加入管理
     *
     * @param roomId
     * @return
     */

    public Option<RoomWebRtcStore> createRoom(long roomId) {
        //房间存在
        RoomWebRtcStore webRtcStore = new RoomWebRtcStore(roomId);
        if (addRoom(webRtcStore)) {
            return Option.Some(webRtcStore);
        } else {
            log.error("session房间已经存在!");
            return Option.None();
        }


    }

    public boolean addRoom(RoomWebRtcStore roomWebRtcStore) {
        writeLock.lock();

        boolean contain = roomWebRtcStoreList.stream().anyMatch(roomStore -> roomWebRtcStore.getRoomId() == roomStore.getRoomId());
        if (contain) {
            log.info("session房间已经存在了！");
            writeLock.unlock();
            return false;
        }
        roomWebRtcStoreList.add(roomWebRtcStore);
        writeLock.unlock();
        return true;
    }

    public void removeRoomByAnchorSession(WebSocketSession webSocketSession) {
        if (webSocketSession == null) {
            return;
        }
        AtomicReference<RoomWebRtcStore> roomResult = new AtomicReference<>(null);

        writeLock.lock();
        for (RoomWebRtcStore rtcStore : roomWebRtcStoreList) {
            try {
                if (rtcStore.getAnchorSession().unwrap() == webSocketSession) {
                    roomResult.set(rtcStore);
                }
            } catch (NoneException ignore) {
            }
        }
        if (roomResult.get() != null) {
            roomWebRtcStoreList.remove(roomResult.get());
        }
        writeLock.unlock();
        try {
            if (roomResult.get() != null) {
                roomResult.get().closeAll();
            }
        } catch (IOException e) {
            log.error("清理连接时出现io异常", e);
        }
    }

    public void removeRoom(RoomWebRtcStore webRtcStore) {
        if (webRtcStore == null) {
            return;
        }
        writeLock.lock();
        roomWebRtcStoreList.remove(webRtcStore);
        writeLock.unlock();
        try {
            webRtcStore.closeAll();
        } catch (IOException e) {
            log.error("清理连接时出现io异常", e);
        }
    }

    public Option<RoomWebRtcStore> getRoomWebRtcStoreByAnchorSession(WebSocketSession anchorSession) {
        AtomicReference<RoomWebRtcStore> roomResult = new AtomicReference<>(null);
        readLock.lock();
        roomWebRtcStoreList.forEach(roomWebRtcStore -> {
                    try {
                        if (Objects.equals(anchorSession, roomWebRtcStore.getAnchorSession().unwrap())) {
                            roomResult.set(roomWebRtcStore);
                        }
                    } catch (NoneException ignore) {
                    }
                }
        );
        readLock.unlock();
        return Option.from(roomResult.get());
    }

    public Option<RoomWebRtcStore> getRoomWebRtcStore(long roomId) {
        AtomicReference<RoomWebRtcStore> roomResult = new AtomicReference<>(null);
        readLock.lock();
        roomWebRtcStoreList.forEach(roomWebRtcStore -> {
                    if (Objects.equals(roomId, roomWebRtcStore.getRoomId())) {
                        roomResult.set(roomWebRtcStore);
                    }
                }
        );
        readLock.unlock();
        return Option.from(roomResult.get());
    }

    public boolean saveSession(long roomId, short who, long id, WebSocketSession webSocketSession) throws NoneException, IOException {
        RoomWebRtcStore roomWebRtcStore = null;
        writeLock.lock();
        for (RoomWebRtcStore webRtcStore : roomWebRtcStoreList) {
            if (webRtcStore.getRoomId() == roomId) {
                roomWebRtcStore = webRtcStore;
                break;
            }
        }
        if (roomWebRtcStore == null) {
            log.error("本地房间都不存在还添加连接");
            writeLock.unlock();
            return false;
        }
        System.out.println("存: " + roomWebRtcStore);
        Option<WebSocketSession> oldSession = roomWebRtcStore.saveSession(who, id, webSocketSession);
        writeLock.unlock();
        if (oldSession.isSome()) {
            if (oldSession.unwrap().isOpen())
                oldSession.unwrap().close();
        }
        return true;
    }

    public void removeMember(WebSocketSession webSocketSession) throws IOException {
        long roomId = EnterUtil.takeRoomIdOf(webSocketSession);
        writeLock.lock();
        RoomWebRtcStore roomWebRtcStore = null;
        for (RoomWebRtcStore webRtcStore : roomWebRtcStoreList) {
            if (webRtcStore.getRoomId() == roomId) {
                roomWebRtcStore = webRtcStore;
                break;
            }
        }
        if (roomWebRtcStore == null) {
            log.info("本地房间都不存在还删除");
        } else {
            roomWebRtcStore.removeSession(webSocketSession);
        }
        writeLock.unlock();
        if (webSocketSession.isOpen())
            webSocketSession.close();
    }

    public Option<WebSocketSession> getMember(long roomId, short who, long id) throws NoneException {
        readLock.lock();
        Option<RoomWebRtcStore> rtcStore = getRoomWebRtcStore(roomId);
        System.out.println("找room:" + roomId + "|||" + rtcStore.unwrap());
        if (rtcStore.isNone()) {
            log.error("房间都找不到还找member");
            readLock.unlock();
            return Option.None();
        }
        RoomWebRtcStore store = rtcStore.unwrap();
        Option<WebSocketSession> session = store.getSession(who, id);
        readLock.unlock();
        return session;
    }

}
