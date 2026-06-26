package com.ytyo.Service;

import com.ytyo.Model.Report;
import com.ytyo.Option.Option;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ReportService {

    private static final Map<Long, List<Report>> reportMap = new ConcurrentHashMap<>();

    public boolean report(Report report) {
        if (report == null || report.getRoomId() == null)
            return false;
        if (reportMap.get(report.getRoomId()) == null) {
            reportMap.put(report.getRoomId(), new CopyOnWriteArrayList<>());
        }
        reportMap.get(report.getRoomId()).add(report);
        return true;
    }

    public Map<Long, List<Report>> getOverThresholdRoomReports(int threshold) {
        HashMap<Long, List<Report>> map = new HashMap<>();
        reportMap.forEach((key, value) -> {
            if (value != null && value.size() >= threshold) {
                map.put(key, value);
            }
        });
        return map;
    }


    public List<Report> getRoomReportsByThreshold(Long roomId, int threshold) {
        if (roomId == null) {
            return new ArrayList<>();
        }
        List<Report> reports = reportMap.get(roomId);
        if (reports == null) {
            return new ArrayList<>();
        }
        return reports.size() < threshold ? new ArrayList<>() : reports;
    }

    public List<Report> getRoomReports(Long roomId) {
        if (roomId == null) {
            return new ArrayList<>();
        }
        List<Report> reports = reportMap.get(roomId);
        return reports == null ? new ArrayList<>() : reports;
    }

    public Option<Report> getRoomReportByUserId(Long roomId, Long userId) {
        if (roomId == null || userId == null) {
            return Option.None();
        }
        List<Report> reports = reportMap.get(roomId);
        if (reports == null) {
            return Option.None();
        }
        for (Report report : reports) {
            if (report != null && Objects.equals(report.getUserId(), userId)) {
                return Option.Some(report);
            }
        }
        return Option.None();
    }

    public void deleteRoomReport(long roomId) {

        reportMap.remove(roomId);

    }

    public boolean deleteRoomReport(Long roomId, Long userId) {
        if (roomId == null || userId == null) {
            return false;
        }
        List<Report> reports = reportMap.get(roomId);
        if (reports == null) {
            return false;
        }
        return reports.removeIf((report -> Objects.equals(report.getUserId(), userId)));
    }
}
