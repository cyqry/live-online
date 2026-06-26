import React, { useEffect, useRef } from "react"
import FullScreenButton from "./FullScreenButton";
export default function ControlBar({ player, play, mute, volume }) {
    console.log("我是")
    let playRef = useRef();
    let muteRef = useRef();
    let volumeRef = useRef();

    useEffect(() => {
        playRef.current.appendChild(play)
        muteRef.current.appendChild(mute)
        volumeRef.current.appendChild(volume)
    }, []);


    //得到所有控制组件的父组件
    // controlBar.el().insertAdjacentHTML(
    //     'beforeend',
    //     `<div class="vjs-left-controls"></div><div class="vjs-right-controls"></div>`
    // );

    // const leftControls = controlBar.el().querySelector('.vjs-left-controls');
    // const rightControls = controlBar.el().querySelector('.vjs-right-controls');

    // leftControls.appendChild(controlBar.el().querySelector('.vjs-play-control'));
    // leftControls.appendChild(<div className='vjs-label'>我是标签</div>);
    // rightControls.appendChild(controlBar.el().querySelector('.vjs-mute-control'));
    // rightControls.appendChild(controlBar.el().querySelector('.vjs-volume-panel'));
    // rightControls.appendChild(controlBar.el().querySelector('.vjs-picture-in-picture-control'));
    // rightControls.appendChild(controlBar.el().querySelector('.vjs-fullscreen-control'));
    return (
        <div style={{
            display: "flex",
        }}>
            <div className="dom_videojs" ref={playRef}></div>
            <div className="dom_videojs" ref={muteRef}></div>
            <div className="dom_videojs" ref={volumeRef}></div>
            <button onClick={() => { player.pause() }} >停止</button>
            <FullScreenButton/>
        </div>
    )
}