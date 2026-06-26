package com.ytyo.Dao;

import com.ytyo.Model.Operating.OperatingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
@Repository
public interface OperatingRecordMapper {
    int insertOperatingRecord(long userId, String operation);

//    int updateOperatingRecordDisplay(long userId,short display);

    List<OperatingRecord> selectOperatingRecord(long userId);

    List<OperatingRecord> selectOperatingRecordByTimeQuantumUserId(long userId, LocalDateTime start, LocalDateTime end);

    List<OperatingRecord> selectDisplayOperatingRecord(long userId);


    int updateDisplayOperatingRecord(short display,long id,long userId);
}
