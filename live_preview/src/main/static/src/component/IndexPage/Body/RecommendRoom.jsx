import { makeStyles } from '@material-ui/styles';
import { Box, Grid, Paper, Typography } from '@mui/material';
import React from 'react';
import RecommendCard from './RecommendCard';
import { PhoneAndroid, SportsEsports, Theaters, VideogameAsset } from '@mui/icons-material';
import { useState } from 'react';
import { useEffect } from 'react';
import { getRoomSearchsByName } from '../../../Api/spaceApi';
import { log } from '../../../Common/util';
const useStyles = makeStyles((theme) => ({
    root: {
        margin: "0px auto",
        marginLeft: "0px",
        marginTop: "13px",
        flexGrow: 1,
        padding: theme.spacing(2),
    },
}));


const RecommendRoom = ({ width }) => {
    const classes = useStyles();

    const [recommendRooms, setRecommendRooms] = useState([
        {
            header: { firstLevelName: "网游竞技", secondLevelName: "英雄联盟", roomItemName: "绝活", icon: SportsEsports },
            //rooms 以 header为条件被搜索出来
            rooms: []
        },
        {
            header: { firstLevelName: "娱乐天地", secondLevelName: "原创", roomItemName: "熊出没", icon: Theaters },
            rooms: []
        },
        {
            header: { firstLevelName: "手游休闲", secondLevelName: "王者荣耀", roomItemName: "大神", icon: PhoneAndroid },
            rooms: []
        },
        {
            header: { firstLevelName: "单机热游", secondLevelName: "永劫无间", roomItemName: "决赛圈", icon: VideogameAsset },
            rooms: []
        }
    ])

    useEffect(() => {

        async function run() {
            const updatedRecommendRooms = [];
            for (const r of recommendRooms) {
                try {
                    const res = await getRoomSearchsByName(r.header.secondLevelName, r.header.roomItemName);
                    const updatedRoom = { ...r, rooms: res };
                    updatedRecommendRooms.push(updatedRoom);
                } catch (e) {
                    const updatedRoom = { ...r, rooms: [] };
                    updatedRecommendRooms.push(updatedRoom);
                    log(e)
                }
            }
            setRecommendRooms(updatedRecommendRooms);
        }
        run()
    }, [])


    return (
        <div className={classes.root} style={{ width }}>
            <Grid container spacing={2}>
                {
                    recommendRooms.map((r, index) => (
                        <Grid item xs={6} key={index}>
                            <Box
                                sx={{
                                    marginBottom: "8px",
                                    float: "left",
                                    display: "flex",
                                    alignItems: "center",
                                }}
                            >
                                <r.header.icon sx={{ fontSize: "18px", marginRight: "6px" }} />
                                <Typography variant="subtitle1" sx={{ fontWeight: 600, marginRight: "10px" }}>
                                    {r.header.firstLevelName}
                                </Typography>
                                <Typography variant="body2" sx={{ fontSize: "14px", color: "#777" }}>
                                    {r.header.secondLevelName} {'>'} {r.header.roomItemName}
                                </Typography>
                            </Box>
                            <Grid container spacing={1} height={"170px"}>
                                {
                                    r.rooms.map((room, index) => (
                                        <Grid item xs={4} key={index}>
                                            <RecommendCard room={room} width='180px' height='101.25px' />
                                        </Grid>
                                    ))
                                }
                            </Grid>
                        </Grid>
                    ))
                }
            </Grid>
        </div>
    );
};

export default RecommendRoom;
