import { Box, Typography } from "@mui/material";
import { height, width } from "../../../../Common/util";
import * as React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { useState } from "react";
require("./index.css")

export default function Item({ to, preTo, radius, state, details: Details, mt, d, fontStyle, fill, children, liveHistories, followInfos, avatarMap }) {
    let [inTag, setInTag] = useState(false)
    let [inDetails, setInDetails] = useState(false)
    let iconDiameter = radius || width(2.1);

    let nav = useNavigate();

    let videocamIconSx = {
        marginTop: mt,
        width: iconDiameter,
        height: iconDiameter,
        color: "white",
        opacity: 0.8,
        verticalAlign: "middle",
        top: 0
    };
    return (
        <div style={{ width: "6ch", height: "52.7px", textAlign: "center", verticalAlign: "bottom", position: "relative", cursor: "pointer" }}>
            <div className={"shake"} onClick={() => {
                if (preTo) {
                    preTo(() => { nav(to) })
                } else {
                    nav(to, {
                        state
                    })
                }
            }} onMouseEnter={() => { setInTag(true) }} onMouseLeave={() => { setInTag(false) }}>
                <div style={{ width: width(2.1), height: width(2.1), margin: "0 auto", verticalAlign: "middle", overflow: "hidden" }}>
                    <svg style={videocamIconSx} viewBox="0 0 1024 1024">
                        <path d={d}
                            fill={fill}
                        />
                    </svg>
                </div>
                <Typography ml={width(0.03)} fontSize={width(0.5)} sx={fontStyle}
                    variant={"body1"/*默认就是body1,对应p标签*/}
                    component={"span"/*覆盖了variant,变成span标签*/}> {children} </Typography>
            </div>
            {Details && (inDetails || inTag) &&
                <Details followInfos={followInfos} avatarMap={avatarMap} liveHistories={liveHistories} style={{ position: "absolute", top: "52px", left: "-119px", padding: "18%" }} onMouseEnter={() => { setInDetails(true) }} onMouseLeave={() => { setInDetails(false) }} />
            }
        </div>
    )
}