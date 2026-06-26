import * as React from 'react';
import { Avatar, Box, Paper, Typography } from "@mui/material";

import Stack from "@mui/material/Stack";
import { base64ToSrcOrDefault, getDataFromErrorOrDefault, height, width } from "../../../Common/util";

import Item from "./Item";
import HistoryList from './HistoryList';
import LiveStreamList from './AttentionList';
import HeadShot from './HeadShot';
import { useState } from 'react';
import { useNotification } from '../../NotificationProvider';
import { useEffect } from 'react';
import { getFollowInfo, getRoomInfo, getUserHistory } from '../../../Api/spaceApi';
import { getImage } from '../../../Api/storeApi';
import { getIsAnchor } from '../../../Api/userApi';

export default function UserHappyRight() {
    let style = {
        position: "absolute",
        right: width(16),
        display: "flex"
    };

    let [liveHistories, setLiveHistories] = useState();
    let [followInfos, setFollowInfos] = useState()
    let [avatarMap, setAvatarMap] = useState()


    let { show } = useNotification()
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
                            setAvatarMap(avatarMap)
                        })
                });
                setFollowInfos(followInfosData)
            },
            (e) => {
                console.log(getDataFromErrorOrDefault(e, "未登录"))
            }
        ).finally(() => {
            setAvatarMap(avatarMap)
        })

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
                            setAvatarMap(avatarMap)
                        })
                });
                setLiveHistories(liveHistoriesData)
            },
            (e) => {
                console.log('未登录')
            }
        ).catch(e => {
            show("网络错误", 'error', 2000)
        })
    }, [])

    return (
        <div style={style}>
            <Stack spacing={1} direction={"row"} >

                <Item to="/anchor" d="M940.72 330.86a85.62 85.62 0 0 0-76-13.78l-1.46 0.42-107.5 45.08v-55.54a140.3 140.3 0 0 0-140.14-140.14H212.36a140.3 140.3 0 0 0-140.14 140.14v410a140.3 140.3 0 0 0 140.14 140.14h403.22a140.3 140.3 0 0 0 140.14-140.14v-55.62l107.5 45.08 1.46 0.42a87.66 87.66 0 0 0 24.5 3.54 85.62 85.62 0 0 0 51.54-17.3 83.54 83.54 0 0 0 33.82-67.32V398.16a83.46 83.46 0 0 0-33.82-67.3z m-530.18 14.36a24 24 0 0 1-24 24H258a24 24 0 0 1-24-24v-9.78a24 24 0 0 1 24-24h128.64a24 24 0 0 1 24 24z"
                    fill="white"
                    fontStyle={{ color: "white" }}
                    preTo={(to) => {
                        getIsAnchor().then(
                            (res) => {
                                if (res == true || res == 'true')
                                    to()
                                else {
                                    show("请先注册成为主播", "warning", 2000)
                                }
                            }, (e) => {
                                show(getDataFromErrorOrDefault(e))
                            }
                        )
                    }}
                >开播</Item>
                <Item to={"/i/sub/subscribe"} mt={height(0.45)} d="M555.123126 1023.995565a35.477653 35.477653 0 0 1-25.011745-9.933742L99.590068 583.185732A339.875911 339.875911 0 0 1 339.951164 2.948725a337.037699 337.037699 0 0 1 215.171962 76.809118A339.875911 339.875911 0 0 1 1011.18835 583.185732L580.31226 1014.061823a35.477653 35.477653 0 0 1-25.189134 9.933742zM339.951164 73.90403A268.920606 268.920606 0 0 0 149.790946 532.984854l405.33218 405.33218 405.332181-405.33218A268.920606 268.920606 0 0 0 580.31226 152.664419a35.477653 35.477653 0 0 1-25.189134 9.933743 35.477653 35.477653 0 0 1-25.011745-10.465908A267.678888 267.678888 0 0 0 339.951164 73.90403z"
                    fill="white"
                    radius={width(1.6)}
                    fontStyle={{ color: "white" }}
                    followInfos={followInfos}
                    details={LiveStreamList}
                    avatarMap={avatarMap}
                >订阅</Item>

                <Item to={"/i/sub/history"} d="M512 0a512 512 0 1 0 512 512A511.721133 511.721133 0 0 0 512 0z m248.191721 585.620915h-260.461874a47.965142 47.965142 0 0 1-47.965141-47.686274V205.525054a47.965142 47.965142 0 1 1 95.651416 0v285.002179h212.775599a47.965142 47.965142 0 0 1 0 95.651416z"
                    fill="white"
                    fontStyle={{ color: "white" }}
                    details={HistoryList}
                    liveHistories={liveHistories}
                    avatarMap={avatarMap}
                >历史记录</Item>

                {/*<svg t="1673418929262" className="icon" viewBox="0 0 1116 1024" version="1.1"*/}
                {/*     xmlns="http://www.w3.org/2000/svg" p-id="6330" width="200" height="200">*/}
                {/*    <path*/}
                {/*        d="M555.123126 1023.995565a35.477653 35.477653 0 0 1-25.011745-9.933742L99.590068 583.185732A339.875911 339.875911 0 0 1 339.951164 2.948725a337.037699 337.037699 0 0 1 215.171962 76.809118A339.875911 339.875911 0 0 1 1011.18835 583.185732L580.31226 1014.061823a35.477653 35.477653 0 0 1-25.189134 9.933742zM339.951164 73.90403A268.920606 268.920606 0 0 0 149.790946 532.984854l405.33218 405.33218 405.332181-405.33218A268.920606 268.920606 0 0 0 580.31226 152.664419a35.477653 35.477653 0 0 1-25.189134 9.933743 35.477653 35.477653 0 0 1-25.011745-10.465908A267.678888 267.678888 0 0 0 339.951164 73.90403z"*/}
                {/*        fill="#3D3D3D" p-id="6331"></path>*/}
                {/*    <path*/}
                {/*        d="M555.123126 278.432697a165.858026 165.858026 0 0 0-234.507283 234.507283L555.123126 747.979429l234.507284-235.039449a165.858026 165.858026 0 0 0-234.507284-234.507283z"*/}
                {/*        fill="#F683A2" p-id="6332"></path>*/}
                {/*</svg>*/}
            </Stack>
            <HeadShot />
        </div>
    )
}