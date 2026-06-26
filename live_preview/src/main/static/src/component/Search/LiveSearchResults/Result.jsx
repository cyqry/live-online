import { Box, Container, Grid, Typography } from "@mui/material";
import { useState } from "react"
import { useQuery } from "react-query"
import useStyles from "../style";
import Category, { RoomSearch } from "../../Classify/Category";
import AnchorCard from "./AnchorCard";
import { log } from "../../../Common/util";

export default function Result({ condition, type, searchResults }) {
    const classes = useStyles();


    let relationRooms = searchResults.filter(r => r.type == 1)
    let relationAnchors = searchResults.filter(r => r.type == 2)
    
    
    log("searchResults", searchResults, "relaionRooms", relationRooms)

    let count = type == 0 ? searchResults.length : (type == 1 ? relationRooms.length : relationAnchors.length)

    return (
        <Container >
            <Typography className={classes.resultHeader}>
                找到<em className={classes.em}>"{condition}"</em>相关{type == 0 ? "综合" : type == 2 ? "主播" : "直播"}共<em className={classes.em}>{count}</em>个
            </Typography>
            {
                <Box  >
                    {
                        type == 1 || type == 0 ?
                            <Box className={classes.resultBody}>
                                <Typography variant="h5" className={classes.tag}>相关直播</Typography>
                                <Grid container spacing={3}>
                                    {relationRooms && relationRooms.length > 0 ?
                                        relationRooms.map((room) => (
                                            <Grid item xs={12} sm={8} md={3} key={room.roomId}>
                                                <RoomSearch room={room} />
                                            </Grid>
                                        )) :
                                        <div style={{ width: "100%", height: "100px", lineHeight: "100px", color: "#999", fontSize: "16px" }}>没有相关直播~</div>
                                    }
                                </Grid>
                            </Box> : <></>

                    }
                    {
                        type == 2 || type == 0 ?
                            <Box className={classes.resultBody} >
                                <Typography variant="h5" className={classes.tag} >相关主播</Typography>
                                <Grid container spacing={3}>
                                    {relationAnchors && relationAnchors.length > 0 ?
                                        relationAnchors.map(anchor => (
                                            <Grid item xs={12} sm={8} md={2.2} key={anchor.roomId}>
                                                <AnchorCard anchor={anchor} />
                                            </Grid>
                                        )) :
                                        <div style={{ width: "100%", height: "100px", lineHeight: "100px", color: "#999", fontSize: "16px" }}>没有相关直播~</div>
                                    }
                                </Grid>
                            </Box> : <></>

                    }
                </Box>
            }
        </Container>
    )
}