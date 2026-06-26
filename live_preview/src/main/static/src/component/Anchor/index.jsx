
import OpenPlaylist from "./OpenPlaylist";
import { useState } from "react";
import OpenPlay from "./OpenPlayPage";
import { useEffect } from "react";
import { getWholeUser } from "../../Api/userApi";
import { useNavigate } from "react-router";
import { CircularProgress } from "@mui/material";
import Loading from "../Loading";
import { log } from "../../Common/util";


export default function Anchor(props) {
  const [roomExtra, setRoomExtra] = useState();
  const [wholeUser, setWholeUser] = useState()
  const nav = useNavigate();
  useEffect(() => {
    getWholeUser().then(
      (data) => {
        setWholeUser({ ...data, who: 1 })
      },
      (e) => {
        log(e)
        nav("/")
      }
    ).catch((e) => {
      nav("/")
    })
  }, [])


  return (
    <div style={{ display: "inline-block", width: "100%", textAlign: "center", marginTop: "20px" }}>
      {!wholeUser ? <Loading model="other" size={160} /> : (
        roomExtra ?
          <OpenPlay roomExtra={roomExtra} wholeUser={wholeUser} /> :
          <OpenPlaylist setRoomExtra={setRoomExtra} />)
      }
      {/* <button style={{ backgroundColor: "red" }} onClick={createRoom}>创建房间!</button>
      <button style={{
        width: width(5),
        backgroundColor: "red",
        borderRadius: width(1),
        verticalAlign: "middle",
        height: height(5),
      }} onClick={init(roomId, 1)}> 开始
      </button> */}
    </div>
  )
}