import React from 'react';
import { Box, Card, List, Typography } from '@mui/material';
import LiveHistoryListItem from './LiveHistoryListItem';
import Timeline from './Timeline';
import { makeStyles } from '@material-ui/styles';
import { log } from '../../../../Common/util';

const useStyles = makeStyles((theme) => {
    return {
        root: {
            backgroundColor: "white",
            overflow: "auto",
            display: "flex",
            paddingRight: "20px",
            textAlign: "center"
        }
    }
});


const LiveHistoryList = ({ avatarMap, liveHistories, maxHeight, minHeight, style, onMouseEnter, onMouseLeave }) => {
    const classes = useStyles();
    const today = new Date().toISOString().slice(0, 10);
    const width = "300px";
    log("live", liveHistories);
    const todaysHistories = liveHistories.filter(
        (history) => history.lastWatchTime.toString().slice(0, 10) === today
    );
    const earlierHistories = liveHistories.filter(
        (history) => history.lastWatchTime.toString().slice(0, 10) !== today
    );

    return (
        <Card elevation={3} className={classes.root} sx={{ ...style, width, maxHeight, minHeight }} onMouseEnter={onMouseEnter} onMouseLeave={onMouseLeave}>
            <Box sx={{ width: "100%" }}>
                <div style={{ display: "flex", flexDirection: "row" }} >
                    <Timeline />
                    <div style={{ width: "100%" }}>
                        <Typography variant="caption" color="grey.600" sx={{ marginBottom: '8px', marginLeft: '9px' }} >
                            {"今天"}
                        </Typography>
                        <List>
                            {todaysHistories.map((history, index) => (
                                <LiveHistoryListItem key={index} avatarMap={avatarMap} history={history} />
                            ))}
                        </List>
                    </div>
                </div>
                <hr />
                <div style={{ display: "flex", flexDirection: "row" }}>
                    <Timeline />
                    <div style={{ width: "100%" }}>
                        <Typography variant="caption" color="grey.600" sx={{ marginBottom: '8px', marginLeft: '9px' }} >
                            {"更早"}
                        </Typography>
                        <List>
                            {earlierHistories.map((history, index) => (
                                <LiveHistoryListItem key={index} avatarMap={avatarMap} history={history} />
                            ))}
                        </List>
                    </div>
                </div>
            </Box>
        </Card >
    );
};

export default LiveHistoryList;
