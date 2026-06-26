import { log } from "../Common/util";
import { CHAT_HOST } from "../Config/host";

let chat_ws;
let canSend = false

function isCanSend() {
    return canSend;
}

function clearChat() {
    chat_ws && chat_ws.close()
    canSend = false
}

function openChat({ roomId, id, onMessageEvent, handleClose, handleOpen }) {
    if (chat_ws && (chat_ws.readyState == chat_ws.CONNECTING || chat_ws.readyState == chat_ws.OPEN))
        return
    chat_ws = new WebSocket("wss://" + CHAT_HOST + "/chat/" + roomId + "/" + id);
    chat_ws.onopen = (e) => {
        if (handleOpen)
            handleOpen(e)
        canSend = true
    }
    chat_ws.onclose = (e) => {
        if (handleClose)
            handleClose(e)
        canSend = false
    }
    chat_ws.onmessage = (event) => {
        if (event.data != null)
            onMessageEvent(event.data)
    }
}

function sendMessage(msg) {
    if (!msg || msg == '') {
        log("奇怪的发送")
        return
    } else {
        if (!canSend) {
            //
        } else {
            chat_ws.send(msg)
        }
    }
}

export { openChat, sendMessage, clearChat, isCanSend }