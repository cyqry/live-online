import { Fragment } from "react"
import RoomCard from "../RoomCard"
import TimelineItem from "./TimelineItem"
import { Stack } from "@mui/material"
import RoomCardList from "../RoomCardList"
import { useEffect } from "react"
import { useState } from "react"
import { getUserHistory } from "../../../../Api/spaceApi"
import { getImage } from "../../../../Api/storeApi"
import { base64ToSrcOrDefault, convertDateTimeToDate } from "../../../../Common/util"
import { useNotification } from "../../../NotificationProvider"


const HistoryList = () => {
    let { show } = useNotification()
    let [liveHistories, setLiveHistories] = useState([])
    let [{ avatarMap, tag }, setAvatarMap] = useState({ avatarMap: undefined, tag: 0 })

    useEffect(() => {
        avatarMap = new Map()
        getUserHistory().then(
            (liveHistoriesData) => {
                liveHistoriesData.forEach(data => {
                    if (!avatarMap.get(data.roomId))
                        getImage(data.avatarUrl).then(
                            (base64) => {
                                avatarMap.set(data.roomId, base64ToSrcOrDefault(base64))
                            },
                            (e) => {
                            }
                        ).finally(() => {
                            setAvatarMap({ avatarMap, tag: tag + 1 })
                        })
                });
                setLiveHistories(liveHistoriesData)
            },
            (e) => {
                console.log('未登录')
            }
        ).catch(e => {
            show("网络错误", 'error', 2000)
        }).finally(() => {
            setAvatarMap({ avatarMap, tag: tag + 1 })
        })
    }, [])

    let historyDateRoomsMap = {}
    liveHistories.map(l => {
        let rooms = historyDateRoomsMap['' + convertDateTimeToDate(l.lastWatchTime)]
        let room = { id: l.roomId, anchorNickname: l.anchorNickname, liveTitle: l.liveTitle, lastLiveTime: l.lastLiveTime, domain: l.domain }
        if (!rooms) {
            historyDateRoomsMap['' + convertDateTimeToDate(l.lastWatchTime)] = [room]
        } else {
            rooms.push(room)
        }
    })

    return (
        <Stack direction={"column"} pt={"20px"} >

            {
                Object.entries(historyDateRoomsMap).length == 0 ?
                    <div style={{
                        paddingTop: "15%",
                        margin: '0px auto',
                        width: "30%", height: "300px"
                    }} >
                        <div style={{
                            height: '20px',
                            fontSize: '12px',
                            textAlign: 'center',
                            lineHeight: '20px',
                            marginTop: '10px',
                        }}>
                            <p style={{ color: '#999' }}>最近还没有观看记录~</p>
                        </div>
                    </div>
                    : Object.entries(historyDateRoomsMap).map(([lastWatchTime, rooms], index) => {
                        return (
                            <Fragment key={index}>
                                <TimelineItem date={lastWatchTime} />
                                <RoomCardList style={{ paddingTop: "20px", paddingLeft: "25px" }} rooms={rooms} avatarMap={avatarMap} model="history" />
                            </Fragment>
                        )
                    })

            }
        </Stack>
    )
}
export default HistoryList;