import React from 'react';
import { Box, Typography, Avatar, Chip, IconButton, Card, Button } from '@mui/material';
import LiveIcon from '@mui/icons-material/LiveTv';
import OfflineIcon from '@mui/icons-material/OfflineBolt';
import HeatIcon from '@mui/icons-material/Whatshot';
import { makeStyles } from '@material-ui/styles';
import { Link, useNavigate } from 'react-router-dom';
import { base64ToSrcOrDefault, height as heightUtil } from '../../../../Common/util';

const useStyles = makeStyles((theme) => ({
    root: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
    },
    tag: {
        color: "orange",
    },
    followInfoItem: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        width: '100%',
        padding: theme.spacing(1),
        borderBottom: `1px solid ${theme.palette.divider}`,
        zIndex: "2",
        '&:hover': {
            backgroundColor: "#eaeff1",
        }
    },
    liveAvatar: {
        border: `2px solid ${theme.palette.primary.main}`,
    },
    offlineAvatar: {
        border: `2px solid ${theme.palette.grey[400]}`,
    },
    liveInfo: {
        display: 'flex',
        flexDirection: 'column',
        marginLeft: theme.spacing(2),
        color: "#666"
    },
    liveTitle: {
        fontWeight: 500,
    },
    statusChip: {
        fontSize: '0.75rem',
        height: 'auto',
        marginTop: theme.spacing(0.5),
    },
    button: {
        color: "#5c6569",
        backgroundColor: "#dae7ed",
        width: "62.5%",
        marginTop: "13px",
        marginBottom: "5px",
        "&:hover": {
            backgroundColor: "orange",
        }
    }
}));

const AttentionList = ({ avatarMap, style, maxHeight = heightUtil(53), minHeight = heightUtil(32), onMouseEnter, onMouseLeave, followInfos }) => {
    const classes = useStyles();
    let nav = useNavigate()
    let count = followInfos ? followInfos.filter(l => l.online).length : 0;
    return (
        !followInfos ?
            <></>
            :
            <Card elevation={7} style={{ width: '280px', ...style, maxHeight, minHeight, overflowY: "auto" }} className={classes.root} onMouseEnter={onMouseEnter} onMouseLeave={onMouseLeave} >
                <Typography variant="h6" className={classes.tag} sx={{
                    fontSize: "15px",
                    fontWeight: 400,
                    marginTop: "7px",
                    marginBottom: "13px",
                }} >我订阅的有{count}个在直播</Typography>
                {followInfos.map((followInfo) => (
                    <Link key={followInfo.roomId} to={"/v/" + followInfo.roomId} state={{ isJump: true }} className={classes.followInfoItem}>
                        <Box display="flex" alignItems="center">
                            <Box>
                                <Avatar
                                    style={{ margin: "0px auto" }}
                                    src={avatarMap && avatarMap.get(followInfo.roomId) ? avatarMap.get(followInfo.roomId) : base64ToSrcOrDefault()}
                                    className={followInfo.online ? classes.liveAvatar : classes.offlineAvatar}
                                />
                                <Chip
                                    className={classes.statusChip}
                                    icon={followInfo.online ? <LiveIcon fontSize="inherit" /> : <OfflineIcon fontSize="inherit" />}
                                    label={followInfo.online ? '在线' : '离线'}
                                    color={followInfo.online ? 'primary' : 'default'}
                                />
                            </Box>
                            <Box className={classes.liveInfo}>
                                <Typography className={classes.liveTitle}>{followInfo.anchorNickname}</Typography>
                                <Typography variant="body2">{followInfo.liveTitle}</Typography>
                            </Box>
                        </Box>
                        <Box display="flex" alignItems="center">
                            <IconButton>
                                <HeatIcon sx={{ color: "red" }} />
                            </IconButton>
                            <Typography>{followInfo.hot}</Typography>
                        </Box>
                    </Link>
                ))}
                <Button variant='contained' disableElevation onClick={() => {
                    nav("/i/sub/subscribe")
                }} className={classes.button}>全部订阅 {">"}</Button>
            </Card>
    );
};

export default AttentionList;
