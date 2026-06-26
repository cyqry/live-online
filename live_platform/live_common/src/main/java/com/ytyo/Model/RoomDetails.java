package com.ytyo.Model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


//这整个，是存redis的，由Space管理
@Data
@NoArgsConstructor
@ToString
public class RoomDetails {

    private CopyOnWriteArrayList<Long> userList;
    private CopyOnWriteArrayList<Long> visitorList;

    @NotNull
    private RoomInfo roomInfo;
    /**
     * 房间封面图片，path(年月/图片名)
     */
    @NotNull
    String coverSrc;

    public RoomDetails(RoomInfo roomInfo, String coverSrc) {
        this.roomInfo = roomInfo;
        this.coverSrc = coverSrc;
        visitorList = new CopyOnWriteArrayList<>();
        userList = new CopyOnWriteArrayList<>();
    }

    public void setUserList(List<Long> userList) {
        if (userList instanceof CopyOnWriteArrayList<Long> u)
            this.userList = u;
        else {
            this.userList = userList == null ? null : new CopyOnWriteArrayList<>(userList);
        }
    }

    public void setVisitorList(List<Long> visitorList) {
        if (visitorList instanceof CopyOnWriteArrayList<Long> u)
            this.visitorList = u;
        else {
            this.visitorList = visitorList == null ? null : new CopyOnWriteArrayList<>(userList);
        }
    }

    /**
     * @param who -1： 游客 0: 已登录用户
     * @return id: 已经加入房间,删了重新加入；-1：新加入房间
     */
    public long addMember(long id, short who) {
        List<Long> list = choosedList(who);
        if (list.contains(id)) {
            list.remove(id);
            list.add(id);
            return id;
        }
        list.add(id);
        return -1;
    }

    //返回旧的id，若没有返回-1
    public long deleteMember(long id, short who) {
        List<Long> list = choosedList(who);
        long result = -1;
        if (list.contains(id)) {
            result = id;
        }
        list.remove(id);
        return result;
    }

    public List<Long> getMemberList(short who) {
        List<Long> list = choosedList(who);
        return List.copyOf(list);
    }

    private List<Long> choosedList(short who) {
        return switch (who) {
            case 0 -> userList;
            case -1 -> visitorList;
            default -> throw new RuntimeException("who参数错误!");
        };
    }

    public RoomInfo getRoomInfo() {
        return roomInfo;
    }

    /**
     * RoomInfo mysql部分
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoomInfo {
        private Long id;
        /**
         * 存客户端进入主播直播间的路径,而不是开播路径
         */
        String src;

        Short canLive;

        Long anchorId;
        /**
         * 最后一次直播的时间，在开播和下播的时候更新;RoomInfo只有这个字段要更新，该字段无需与缓存同步
         */
        LocalDateTime lastLiveTime;
        LocalDateTime createTime;
        LocalDateTime updateTime;
    }
}
