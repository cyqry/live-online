import React from 'react';
import { Card, CardContent, Avatar, Typography, Grid, Box, Icon } from '@mui/material';
import { makeStyles } from '@material-ui/styles';
import { base64ToSrcOrDefault, calculateTimeDifference } from '../../../Common/util';
import { FiberManualRecord, OnlinePredictionOutlined } from '@mui/icons-material';
import { useNavigate } from 'react-router';


/**
 * 个人中心的RoomCard
 */

const useStyles = makeStyles((theme) => ({
    card: {
        width: '400px',
        height: '120px',
        minWidth: 30,
        minHeight: 50,
        cursor:"pointer"
    },
}));

const RoomCard = ({ room, avatarMap, model = "attention" }) => {
    let classes = useStyles();
    let nav = useNavigate();
    return (
        <Card className={classes.card} onClick={() => {
            nav('/v/' + room.id, {
                state: { isJump: true }
            })
        }} >
            <CardContent sx={{ paddingBottom: "1% !important", paddingLeft: 0 }} >
                <Grid container alignItems="center">
                    <Grid item xs={3} sx={{ position: "relative" }}>
                        <Avatar
                            src={avatarMap && avatarMap.get(room.id) ? avatarMap.get(room.id) : base64ToSrcOrDefault()}
                            sx={{
                                width: '75%',
                                height: '75%',
                                marginLeft: '15%',
                                aspectRatio: '1',
                            }}
                        />
                        {room.online && (
                            <Box
                                sx={{
                                    position: "absolute",
                                    top: 0,
                                    width: "35px",
                                    height: "12px",
                                    right: "10px",
                                    backgroundColor: "#62b21b",
                                    color: "white",
                                    fontSize: "8px",
                                    padding: "4px",
                                    borderRadius: "5px",
                                    display: "flex",
                                    alignItems: "center",
                                    justifyContent: "center",
                                }}
                            >

                                <Icon
                                    component={FiberManualRecord}
                                    sx={{ fontSize: "0.6rem", color: "white" }}
                                />
                                <Typography variant="caption" sx={{ color: "white", width: "2em", height: "1.3em", lineHeight: "1.3em", whiteSpace: "nowrap", fontWeight: "bold" }}>在线</Typography>
                            </Box>
                        )}
                    </Grid>
                    <Grid item xs={8}>
                        <Typography variant="subtitle1" style={{ lineHeight: "24px", fontSize: "14px", color: "#333", fontWeight: 700, textOverflow: "ellipsis" }}>
                            {room.anchorNickname}
                        </Typography>
                        <Typography color="textSecondary" style={{ fontSize: "12px", whiteSpace: "nowrap" }}>
                            {room.liveTitle}
                        </Typography>
                    </Grid>
                    <Grid item xs={12} height={"38px"} ml={'10%'} mt={"1%"} position={"relative"} >
                        {
                            room.online ? <span style={{
                                display: "inline-block",
                                textOverflow: "ellipsis",
                            }}>直播：</span> :
                                <span style={{
                                    color: "#989898",
                                    display: "inline-block",
                                    textOverflow: "ellipsis",
                                }}>{calculateTimeDifference(room.lastLiveTime)}&nbsp;&nbsp;直播了：</span>
                        }
                        <span style={{
                            color: "#f80",
                            maxWidth: "70px"
                        }}>{room.domain}</span>
                        {
                            model == 'history' ? <></> :
                                <span style={{
                                    display: "inline-block",
                                    color: "#989898",
                                    maxWidth: "100px",
                                    position: "absolute",
                                    top: "0",
                                    fontSize: "14px",
                                    whiteSpace: "nowrap",
                                    right: "10px"
                                }}>订阅: {room.attentions}</span>
                        }
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
};

export default RoomCard;
