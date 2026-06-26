import { Card, CircularProgress } from "@mui/material";
import { height as heightUtil } from "../../../../Common/util";
import LiveHistoryList from "./LiveHistoryList";
import { getUserHistory } from "../../../../Api/spaceApi";
import { useState } from "react";
import { useEffect } from "react";
import { useNotification } from "../../../NotificationProvider";



// const liveHistories = [
//     // 示例数据，实际应用中请从 API 或其他数据源获取
//     {
//         title: '直播1',
//         date: '2023-04-10',
//         duration: '1h 30m',
//         liveTitle: '直播1',
//         domain: "三国杀",
//         anchor: {
//             name: '主播1',
//             avatarUrl: 'https://example.com/avatar1.jpg',
//             online: true,
//         },
//     },
//     {
//         date: '2023-04-11',
//         duration: '2h 15m',
//         liveTitle: '直播2',
//         domain: "王者荣耀",
//         anchor: {
//             name: '主播2',
//             avatarUrl: 'https://example.com/avatar2.jpg',
//             online: false,
//         },
//     }
//     // ... 更多直播历史记录
// ];

const HistoryList = ({ liveHistories, avatarMap, maxHeight = heightUtil(32), minHeight = heightUtil(32), style, onMouseLeave, onMouseEnter }) => {


    return (
        !liveHistories ?
            <></>
            :
            <LiveHistoryList style={style} maxHeight={maxHeight} minHeight={minHeight} avatarMap={avatarMap} liveHistories={liveHistories} onMouseEnter={onMouseEnter} onMouseLeave={onMouseLeave} />
    )
}
export default HistoryList;