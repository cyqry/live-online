import { makeStyles } from '@material-ui/styles';
import { Box, CircularProgress } from '@mui/material';
import React from 'react';
const useStyles = makeStyles((theme) => ({
    root: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
    },
    circularProgress: {
        animation: '$rotate 1.5s linear infinite',
    },
    pulse: {
        animation: '$pulse 1.5s ease-in-out infinite',
    },
    '@keyframes rotate': {
        '0%': {
            transform: 'rotate(0deg)',
        },
        '100%': {
            transform: 'rotate(360deg)',
        },
    },
    '@keyframes pulse': {
        '0%': {
            opacity: 0.3,
            transform: 'rotate(0deg)',
        },
        '30%': {
            opacity: 0.6,
        },
        '50%': {
            opacity: 1,
        },
        '100%': {
            opacity: 0.3,
            transform: 'rotate(360deg)',
        },
    },
}));

const Loading = ({ style, size = 40, model = "rotate", color = "primary", thickness = 2 }) => {
    const classes = useStyles()
    return (
        <Box className={classes.root}>
            <CircularProgress style={style} className={model == "rotate" ? classes.circularProgress : classes.pulse} size={size} color={color} thickness={thickness} />
        </Box>
    );
};

export default Loading;
