import 'webrtc-adapter'
import { getCookieValue } from '../../Api/userApi';
import { GATEWAY_HOST, STURN_HOST, TURN_HOST } from '../../Config/host';
import { log } from '../../Common/util';


let currentRoomId
let socket;
let eventMap;
let haveRemote;
let iceTmp;
let canInitRtc = false;
let LocalVideo;
let connection;
let dataChannel;


async function localInit(video, mediaStream) {
    LocalVideo = video;
    if (!video || !video.srcObject) {
        video.srcObject = mediaStream;
    }
}

async function notityAnchor(msg) {
    if (dataChannel)
        dataChannel.send(JSON.stringify(msg))
    else
        console.log("直播还未连接或直播间不支持通知对方")
}

function isReadyNotityAnchor() {
    return dataChannel
}

//不清理socket和currentRoomId，下面留着复用
//不清理 canInitRtc，canInitRtc应该在sokcet重新new之前被置为false
function watchClear() {
    log("watch 清理")
    eventMap = undefined
    haveRemote = undefined
    iceTmp = undefined
    if (LocalVideo && LocalVideo.srcObject) {
        log("清理video")
        LocalVideo.srcObject.getTracks().forEach(track => track.stop());
        LocalVideo.srcObject = null
    }
    if (connection) {
        log("清理rtc connection")
        connection.close()
        connection = undefined
    }
    if (dataChannel) {
        log("清理data channel")
        dataChannel.close();
        dataChannel = undefined;
    }
}

//观众连接远程直播源初始化
async function init({ video, roomId, id, handleLiveClose, handleLiveOpen, handleLiveFailed, handleDataChannelMessagStruct }) {
    haveRemote = false;
    iceTmp = [];
    if (connection) {
        connection.close();
        connection = null;
    }
    if (dataChannel) {
        dataChannel.close();
        dataChannel = undefined;
    }
    eventMap = null;
    log("socket", socket)
    log("eventMap", eventMap)
    log("connection", connection)
    log("haveRemote", haveRemote)
    log("iceTmp", iceTmp)
    log("LocalVideo", LocalVideo)
    log("video", video);
    LocalVideo = video
    //仅留下socket复用，当为同一个房间且连接存活，复用这个socket
    //canInitRtc应该和socket一样
    if ((!socket || (socket.readyState !== socket.OPEN && socket.readyState !== socket.CONNECTING)) || currentRoomId != roomId) {
        canInitRtc = false
        if (socket) {
            socket.close()
        }
    }
    //监听事件,下面的步骤多次在这个页面init都会执行
    eventMap = new Map();
    //设置监听事件，收到offer之前保证已经初始化
    eventMap.set("offer", async (offerJson) => {
        let offer = JSON.parse(offerJson);

        await connection.setRemoteDescription(offer);
        log("set_remote:", offerJson)
        haveRemote = true;
        for (const ice of iceTmp) {
            await addIce(ice);
        }

        let answer = await connection.createAnswer();
        socket.send("answer diy_split " + JSON.stringify(answer));
        await connection.setLocalDescription(answer);
        log("set_local")
        log(connection);

    })

    eventMap.set("candidate", async (candidateInitJson) => {
        let candidateInit = JSON.parse(candidateInitJson);
        if (haveRemote) {
            await addIce(candidateInit);
        } else {
            iceTmp.push(candidateInit)
            log("还未set_remote,加入到缓存")
        }
    })

    if (!socket || (socket.readyState !== socket.OPEN && socket.readyState !== socket.CONNECTING)) {
        log("socket conn")
        socket = new WebSocket("wss://" + GATEWAY_HOST + "/yws/webrtc_sub/" + id + "/" + roomId);
    }




    socket.onmessage = async (msgEvent) => {
        let split = msgEvent.data.split(" diy_split ");
        if (eventMap.has(split[0].trim())) {
            log("on ", split[0], "----", split[1]);
            await (eventMap.get(split[0].trim()))(split[1].trim());
        }
    }
    socket.onclose = (e) => {
        //这里也不要清理，因为清理是异步的，可能出问题
        canInitRtc = false;
    }

    socket.onopen = async () => {
        currentRoomId = roomId;
        log("连接成功！")
        if (!canInitRtc) {
            await initRtcWithVideo({ video, roomId, id, handleLiveClose, handleLiveOpen, handleLiveFailed, handleDataChannelMessagStruct })
            socket.send("reqRtc diy_split _")
            canInitRtc = true;
            log("发送观看请求!")
        } else {
            log("已经初始化")
        }
    }
    if (canInitRtc) {//false时 onopen执行开始，true时由这里执行开始
        await initRtcWithVideo({ video, roomId, id, handleLiveClose, handleLiveOpen, handleLiveFailed, handleDataChannelMessagStruct })
        socket.send("reqRtc diy_split _")
    }
}

async function initRtcWithVideo({ video, roomId, id, handleLiveClose, handleLiveOpen, handleLiveFailed, handleDataChannelMessagStruct }) {
    let config = {
        iceServers: [
            {
                urls: [
                    "turn:" + TURN_HOST + ":3478?transport=udp",
                    "turn:" + TURN_HOST + ":3478?transport=tcp"
                ],
                credential: "654321",
                username: "mycoturn",
            },
            {
                urls: ["stun:" + STURN_HOST + ":3478"],
            }],
    };

    connection = new RTCPeerConnection(config);//暂时不用config



    let c = connection.createDataChannel("Adata");
    c.onopen = () => {
        log("开通道!")
        dataChannel = c;
    }
    c.onmessage = (m) => {
        log("消息:" + m.data)
    }
    c.onclose = (m) => {
        log("关通道!")
    }

    connection.ondatachannel = (e) => {
        let receiveChannel = e.channel;
        receiveChannel.onmessage = event => {
            let message = JSON.parse(event.data)

            if (message && message.banPostNotify) {//禁言通知
                if (handleDataChannelMessagStruct) {
                    let { setIsBanPost } = handleDataChannelMessagStruct
                    if (setIsBanPost) {
                        log("receiveChannel.onmessage setIsBanPost")
                        setIsBanPost(true)
                    }
                }
            }
            log("receiveChannel消息:", message)
        };
    }


    connection.onconnectionstatechange = (e) => {
        log("onconnectionstatechange:", connection.connectionState)
        switch (connection.connectionState) {
            case "new":
                // 执行初始状态的逻辑
                break;
            case "connecting":
                // 执行连接建立中的逻辑
                break;
            case "connected":
                if (handleLiveOpen) {
                    handleLiveOpen(e)
                }
                break;
            case "disconnected":
                // 执行连接已断开的逻辑
                break;
            case "failed":
                if (handleLiveFailed) {
                    handleLiveFailed(e)
                }
                // 执行连接失败的逻辑
                break;
            case "closed":
                if (handleLiveClose) {
                    handleLiveClose(e)
                }
                break;
            default:
                // 处理未知状态的逻辑
                break;
        }
    }
    connection.oniceconnectionstatechange = (e) => {

        log("oniceconnectionstatechange: ", connection.iceConnectionState);
    }
    connection.onicegatheringstatechange = (e) => {
        log("onicegatheringstatechange: ", connection.iceGatheringState);
    };
    /**
     * 到这里都没用， 测试
     */

    connection.onicecandidate = (e) => {
        let real_send;
        if (e.candidate != null) {
            real_send = JSON.stringify(e.candidate);
        } else {
            real_send = JSON.stringify({
                flag: '_',
            })
        }
        log("发送 candidate:", real_send);
        socket.send("candidate diy_split " + real_send);
    };

    connection.ontrack = (ev) => {
        video.srcObject = ev.streams[0];
        log("得到stream " + ev.streams[0]);

        video.addEventListener('loadeddata', function (e) {
            log('提示当前帧的数据是可用的')
            log(e)
            log(e.data)
        })
        video.addEventListener('loadstart', function (e) {
            log('提示视频的元数据已加载')
            log(e)
            log(e.data)
            log(video.duration)
        })

    }
}


async function addIce(candidateInit) {
    if (!candidateInit.candidate) {
        await connection.addIceCandidate(null);
        log(candidateInit + "add candidate finished! ")
    } else {
        await connection.addIceCandidate(candidateInit);
        log(candidateInit + "add candidate successful! ")
    }
}



export {
    watchClear, init, localInit, notityAnchor, isReadyNotityAnchor
}