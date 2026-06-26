import React from 'react';
import Stack from '@mui/material/Stack';
import { Avatar, Box, Container } from '@mui/material';
import { headerBack, height, logo, width } from '../../Common/util';
import CenterInput from './CenterInput';
import UserHappyRight from './UserHappyRight';
import StartNavigation from './Navigation';
import { makeStyles } from '@material-ui/styles';

const padding = 3;
const contentHeight = 9;
const size = height(8);

const useStyles = makeStyles((theme) => ({
    header: {
        backgroundColor: '#99b8c0',
        color: '#000',
        position: 'fixed',
        top: 0,
        left: 0,
        width: '100%',
        boxSizing: 'border-box',
        height: height(contentHeight + padding),
        zIndex: 1000,
        paddingLeft: width(12),
        paddingTop: height(padding),
        boxShadow: '0 2px 4px rgba(0, 0, 0, 0.3)',
    },
    fill: {
        width: '100%',
        height: height(contentHeight + padding),
        backgroundColor: '#f0f0f0',
        zIndex: 1,
    },
    stack: {
        alignItems: 'center',
    },
    logo: {
        width: size,
        height: size,
        objectFit: 'contain',
        marginTop: -12,
    },
}));

export default function Header() {
    const classes = useStyles();

    return (
        <>
            <div className={classes.header}>
                <Stack className={classes.stack} spacing={2} direction="row">
                    <StartNavigation />
                    <CenterInput />
                    <UserHappyRight />
                </Stack>
            </div>
            <div className={classes.fill}></div>
        </>
    );
}
