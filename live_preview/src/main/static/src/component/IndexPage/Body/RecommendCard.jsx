import { makeStyles } from '@material-ui/styles';
import { PlayCircleOutline } from '@mui/icons-material';
import { Card, Typography } from '@mui/material';
import React, { useState } from 'react';
import { base64ToSrcOrDefault } from '../../../Common/util';
import { useNavigate } from 'react-router';
import { parseAvatarToSrc } from '../../../Api/api';

const useStyles = makeStyles((theme) => ({
    card: {
        position: 'relative',
        objectFit: 'cover',
        cursor: "pointer",
        '&:hover': {
            '& $playButton': {
                opacity: 1,
                transform: 'translate(-50%, -50%) scale(0.8)',
            },
            '& $cardMedia': {
                filter: 'brightness(0.7)',
            },
        },
    },
    cardMedia: {
        position: 'absolute',
        top: '0px',
        left: '0px',
        width: '100%',
        height: '100%',
        transition: 'filter 0.3s',
    },
    playButton: {
        position: 'absolute',
        top: '50%',
        left: '50%',
        transition: 'opacity 0.1s,transform 0.3s ease-out',
        color: theme.palette.common.white,
        fontSize: 72,
        opacity: 0,
        transform: 'translate(-50%, -50%) scale(1.1)',
    },
    title: {
        position: 'absolute',
        bottom: theme.spacing(1),
        left: theme.spacing(1.3),
        fontSize: "16px",
        color: theme.palette.common.white,
        zIndex: 1,
    },
}));

const RecommendCard = ({ room, width = '180px', height = '101.25px' }) => {
    const classes = useStyles();
    const nav = useNavigate()

    return (
        <Card className={classes.card} sx={{ width, height }} onClick={() => {
            nav(`/v/${room.roomId}`, {
                state: { isJump: true }
            })
        }} >
            <img src={parseAvatarToSrc(room.coverSrc)} alt={room.title} className={classes.cardMedia} />
            <PlayCircleOutline className={classes.playButton} />
            <Typography variant="h5" className={classes.title}>
                {room.title}
            </Typography>
        </Card>
    );
};

export default RecommendCard;
