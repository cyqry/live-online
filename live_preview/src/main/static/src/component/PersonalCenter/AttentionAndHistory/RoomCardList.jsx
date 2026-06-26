import React from 'react';
import { Box, Grid, Typography } from '@mui/material';
import RoomCard from './RoomCard';
import { log } from '../../../Common/util';

const RoomCardList = ({ rooms, avatarMap, style, model }) => {
    log("rooms:", rooms)
    return (
        <Box style={style}>
            <Grid container spacing={1.5}>
                {rooms.map((room, index) => (
                    <Grid item xs={6} key={index} marginBottom={2}>
                        <RoomCard room={room} avatarMap={avatarMap} model={model} />
                    </Grid>

                ))}
            </Grid>
        </Box>
    );
};

export default RoomCardList;