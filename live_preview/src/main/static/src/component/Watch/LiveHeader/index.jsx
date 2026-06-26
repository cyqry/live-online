import LiveTitle, { txDiameter } from "./LiveTitle";
import LiveHeaderRight from "./LiveHeaderRight";


export default function LiveHeader({ height, width, roomBase, anchorPublic, onlineRoom, currentUser }) {
    return (
        <div style={{
            height, width, padding: "15px 15px 10px 15px",
            boxSizing: "border-box", backgroundColor: "rgba(18,18,18,0.1)", borderRadius: "10px 10px 1px 1px"
        }}>
            <LiveTitle roomBase={roomBase} anchorPublic={anchorPublic} onlineRoom={onlineRoom} />
            <LiveHeaderRight roomBase={roomBase} currentUser={currentUser} />
        </div >
    )
}