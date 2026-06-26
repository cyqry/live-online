import { useEffect, useRef } from "react"
import ReactCanvasNest from "react-canvas-nest";

export default function ModelOne() {


    return (
        <ReactCanvasNest
            className='canvasNest'
            config={{
                pointColor: ' 255, 255, 255 ',
                lineColor: '255,255,255',
                pointOpacity: 0.5,
                pointR: 2,
                count: 100
            }}
            style={{ zIndex: 1 }}
        />
    )
}