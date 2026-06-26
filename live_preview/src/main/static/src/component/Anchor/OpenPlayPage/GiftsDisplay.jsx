import React from 'react';
import { Paper, Typography, Grid, Box, Stack } from '@mui/material';
import { makeStyles } from '@material-ui/styles';
import { useEffect } from 'react';
import { giftMap } from '../../../Common/const';
import { CSSTransition } from 'react-transition-group';

const useStyles = makeStyles((theme) => {
    return {
        gift: {
            display: "flex",
            flexDirection: "row",
            alignItems: "center",
            justifyContent: "center",
            border: "1px solid #eee",
            borderRadius: "8px",
            '&:hover': {
                border: "2px solid orange"
            }
        },
        animationEnter: {
            fontSize: "1.1em",
        },
        animationEnterActive: {
            fontSize: "1.5em",
        },
        animationExit: {
            fontSize: "1.5em",
        },
        animationExitActive: {
            fontSize: "1.1em",
        },
    }
})

const Gift = ({ img, name, count, animate }) => {
    const classes = useStyles();
    return (

        <Box className={classes.gift} >
            <img src={img} alt={name} style={{
                width: "35px",
                height: "35px",
                verticalAlign: "center",
                marginRight: "2px",
                rotate: "1"
            }} />
            <Typography variant="span" fontSize={"0.8em"} color={"red"} >{name}</Typography>
            <CSSTransition
                in={animate}
                timeout={500}
                classNames={{
                    enter: classes.animationEnter,
                    enterActive: classes.animationEnterActive,
                    exit: classes.animationExit,
                    exitActive: classes.animationExitActive,
                }}
                unmountOnExit={false}
            >
                <Typography sx={{ transition: 'font-size 0.5s', fontWeight: 800, color: "red" }} variant="span" width={'60px'} color={"blue"} fontSize={"1.1em"} marginLeft={"10px"}>x {count}</Typography>
            </CSSTransition>
        </Box>
    );
};

const GiftsDisplay = ({ width, gifts, giftAnimateList }) => {

    let animateMap = new Map();
    giftAnimateList.forEach(gA => {
        animateMap.set(gA.id, gA.animate)
    })


    return (
        <Paper elevation={2} style={{ padding: '10px', width }}>
            <Box display={"flex"} flexDirection="row" alignItems={"center"}>
                <Typography variant="div" style={{
                    height: "60px",
                    lineHeight: "60px",
                    width: "140px",
                    fontSize: "0.8em",
                    color: "orange"
                }}>本次直播已收到礼物:</Typography>
                <Stack direction={"row"} spacing={2}>
                    {gifts.map((gift, index) => (
                        <Gift key={index} img={giftMap.get(gift.id).src} name={giftMap.get(gift.id).name} count={gift.count} animate={animateMap.get(gift.id)} />
                    ))}
                </Stack>
            </Box>
        </Paper>
    );
};

export default GiftsDisplay;
