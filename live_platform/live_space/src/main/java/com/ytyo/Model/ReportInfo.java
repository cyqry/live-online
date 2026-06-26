package com.ytyo.Model;

import java.util.List;

public record ReportInfo(Long roomId,String avatar ,String anchorNickname, List<Report> reports) {

}
