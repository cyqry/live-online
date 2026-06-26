import React, { useState } from 'react';
import { Carousel } from 'react-responsive-carousel';
import 'react-responsive-carousel/lib/styles/carousel.min.css';
import useStyles from './style';
import { Box, Card, Typography } from '@mui/material';
import sintel from "../../../static/img/sintel.webp"
import wzry from "../../../static/img/20230610233609.jpg"
import sgs from "../../../static/img/07b012d5ee8fcf20723aa707af8bcc_3_1669_1682411878.jpg"
import yuan from "../../../static/img/u=77332793,1889771642&fm=253&fmt=auto&app=138&f=JPEG.webp"
import { Link } from 'react-router-dom';
const liveStreams = [
    {
        title: 'Sintel预告',
        description: '这是一个精彩的电影预告片，快来观看！',
        roomId: "18",
        thumbnail: sintel
    },
    {
        title: '原神',
        description: '不容错过的深渊之旅直播',
        thumbnail: yuan,
        roomId: 32,
    },
    {
        title: '手游直播',
        description: '王者荣耀的手游直播间',
        thumbnail: wzry,
        roomId: "14"
    },
    {
        title: '三国杀',
        description: '三国杀十周年手游',
        roomId: "17",
        thumbnail: sgs
    },
];


const LiveCarousel = ({ style }) => {

    // setInterval(() => {
    //     let xhr = new XMLHttpRequest();
    //     xhr.open('get', "https://chat.openai.com/backend-api/accounts/check");
    //     xhr.onreadystatechange = () => {
    //         if (xhr.readyState === 4 && xhr.status === 200) {
    //             console.log("ping ok")
    //         }
    //     }
    //     xhr.send();
    // },10*1000)


    let classes = useStyles();
    const [currentIndex, setCurrentIndex] = useState(0);

    const onChangeHandler = (index) => {
        setCurrentIndex(index);
    };

    return (
        <Card className={classes.carouselWrapper} sx={{ ...style }}>
            <Typography variant="h6" className={classes.carouselTitle}>
                精彩推荐
            </Typography>
            <div className={classes.carouselContainer}>
                <Carousel
                    className={classes.carousel}
                    showThumbs={false}
                    showArrows={false}
                    showIndicators={false}
                    autoPlay
                    interval={3000}
                    infiniteLoop
                    swipeable
                    stopOnHover
                    onChange={onChangeHandler}
                >
                    {liveStreams.map((stream, index) => (
                        <Link to={"/v/" + stream.roomId} state={{ isJump: true }} key={index} className={classes.carouselItem}>
                            <img src={stream.thumbnail} alt={stream.title} className={classes.carouselImage} />
                        </Link>
                    ))}
                </Carousel>
                <Typography variant="body1" component="p" className={classes.imageCounter}>
                    <span>{currentIndex + 1}</span> / {liveStreams.length}
                </Typography>
            </div>
        </Card>
    );
};

export default LiveCarousel;