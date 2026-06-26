import Stack from "@mui/material/Stack";
import * as React from "react";
import { Avatar, Chip, Typography } from "@mui/material";
import Face from '@mui/icons-material/Face';
import { base64ToSrcOrDefault, height, log, width } from "../../../../Common/util";
import { useEffect } from "react";
import { getImage } from "../../../../Api/storeApi";
import { useState } from "react";
import { stateMap } from "../../../../Common/const";
import { LiveTv, Movie } from "@mui/icons-material";

const txDiameter = width(7);
export default function LiveTitle({ roomBase, anchorPublic, onlineRoom }) {
    let titleSx = {
        width: width(40),
        float: "left",
        display: "flex",
    }
    let [avatarBase64, setAvatarBase64] = useState();
    useEffect(() => {
        getImage(anchorPublic.avatar).then(
            (data) => {
                setAvatarBase64(data);
            }
        ).catch((e) => {
            log(e)
        })

        //其他都是传过来的数据，所以只监听avatar
    }, [anchorPublic.avatar])
    let avatarSrc = base64ToSrcOrDefault(avatarBase64)

    let stateIcon = stateMap.get(roomBase.roomItemCategoryName) ? <Movie /> : <LiveTv />
    let stateLabel = stateMap.get(roomBase.roomItemCategoryName) ? "点播" : "直播";

    return (
        <div style={titleSx}>
            {/*                             设置这个对齐 让旁边的文字与这里对齐*/}
            <div style={{ display: "inline-block", verticalAlign: "top", marginRight: width(0.8) }} href={"#"}>
                <Avatar
                    alt="头像"
                    src={avatarSrc}
                    sx={{ width: txDiameter, height: txDiameter }}
                />
            </div>
            <div style={{
                display: "flex", flexDirection: "column",
                justifyContent: "space-between", width: width(80), height: txDiameter
            }}>
                <div style={{ width: "100%", height: '30px' }}>
                    {/*                                                设置这个对齐 让下一个div里的内容与这里对齐   */}
                    <Typography sx={{ display: "inline-block", float: "left", verticalAlign: "top", marginRight: "20px", lineHeight: '30px' }}
                        component={"h3"} fontSize={width(1.4)} maxWidth={"70%"}>
                        {roomBase.title}
                    </Typography>
                    <div style={{
                        maxWidth: width(30), display: "inline-block", fontSize: "12px", color: "#888", margin: '6px 0',
                        paddingRight: '12px',
                        whiteSpace: 'nowrap',
                        height: '18px',
                        lineHeight: '18px',
                        fontSize: '12px'
                    }}>
                        {roomBase.firstLevelCategoryName + '>' + roomBase.secondLevelCategoryName + '>' + roomBase.roomItemCategoryName}
                    </div>
                </div>
                <div style={{ width: width(20), height: height(5), display: "flex" }}>
                    <div style={{ display: "inline-block", lineHeight: height(5) }}>
                        <img style={{ width: "30px", height: height(2) }} src="https://shark2.douyucdn.cn/front-publish/live-master/assets/images/m1_100_cbfec9b.webp" />
                    </div>
                    <Typography sx={{ lineHeight: height(5), marginLeft: "20px", marginRight: "10px" }} ml={width(0)}
                        component={"span"} fontSize={width(0.9)}>
                        {anchorPublic.nickname}
                    </Typography>
                    {onlineRoom &&
                        <span style={{ lineHeight: height(5), fontSize: "12px", color: "red" }}>{'在线人数' + (onlineRoom.count + 1)}</span>}
                </div>
                <Stack spacing={1} direction={"row"}>
                    <Chip size={"small"} icon={<Face />} label="主播" />
                    <Chip size={"small"} icon={stateIcon} label={stateLabel} />
                </Stack>
            </div>

        </div >
    )
}
export {
    txDiameter
}