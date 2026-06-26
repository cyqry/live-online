import { useEffect, useRef } from "react";
import { ControlBar, CurrentTimeDisplay, ForwardControl, FullscreenToggle, LoadingSpinner, PlayToggle, PlaybackRateMenuButton, Player, ReplayControl, TimeDivider, VolumeMenuButton } from "video-react";
import '../../../node_modules/video-react/dist/video-react.css'
import './index.css'
import { init, initNotifyAnchor, localInit, watchClear } from "./watch";
import { getMediaStream, log, multiplyAndGetLength } from "../../Common/util";
import { HeaderWidth } from "../Watch/size";
import { Box, Card, Typography } from "@mui/material";
import { makeStyles } from "@material-ui/styles";
import { Schedule } from "@mui/icons-material";
import { useState } from "react";

const useStyles = makeStyles((theme) => ({
    root: {
        position: "relative",
        backgroundColor: "white",
        overflow: "hidden",
        borderRadius: '10px',
        boxShadow: '0 7px 14px rgba(0, 0, 0, 0.1), 0 3px 6px rgba(0, 0, 0, 0.08)',
    },
    notify: {
        width: "200px",
        height: "80px",
        boxSizing: "border-box",
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#9E9E9E",
        border: `1px solid ${theme.palette.grey[400]}`,
        borderRadius: theme.spacing(2),
        padding: theme.spacing(2),
        textAlign: 'center',
        boxShadow: theme.shadows[2],
        zIndex: 6,
    },
    notifyIcon: {
        fontSize: '48px',
        marginRight: theme.spacing(1),
        color: theme.palette.primary.main,
    },
    notifyText: {
        fontSize: '18px',
        fontWeight: 'bold',
        color: theme.palette.text.primary,
    },
}));

const LiveVideo = ({ roomId, id, width = HeaderWidth, style, remote = true, mediaStream, setIsBanPost }) => {
    let playerRef = useRef();
    const classes = useStyles();
    let height = multiplyAndGetLength(width, 0.5625);
    let [lived, setLived] = useState(false)
    useEffect(() => {
        let video = playerRef.current.video.video;
        log("params.id:" + id);
        log("videoRef.current:", video)
        if (remote) {//观众席连接远程直播源
            init({
                video, roomId, id,
                handleLiveClose: () => {
                    log("handleLiveClose")
                    //不应该判断rtc连接的状态来清理组件，因为不知道上一个rtc什么时候断开，很可能就清到了新rtc连接需要的
                    setLived(false)
                },
                handleLiveOpen: () => {log("handleLiveOpen"); setLived(true) },
                handleDataChannelMessagStruct: { setIsBanPost }
            }
            );
        } else {//主播开启本地视频
            localInit(video, mediaStream);
        }
        return watchClear
    }, [roomId])

    return (
        <Card className={classes.root} sx={{ ...style, width, height, }}>
            {/* 不将自定义组件卸载Player里，会报错 */}
            <Player
                width={width}
                height={height}
                ref={playerRef}
                playsInline={true}
                muted={false}
                autoPlay={true}
            // poster="https://video-react.js.org/assets/poster.png"
            >
                {/* <div style={{ backgroundColor: "red", height: "300px", width: "100px" }}>
                    <PlaybackRateMenuButton rates={[5, 2, 1.5, 1, 0.5]} order={7.1} />
                </div> */}
                {/* <ControlBarOne /> */}

                {/* 自动隐藏控制栏                关闭默认样式显示           这里不要这个    */}
                <ControlBar autoHide={true} disableDefaultControls={true}  >
                    <PlayToggle />
                    <LoadingSpinner />
                    <ReplayControl seconds={10} order={1.1} />
                    {/* <ForwardControl seconds={30} order={1.2} /> */}

                    {/* <CurrentTimeDisplay order={4.1} />
                        <TimeDivider order={4.2} /> */}
                    {/* <PlaybackRateMenuButton rates={[5, 2, 1.5, 1, 0.5]} order={7.1} /> */}
                    <VolumeMenuButton vertical />
                    <FullscreenToggle />
                </ControlBar>
            </Player>
            {remote && !lived &&
                <Box className={classes.notify}>
                    <Schedule className={classes.notifyIcon} />
                    <Typography className={classes.notifyText}>主播未开播</Typography>
                </Box>
            }
        </Card>
    )

}

export { LiveVideo }