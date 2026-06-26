import styled from '@emotion/styled';
import { Button, Card, CardActionArea, CardActions, CardContent, CardMedia, Typography } from '@mui/material';
import React, { useEffect, useRef, useState } from 'react';
import ReactDOM from 'react-dom/client';
import videojs from 'video.js';
import 'video.js/dist/video-js.css';
import './testVideo.css'
import 'webrtc-adapter'
import ControlBar from './ControlBar';

export default function TestVideo() {
    let ImageHost = "http://localhost:8083"

    return (
        <Card sx={{ width: "50%" }}>
            <CardActionArea>
                <CardMedia
                    height={"300px"}
                    //这里宽度无效
                    component={"video"}
                    className={"video-js vjs-theme-city"}
                    image={ImageHost + "/img"}
                    title="Contemplative Reptile"
                />
                {/* <CardContent>
                    <Typography gutterBottom variant="h5" component="h2">
                        我的CArd
                    </Typography>
                    <Typography variant="body2" color="textSecondary" component="p">
                        Lizards are a widespread group of squamate reptiles, with over 6,000 species, ranging
                        across all continents except Antarctica
                    </Typography>
                </CardContent> */}
            </CardActionArea>
            <CardActions>
                <Button size="small" color="primary">
                    Share
                </Button>
                <Button size="small" color="primary">
                    Learn More
                </Button>
            </CardActions>
        </Card>
    );
}










const VideoWrapper = styled('div')({
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#000',
    width: '50%',
    height: '500px',
});

const Video = styled('video')({
    width: '100%',
    height: '100%',
});


const videoOptions = {
    autoplay: true,
    controls: true,
    responsive: true,
    muted: true,
    fluid: true,
    sources: [
        {
            src: 'http://localhost:8083/video',
            type: 'video/mp4',
        },
    ],
};

let controlBar;
const TestVideo2 = () => {
    const videoRef = useRef();

    useEffect(() => {
        console.log("effect")
        const player = videojs(videoRef.current, videoOptions);

        player.ready(function () {

            const controlBar = player.getChild('controlBar');

            ReactDOM.createRoot(controlBar.el()).render(
                <ControlBar
                    player={player}
                    play={controlBar.el().querySelector('.vjs-play-control')}
                    mute={controlBar.el().querySelector('.vjs-mute-control')}
                    volume={controlBar.el().querySelector('.vjs-volume-panel')}
                    p_in_p={controlBar.el().querySelector('.vjs-picture-in-picture-control')}
                    full_screen={controlBar.el().querySelector('.vjs-fullscreen-control')}
                />);

            // //删除与时间有关的组件
            // controlBar.removeChild('remainingTimeDisplay');
            // controlBar.removeChild('currentTimeDisplay');
            // controlBar.removeChild('timeDivider');
            // controlBar.removeChild('durationDisplay');
        })


        return () => {
            //下面的依赖项变化后调用，即相当于unmount时调用
            player.dispose();
        };
    }, [videoRef]);


    const handleCustomButtonClick = () => {
        // 在这里添加按钮的点击处理逻辑
        console.log('Custom button clicked');
    };
    return (
        <VideoWrapper className='video-container'>

            <Video ref={videoRef} muted className="video-js vjs-big-play-centered" />
            <button className="vjs-custom-button" onClick={handleCustomButtonClick}>
                Custom Button
            </button>
        </VideoWrapper>
    );
};



export { TestVideo2 }