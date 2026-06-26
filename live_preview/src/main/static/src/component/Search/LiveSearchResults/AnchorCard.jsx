import { makeStyles } from "@material-ui/styles";
import { Avatar, Card, Typography } from "@mui/material";
import { Link } from "react-router-dom";
import { base64ToSrcOrDefault } from "../../../Common/util";
import { parseAvatarToSrc } from "../../../Api/api";

const useStyles = makeStyles((theme) => ({
    nick: {
        height: '14px',
        margin: '5px 0',
        fontSize: '14px',
        lineHeight: '14px',
        wordBreak: 'break-all',
        color: '#333',
        whiteSpace: 'nowrap',
        overflow: 'hidden',
        textOverflow: 'ellipsis',
        textAlign: 'center',
    },
    avatar: {
        width: '86px',
        height: '86px',
        margin: '0 auto',
    },
    room: {
        height: '12px',
        margin: '5px 0',
        fontSize: '12px',
        lineHeight: '12px',
        color: '#999',
        textAlign: 'center',
    },
    card: {
        "&:hover": {
            border: "2px solid orange"
        },
        padding: "30px 0px"
    },
    animatedIcon: {
        display: "inline-block",
        width: "18px",
        height: "18px",
        backgroundColor: "#f80",
        borderRadius: "50%",
        position: "absolute",
        bottom: "0px",
        right: "64px",
        backgroundImage: "url(https://a.msstatic.com/huya/main3/widget/search-host-list/icon_live_eb974.png)",
        animationName: "$living",
        animationDuration: "0.6s",
        animationTimingFunction: "steps(6)",
        animationIterationCount: "infinite",
    },
    "@keyframes living": {
        "0%": {
            backgroundPosition: "0 0",
        },

        "100%": {
            backgroundPosition: "-126px 0",
        },
    },
})
)
export default function AnchorCard({ anchor }) {
    const classes = useStyles();
    return (
        <Link to={`/v/${anchor.roomId}`} state={{ isJump: true }}>
            <Card className={classes.card}>
                <div style={{ position: "relative" }}>
                    <Avatar className={classes.avatar} src={parseAvatarToSrc(anchor.avatar)} alt={anchor.nickname} />

                    {anchor.online && <em className={classes.animatedIcon}></em>}
                </div>
                <Typography className={classes.nick}>{anchor.nickname}</Typography>
                <Typography className={classes.room}>房间号：{anchor.roomId}</Typography>
            </Card>
        </Link>
    )
}