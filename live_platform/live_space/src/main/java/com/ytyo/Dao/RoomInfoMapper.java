package com.ytyo.Dao;

import com.ytyo.Model.RoomDetails;
import com.ytyo.Utils.DynamicSqlUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface RoomInfoMapper {
    List<RoomDetails.RoomInfo> selectAllRoomInfo();

    int deleteRoomInfoById(long roomId);

    RoomDetails.RoomInfo selectRoomInfoById(long roomId);

    int countRoomInfo(long anchorId, long roomId);


    short getCanLive(long roomId);

    List<RoomDetails.RoomInfo> selectRoomInfoByAnchorIds(List<Long> anchorIds);

    List<RoomDetails.RoomInfo> selectRoomInfoByIds(List<Long> roomIds);

    RoomDetails.RoomInfo selectRoomInfoByAnchorId(long anchorId);

    int countRoomIdAnchorId(long roomId, long anchorId);

    @UpdateProvider(value = DynamicSqlUtil.class, method = "generateUpdateSql")
    int updateRoomInfoById(@Param("model") RoomDetails.RoomInfo roomInfo);

    int countByMap(@Param("fields") Map<String, Object> fields);

    long insertRoomInfo(RoomDetails.RoomInfo roomInfo);

}
