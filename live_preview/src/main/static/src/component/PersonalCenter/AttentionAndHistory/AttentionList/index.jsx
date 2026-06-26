import React from 'react';
import { Container, Typography } from '@mui/material';
import Header from '../Header';
import RoomCardList from '../RoomCardList';
import { useEffect } from 'react';
import { getFollowInfo } from '../../../../Api/spaceApi';
import { useState } from 'react';
import { getImage } from '../../../../Api/storeApi';
import { base64ToSrcOrDefault, getDataFromErrorOrDefault, log } from '../../../../Common/util';
import nofollow from "../../../../static/img/nofollow.webp"
// const rooms = [
//     {
//         avatar: 'https://avatar.example.com/1.jpg',
//         nickname: '订阅的主播1',
//         latestStream: {
//             date: '2023-04-10',
//             title: '直播主题1',
//         },
//         latestWatchDate: '2023-04-16'
//     },
//     {
//         avatar: 'https://avatar.example.com/2.jpg',
//         nickname: '订阅的主播2',
//         latestStream: {
//             date: '2023-04-12',
//             title: '直播主题2',
//         },
//         latestWatchDate: '2023-04-15'
//     },
//     {
//         avatar: 'https://avatar.example.com/2.jpg',
//         nickname: '订阅的主播2',
//         latestStream: {
//             date: '2023-04-12',
//             title: '直播主题2',
//         },
//         latestWatchDate: '2023-04-15'
//     },
//     // ...其他订阅
// ];


const AttentionList = () => {
    let [{ avatarMap, tag }, setAvatarMap] = useState({ avatarMap: undefined, tag: 0 })
    const [followInfos, setFollowInfos] = useState([])

    useEffect(() => {
        avatarMap = new Map()
        getFollowInfo(5).then(
            (followInfosData) => {
                followInfosData.forEach(followInfo => {
                    if (!avatarMap.get(followInfo.roomId))
                        getImage(followInfo.avatarUrl).then(
                            (data) => {
                                avatarMap.set(followInfo.roomId, base64ToSrcOrDefault(data))
                            },
                            (e) => {
                            }
                        ).finally(() => {
                            setAvatarMap({ avatarMap, tag: tag + 1 })
                        })
                });
                setFollowInfos(followInfosData)
            },
            (e) => {
                console.log(getDataFromErrorOrDefault(e, "未登录"))
            }
        )
        setAvatarMap({ avatarMap, tag: tag + 1 })
    }, [])

    let rooms = followInfos.map(f => { return { attentions: f.attentions, id: f.roomId, lastLiveTime: f.lastLiveTime, liveTitle: f.liveTitle, anchorNickname: f.anchorNickname, online: f.online, domain: f.domain }; })

    log("receive:", rooms)

    return (
        <>
            <Typography variant="h6" gutterBottom>
                我的订阅详情
            </Typography>
            {
                rooms.length == 0 ?
                    <div style={{
                        margin: '0px auto',
                        width: "30%", height: "300px"
                    }} >
                        <img src={nofollow}></img>
                        <div style={{
                            height: '20px',
                            fontSize: '12px',
                            textAlign: 'center',
                            lineHeight: '20px',
                            marginTop: '10px',
                        }}>
                            <p style={{ color: '#999' }}>你还没有关注的主播哦~</p>
                        </div>
                    </div> :
                    <RoomCardList style={{ paddingTop: "10px", paddingLeft: "25px" }} rooms={rooms} avatarMap={avatarMap} />
            }
        </>
    );
};

export default AttentionList;
