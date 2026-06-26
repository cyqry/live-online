package com.ytyo.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

public class Report {

    Long anchorId;
    String userNickname;
    Long userId;

    @NotNull
    Long roomId;

    @NotBlank
    String reason;

    @NotBlank
    String detail;

    @Size(max = 3)
    String[] screenshotPaths;

    @NotBlank
    String contactWay;

    public Report() {
    }

    public Report(ReportExtension reportExtension) {
        this.contactWay = reportExtension.getContactWay();
        this.detail = reportExtension.getDetail();
        this.reason = reportExtension.getReason();
        this.roomId = reportExtension.getRoomId();
        this.userId = reportExtension.getUserId();
        this.screenshotPaths = reportExtension.getScreenshotPaths();
        this.userNickname = reportExtension.getUserNickname();
        this.anchorId = reportExtension.getAnchorId();
    }
}
