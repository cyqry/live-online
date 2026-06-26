package com.ytyo.Interceptor;

import com.ytyo.Api.LiveAccountApi;
import com.ytyo.Api.LiveSpaceApi;
import com.ytyo.Model.*;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Utils.RequestUtil;
import com.ytyo.Worker.Relay.WebrtcRelay;
import com.ytyo.Worker.RoomWebRtcStoreManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Objects;

import static com.ytyo.Util.EnterUtil.handleUri;

/**
 * RoomDetails生命周期:  主播点击开播后由开播接口创建RoomDetails, 然后主播再连入这里开播,当主播下播.就由这里来自动删除RoomDetails，
 * RoomDetails生命周期略大于主播Webrtc连接生命周期；不与主播webrtc连接绑定，但包含webrtc连接整个生命周期
 */

//todo 增加token校验
@Component
@Slf4j
public class WebrtcHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    WebrtcRelay webrtcRelay;
    @Autowired
    LiveAccountApi liveAccountApi;

    @Autowired
    LiveSpaceApi liveSpaceApi;
    @Autowired
    RoomWebRtcStoreManager roomWebRtcStoreManager;

    //    ws传给主播webrtc连接消息时，要带发送者id;然后返回时，将id带回;
    //    连接所需格式: ws://path/id
    //    身份识别采用: id+cookie
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        try {
            URI uri = Objects.requireNonNull(request.getURI());
            System.out.println(uri.getPath());
            String path = handleUri(uri, 0).unwrap();
            //当前用户(可能为主播)的id
            long id = Long.parseLong(handleUri(uri, 1).unwrap());

            long roomId = Long.parseLong(handleUri(uri, 2).unwrap());

            //观众一般不会从已经不存在的房间进来;就算这里返回了true。房间却在true传输的途中关闭了，导致这个连接连上了也没有数据，用户刷新即可，无所谓。
            //可能造成房间删了，主播还在播的问题，但无所谓，他刷新就无了
            //查看房间是否存在;
            Option<RoomDetails> roomDetails = syncRoom(roomId);
            if (roomDetails.isNone()) {
                log.info("没有这个房间!");
                response.setStatusCode(HttpStatusCode.valueOf(404));
                return false;
            }
            if (roomDetails.unwrap().getRoomInfo().getCanLive() == 0) {
                log.info("主播被封禁中");
                response.setStatusCode(HttpStatusCode.valueOf(409));
                return false;
            }

            //通过cookie查出id，和id比对，若已登录且id比对上，则返回1或0，若未登录，则返回-1; 否则拒绝连接
            Record o = verifyAndGetUser(id, request);//这里匹配失败会抛异常然后下面捕获
            short who;
            switch (o) {
                case UserModel userModel -> {
                    who = 0;
                    attributes.put("user", userModel.user());
                }
                case AnchorModel anchorModel -> {
                    who = 1;
                    attributes.put("user", anchorModel.user());
                }
                default -> {
                    who = -1;
                }
            }


            attributes.put("id", id);
            attributes.put("roomId", roomId);
            attributes.put("who", who);

            log.info("who:{}.roomId:{},id:{}", who, roomId, id);

            switch (path) {
                case "webrtc_sub" -> {
                    if (who == 1) {// 不是 已登录用户或游客的webrtc连接,而是主播连接
                        //虽然是主播,但是主播的id与其他已登录用户并不重复,所以可以直接当做已登录用户处理
                        who = (short) 0;
                        attributes.put("who", (short) 0);
                    }

                    Option<WebSocketSession> session = roomWebRtcStoreManager.getMember(roomId, (short) 1, id);//找主播的webrtc在不在，因为房间在线(即这里的房间还在)但主播不一定在播
                    if (session.isNone()) {
                        log.info("主播未直播!");
                        return false;
                    }
                    log.info("观众{}进入房间{}!", id, roomId);
                }
                case "webrtc_pub" -> {
                    if (who != 1 || !Objects.equals(roomDetails.unwrap().getRoomInfo().getAnchorId(), id)) {
                        log.warn("非房间主播却想开播");
                        return false;
                    }
                    log.info("主播id: {}开播,房间: {}!", id, roomId);
                }
                default -> {
                    return false;
                }
            }
            log.info("握手成功!");
            return true;
        } catch (
                NumberFormatException e) {
            log.info("路径解析id错误!");
            return false;
        } catch (NoneException |
                 UnsupportedOperationException e) {
            log.error("未知错误:", e);
            return false;
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

    }


    //同步这里的房间，并返回远程的房间
    private Option<RoomDetails> syncRoom(long roomId) throws NoneException, IOException {
        System.out.println("同步！");
        Option<RoomDetails> info = liveSpaceApi.getRoom(roomId);
        if (info.isSome()) {
            Option<RoomWebRtcStore> storeOption = roomWebRtcStoreManager.createRoom(roomId);
            if (storeOption.isSome()) {
                log.info("同步房间创建成功！");
            } else {
                log.info("房间已存在!无需同步创建");
            }
        } else {
            Option<RoomWebRtcStore> roomWebRtcStore = roomWebRtcStoreManager.getRoomWebRtcStore(roomId);
            if (roomWebRtcStore.isSome()) {
                roomWebRtcStoreManager.removeRoom(roomWebRtcStore.unwrap());
                log.info("同步房间删除成功");
            }
        }
        System.out.println("同步完成！");
        return info;
    }

    private Record verifyAndGetUser(long id, ServerHttpRequest request) {
        try {
            Option<User> user = RequestUtil.getUserByReq(request);
            if (user.isNone())
                return new Visitor();
            if (Long.valueOf(id).equals(user.unwrap().getId())) {
                //判断是否为主播
                boolean anchor = liveAccountApi.isAnchor(request).unwrap();
                return anchor ? new AnchorModel(user.unwrap()) : new UserModel(user.unwrap());
            } else {
                return new Visitor();
            }
        } catch (NoneException e) {
            return new Visitor();
        }
    }

    //    握手后
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            exception.printStackTrace();
        }
        System.out.println("afterHandshake");
    }
}

