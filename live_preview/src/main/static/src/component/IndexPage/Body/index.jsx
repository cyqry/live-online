
import * as React from "react";
import JumpLink from "./JumpLink";
import RecommendRoom from "./RecommendRoom";
import { LiveVideo } from "../../LiveVideo";
import { getRandomLong } from "../../../Common/util";
import { Link } from "react-router-dom";

export default class Body extends React.Component {

    render() {
        let centerRoomId = 33;
        return (
            <div>
                {/* <div><JumpLink /></div> */}
                <div style={{ display: "flex", justifyContent: "center" }}>
                    <div style={{
                        marginTop: "40px",
                        width: "80%",
                        textAlign: "center",
                        height: "520px",
                    }}>

                        <LiveVideo roomId={centerRoomId} width={"872px"} id={getRandomLong()} style={{
                            margin: "-10px auto 0px auto",
                        }} ></LiveVideo>
                        <RecommendRoom width={"100%"} />
                    </div>
                </div>
            </div >
        )
    }
}

