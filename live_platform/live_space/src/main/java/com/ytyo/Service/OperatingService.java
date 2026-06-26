package com.ytyo.Service;

import com.ytyo.Dao.OperatingRecordMapper;
import com.ytyo.Model.Operating.OperatingRecord;
import com.ytyo.Option.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperatingService {
    @Autowired
    OperatingRecordMapper operatingRecordMapper;

    public boolean generateOperatingRecord(OperatingRecord operatingRecord) {
        if (operatingRecord == null || operatingRecord.getUserId() == null || operatingRecord.getOperation() == null)
            return false;
        return operatingRecordMapper.insertOperatingRecord(operatingRecord.getUserId(), operatingRecord.getOperation()) > 0;
    }

    public Option<List<OperatingRecord>> getOperatingRecordsByUserId(Long userId) {
        if (userId == null) {
            return Option.None();
        }
        List<OperatingRecord> records = operatingRecordMapper.selectOperatingRecord(userId);
        return Option.from(records);
    }


    public Option<List<OperatingRecord>> getDisplayOperatingRecordsByUserId(Long userId) {
        if (userId == null) {
            return Option.None();
        }
        List<OperatingRecord> records = operatingRecordMapper.selectDisplayOperatingRecord(userId);
        return Option.from(records);
    }

    public boolean hiddenOperating(Long recordId, Long userId) {
        if (recordId == null || userId == null) {
            return false;
        }
        return operatingRecordMapper.updateDisplayOperatingRecord((short) 0, recordId, userId) > 0;

    }
}
