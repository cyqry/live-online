import React from "react";
import { Container, Typography, Box, Button, List, ListItem, ListItemIcon, ListItemText, Dialog } from "@mui/material";

import VerifiedUserIcon from "@mui/icons-material/VerifiedUser";
import { makeStyles } from "@material-ui/styles";
import { Link } from "react-router-dom";
import { getRoomInfo } from "../../../Api/spaceApi";
import { useState } from "react";
import { useEffect } from "react";
import { giftMap } from "../../../Common/const";
import { Close } from "@mui/icons-material";

const useStyles = makeStyles((theme) => ({
    container: {
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        minHeight: "40vh",
    },
    icon: {
        fontSize: 72,
        color: theme.palette.primary.main,
    },
    tip: {
        fontSize: '16px',
        fontWeight: 300,
        color: '#666',
    },
    button: {
        marginTop: "15px"
    },

    dialog: {
    },
    giftDisplay: {
        padding: '10px',
        width: "230px",
        height: "250px",
        position: 'relative',
        display: 'inline-block',
        backgroundColor: theme.palette.background.paper,
        boxShadow: theme.shadows[1],
        borderRadius: theme.shape.borderRadius,
    },
    closeButton: {
        position: 'absolute',
        top: '0px',
        left: '0px',
        cursor: 'pointer',
        backgroundColor: '#c485c0',
        color: "#84441a",
        borderRadius: '50%',
    },
    giftList: {
        display: "inline-block",
        marginTop: theme.spacing(2),
    },
}));

const Authenticated = ({ anchor }) => {
    const classes = useStyles();
    const [roomInfo, setRoomInfo] = useState({ id: "---" })
    const [open, setOpen] = useState(false)

    useEffect(() => {
        getRoomInfo().then(
            (data) => {
                setRoomInfo(data)
            })
    }, [])
    const parsedAnchorProperty = anchor.anchorProperty ? JSON.parse(anchor.anchorProperty) : null;
    const gifts = (parsedAnchorProperty && parsedAnchorProperty.gifts) || [];
    return (
        <Container className={classes.container}>
            <Typography variant="h5">
                您已经是认证主播
            </Typography>
            <Box mt={2}>
                <VerifiedUserIcon className={classes.icon} />
            </Box>
            <Typography variant="h6" component="h2" className={classes.tip}>
                房间号:{roomInfo.id}, 快去开启你的直播吧
            </Typography>
            <Button variant="contained" className={classes.button} color="primary"><Link to={'/anchor'}>开启直播</Link></Button>

            <Button className={classes.button} onClick={() => { setOpen(true) }}>查看累计收到的礼物</Button>
            <Dialog open={open} onClose={() => { setOpen(false) }} PaperProps={{ className: classes.dialog }}>
                <div className={classes.giftDisplay}>
                    <Close className={classes.closeButton} onClick={() => {
                        setOpen(false)
                    }} />

                    {gifts == [] ?
                        <div style={{ textAlign: "center", lineHeight: "250px",color:"#919e9d" }}>还没有收到礼物!</div>
                        : <>
                            <Typography variant="h5" sx={{ display: "inline-block", fontSize: "16px", color: "#555", verticalAlign: "top", marginTop: "10%", marginLeft: "10px" }} gutterBottom>
                                收到的礼物:
                            </Typography>
                            <List className={classes.giftList}>

                                {gifts.map((gift, index) => {
                                    const { giftId, count } = gift;
                                    const { src, name } = giftMap.get(giftId);

                                    return (
                                        <ListItem key={index}>
                                            <ListItemIcon>
                                                <img src={src} alt={name} />
                                            </ListItemIcon>
                                            <ListItemText primary={name} secondary={`X ${count}`} />
                                        </ListItem>
                                    );
                                })}
                            </List>
                        </>
                    }
                </div>
            </Dialog>
        </Container>
    );
};

export default Authenticated;
