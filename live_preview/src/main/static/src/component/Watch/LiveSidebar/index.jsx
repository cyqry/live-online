import * as React from "react";
import { addAndGetLength, height, width } from "../../../Common/util";
import { HeaderHeight, SideBarWidth, VideoHeight } from "../size";
import BarrageChat from "./BarrageChat";


export default class LiveSidebar extends React.Component {

    render() {
        let { currentUser, roomBase, banPostState } = this.props;
        return (
            <div style={{ marginLeft: "10px" }}>
                <div style={{ height: HeaderHeight }}></div>
                <BarrageChat banPostState={banPostState} roomBase={roomBase} currentUser={currentUser} width={width(SideBarWidth)} height={addAndGetLength(VideoHeight, -26)} />
            </div>
        )
    }
}