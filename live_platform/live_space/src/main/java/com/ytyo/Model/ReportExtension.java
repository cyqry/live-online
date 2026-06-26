package com.ytyo.Model;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportExtension extends Report {

    @Size(max = 3)
    String[] screenshotBase64s;

    String anchorNickname;

    public ReportExtension() {
    }

    public ReportExtension(Report report) {
        this.contactWay = report.getContactWay();
        this.detail = report.getDetail();
        this.reason = report.getDetail();
        this.roomId = report.getRoomId();
        this.userId = report.getUserId();
        this.screenshotPaths = report.getScreenshotPaths();
        this.userNickname = report.getUserNickname();
        this.anchorId = report.getAnchorId();
    }
}
