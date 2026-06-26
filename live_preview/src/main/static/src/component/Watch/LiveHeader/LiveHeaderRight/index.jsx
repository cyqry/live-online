import * as React from "react";
import { txDiameter } from "../LiveTitle";
import { getDataFromErrorOrDefault, height, log, width } from "../../../../Common/util";
import { Chip, IconButton, Stack, Tooltip, Typography } from "@mui/material";
import { Block, Favorite, Reply, Report } from "@mui/icons-material";
import { makeStyles } from "@material-ui/styles";
import { useState } from "react";
import "./index.css";
import ReportDialog from "./ReportDialog";
import { bannedLive, getAttentionCount, getFollow, subscribe, unsubscribe } from "../../../../Api/spaceApi";
import { useNotification } from "../../../NotificationProvider";
import { useEffect } from "react";
import ShareCard from "./ShareCard";

const useStyles = makeStyles((theme) => {
    return {
        root: {
            display: "flex",
            position: "relative",
            height: txDiameter,
            flexDirection: "column",
            justifyContent: "space-between",
            float: "right"
        },
        follow: {
            display: "flex",
            backgroundColor: "ghostwhite",
            borderRadius: width(2),
            position: "absolute",
            right: "2px",
            justifyContent: "center",
            alignItems: "center"
        },
        banLive: {
            border: '2px solid red', // 自定义描边样式
            borderRadius: '50%', // 圆形边框样式
            padding: 8, // 调整内边距
        }
    }
})

export default function LiveHeaderRight({ roomBase, currentUser }) {

    const classes = useStyles();
    let [follow, setFollow] = useState(false);
    let [inShareBt, setInShareBt] = useState(false);
    let [inSharePage, setInSharePage] = useState(false);
    let { show } = useNotification()
    let [attention, setAttention] = useState(0)


    let followHeight = height(5);

    const [dialogOpen, setDialogOpen] = useState(false);

    const handleReportClick = () => {
        if (currentUser && (currentUser.who == 0 || currentUser.who == 1)) {
            setDialogOpen(true);
        } else {
            show("登录后才能使用此功能");
        }
    };

    const handleDialogClose = () => {
        setDialogOpen(false);
    };

    const banedLive = () => {
        bannedLive(roomBase.roomId).then(
            (res) => {
                show('禁播成功!', "success", 2000)
            },
            (e) => {
                show(getDataFromErrorOrDefault(e, "操作失败！"), "error", 2500)
            }
        ).catch(e => {
            log(e)
            show('网络错误!', "error", "2000")
        })
    }

    useEffect(() => {

        getAttentionCount(roomBase.roomId).then((count) => {
            setAttention(count)
        })

        getFollow(roomBase.roomId).then(
            (data) => {
                setFollow(data)
            }, (e) => {
                console.log("未关注")
            }
        ).catch((e) => {
            log(e)
        })
    }, [roomBase.roomId])

    let attentionHandle = () => {

        async function run() {
            if (currentUser && (currentUser.who == 0 || currentUser.who == 1)) {
                try {
                    let suc = await (follow ? unsubscribe : subscribe)(roomBase.roomId);
                    if (suc && suc != 'false') {

                        setAttention(attention => attention + (follow ? -1 : 1))
                        setFollow(follow => !follow)
                    }
                    // if (!follow && suc) {
                    //     let f = await getFollow(roomBase.roomId);
                    //     setFollow(f)
                    // } else if (suc) {
                    //     setFollow()
                    // }
                } catch (e) {
                    show(getDataFromErrorOrDefault(e), 'error', 2000)
                }
            } else {
                show("请先登录!", "warning", 2000)
            }
        }
        run()


    }
    return (
        <div className={classes.root} >
            <div className={classes.follow} style={{ height: followHeight, maxWidth: "200px" }}>
                <Typography sx={{ textAlign: "center", lineHeight: followHeight }} pl={"10px"} pr={"5px"} component={"span"}>{attention}</Typography>
                <Chip clickable={true} onClick={attentionHandle} icon={<Favorite color={follow ? "error" : "inherit"} />} color={"success"} label={"关注"} />
            </div>



            <Stack direction={"row"} justifyContent={"space-between"} alignItems={"center"} spacing={1} sx={{ width: width(16), position: "absolute", bottom: "0px", right: "10px" }} >
                {currentUser && currentUser.role >= 2 && (
                    <Tooltip title="禁播" sx={{ position: "absolute", bottom: "-9px", right: "194px", width: "100px" }}>
                        <IconButton onClick={banedLive}>
                            <Block color="error" fontSize="large" />
                            <Typography>
                                禁播
                            </Typography>
                        </IconButton>
                    </Tooltip>
                )}

                <Chip
                    icon={<Report />}
                    clickable={true}
                    sx={{ position: "absolute", bottom: "0px", right: "100px" }}
                    label="举报"
                    onClick={handleReportClick}
                />
                <ReportDialog roomId={roomBase.roomId} open={dialogOpen} onClose={handleDialogClose} />

                <Chip sx={{ position: "absolute", bottom: "0px", right: "0px" }} onMouseEnter={() => { setInShareBt(true) }} onMouseLeave={() => { setInShareBt(false) }} icon={<Reply />} clickable={true} label={"分享"} />
                {(inShareBt || inSharePage) &&
                    <div style={{ zIndex: 6, position: "absolute", bottom: "-50px", right: "10px", width: "310px", height: "166.6px", backgroundColor: "red" }} onMouseEnter={() => { setInSharePage(true) }} onMouseLeave={() => { setInSharePage(false) }}>
                        <ShareCard style={{ width: "100%", height: "100%" }} />
                    </div>
                }
            </Stack>
        </div >
    )
}