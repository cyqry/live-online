import Stack from "@mui/material/Stack";
import LiveHeader from "./LiveHeader";
import LiveSidebar from "./LiveSidebar";
import { addAndGetLength, getDataFromErrorOrDefault, getRandomLong, height, log, width } from "../../Common/util";
import { LiveVideo } from "../LiveVideo";
import { useLocation, useNavigate, useParams } from "react-router";
import { useEffect } from "react";
import { makeStyles } from "@material-ui/styles";
import { GiftHeigth, HeaderHeight, HeaderWidth, VideoHeight } from "./size";
import GiftBar from "./GiftBar"
import { getAnchorPublic, getUser } from "../../Api/userApi";
import { useNotification } from "../NotificationProvider";
import { useState } from "react";
import { getRoomBase, getRoomPublic } from "../../Api/spaceApi";
import Loading from "../Loading";

const useStyles = makeStyles((theme) => {
    return {
        root: {
            display: "flex",
            marginBottom: "50px",
            width: "auto",
            margin: "10px 0px 10px 100px",
            maxHeight: addAndGetLength(addAndGetLength(HeaderHeight, addAndGetLength(VideoHeight, GiftHeigth)), 50)
        }
    }
})


export default function Watch() {
    const classes = useStyles();
    let location = useLocation();
    let navigate = useNavigate();//hook函数不能放在条件内,每次render必须以相同的顺序调用这些hook函数
    let { roomId } = useParams();
    let { show } = useNotification();
    let flag = location.state == null || !location.state.isJump;
    let [user, setUser] = useState();
    let [roomBase, setRoomBase] = useState();
    let [anchorPublic, setAnchorPublic] = useState();
    let [onlineRoom, setOnlineRoom] = useState();
    let [isBanPost, setIsBanPost] = useState()

    //说明不是jump过来的，那么手动跳转一次;这样好像还是没用
    useEffect(() => {
        if (flag) {
            //记得这里的路径要随着外面的路由路径
            console.log("自动跳转")//因为bug,不是跳转过来的我不接收
            navigate("/", {
                replace: true,
            })
            return
        }


        getAnchorPublic(roomId).then(
            (data) => {
                setAnchorPublic(data)
            }, (e) => {
                console.log("没有这个房间!", e)
                navigate("/")
            }
        ).catch(e => {
            show('网络错误!', 'error')
            log(e)
        })

        getRoomPublic(roomId).then(
            (data) => {
                setOnlineRoom(data)
            }
        ).catch((e) => {
            log(e)
        })

        getRoomBase(roomId).then(
            (data) => {
                setRoomBase(data)
            }, (e) => {
                log("没有这个房间!", e)
                navigate("/")
            }
        ).catch(e => {
            show('网络错误!', 'error')
            log(e)
        })

        getUser().then(
            (user) => {
                if (user && user.id) {
                    setUser({ ...user, who: 0 })
                }
                else {
                    show("网络错误!", "error", 2000)
                }
            },
            (e) => {
                log(e)
                setUser({ id: getRandomLong() })
            }
        ).catch((e) => {
            log(e)
            show(getDataFromErrorOrDefault(e, "网络错误!"), "error", 2000)
        })




    }, [roomId]);

    if (flag) {
        return (
            <></>
        )
    }

    return (
        !(user && roomBase && anchorPublic) ? <Loading size={450} model="other" /> :
            <div className={classes.root}>
                <Stack spacing={0} >
                    <LiveHeader height={HeaderHeight} width={HeaderWidth} roomBase={roomBase} onlineRoom={onlineRoom} anchorPublic={anchorPublic} currentUser={user} />
                    <LiveVideo id={user.id} roomId={roomId} width={HeaderWidth} style={{ borderRadius: "0px" }} setIsBanPost={setIsBanPost} />
                    <GiftBar height={GiftHeigth} roomBase={roomBase} currentUser={user} />
                </Stack>
                <LiveSidebar banPostState={[isBanPost, setIsBanPost]} roomBase={roomBase} currentUser={user} />
            </div>

    )
}