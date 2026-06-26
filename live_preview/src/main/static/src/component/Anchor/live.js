import axios from "axios";
import { Letter, RtcEntity, UserRtcMap } from "./rtcType";
import { getMediaStream, log } from "../../Common/util";
import { GATEWAY_HOST } from "../../Config/host";

let COTURN_HOST = "ytycc.com"

let socket;
let userRtcMap = new UserRtcMap();
let eventMap = new Map();
let mediaStream;
let handleDataChannelMessage;
let sendDataChannelMap = new Map()

function clear() {
  log("清理")
  if (socket && (socket.readyState == socket.CONNECTING || socket.readyState == socket.OPEN)) {
    socket.close();
  }
  socket = null;

  userRtcMap && userRtcMap.forEach((id, entity) => {
    entity.close()
  })
  userRtcMap = new UserRtcMap();

  eventMap = new Map();
  if (mediaStream) {
    mediaStream.getTracks().forEach(track => track.stop());
    mediaStream = null
  }
}


async function initAndGet(choose, deviceIndex) {
  if (!mediaStream) {
    log("初始化流")
    mediaStream = await getMediaStream(choose);
  } else {
    log("流已存在")
  }
  return mediaStream;
}

//开播
async function init(roomId, anchorId, handleMessage, handleLiveClose, handleLiveOpen) {
  if (!handleMessage)//没有传处理函数就什么都不做
    handleMessage = () => { }
  handleDataChannelMessage = handleMessage

  if (socket) {
    return
  }
  log("开始建连")
  socket = new WebSocket("wss://" + GATEWAY_HOST + "/yws/webrtc_pub/" + anchorId + "/" + roomId)

  //todo : userRtcMap应该基于 id 和 who 来存连接
  eventMap.set("answer", async (answerJson, letter) => {
    log(letter)
    let rtcEntity = userRtcMap.getRtcEntity(letter.targetId)
    let answer = JSON.parse(answerJson);
    let connection = rtcEntity.connection;
    await connection.setRemoteDescription(answer);
    rtcEntity.hasRemote = true;
    rtcEntity.iceTmpForeach(async (ice) => {
      await addIce(connection, ice)
    });
    log("set_remote:", answerJson, "成功")
  })

  eventMap.set("candidate", async (candidateInitJson, letter) => {
    let rtcEntity = userRtcMap.getRtcEntity(letter.targetId)
    let candidateInit = JSON.parse(candidateInitJson);
    if (rtcEntity.hasRemote) {
      await addIce(rtcEntity.connection, candidateInit);
    } else {
      rtcEntity.addIceToTmp(candidateInit)
    }
  })

  eventMap.set("reqRtc", async (_, letter) => {
    let config = {
      iceServers: [
        {
          urls: [
            "turn:" + COTURN_HOST + ":3478?transport=udp",
            "turn:" + COTURN_HOST + ":3478?transport=tcp"
          ],
          credential: "654321",
          username: "mycoturn",
        },

        {
          urls: ["stun:" + COTURN_HOST + ":3478"],
        }],
    };

    let connection = await createRtc(config, letter);
    let rtcEntity = new RtcEntity(connection);
    if (userRtcMap.getRtcEntity(letter.targetId)) {
      log("替换之前的流")
      userRtcMap.getRtcEntity(letter.targetId).close()
    }
    userRtcMap.put(letter.targetId, rtcEntity)
    log(rtcEntity.connection)
    await RtcSend(rtcEntity.connection, letter)
  })

  socket.onclose = async (e) => {
    if (handleLiveClose) {
      handleLiveClose(e)
    }
    log("流关闭", e)
  }

  socket.onmessage = async (msgEvent) => {
    let letter = Object.setPrototypeOf(JSON.parse(msgEvent.data), Letter.prototype);
    let split = letter.msg.split(" diy_split ");

    if (eventMap.has(split[0].trim())) {

      log("on " + split[0].trim(), "----", split[1].trim());

      await (eventMap.get(split[0].trim()))(split[1].trim(), letter);//执行对应的方法
    }
  }
  socket.onopen = async (e) => {
    log("连接打开!")
    if (handleLiveOpen)
      handleLiveOpen(e)
    log("连接打开2")
  }
}

let sendMessgeByDataChannel = (userId, msg) => {
  if (!sendDataChannelMap.get(userId))
    return
  sendDataChannelMap.get(userId).send(JSON.stringify(msg))
}


let createRtc = async (config, letter) => {

  let connection = new RTCPeerConnection(config)//暂时不用config

  //这里初始化了的话就什么都不做
  // await initAndGet(1);

  mediaStream.getTracks().forEach((t) => {
    connection.addTrack(t, mediaStream);
  })

  connection.onconnectionstatechange = (e) => {
    log("onconnectionstatechange: ", connection.is);
  }
  connection.oniceconnectionstatechange = (e) => {
    log("oniceconnectionstatechange: ", connection.iceConnectionState);
  }
  connection.onicegatheringstatechange = (e) => {
    log("onicegatheringstatechange: ", connection.iceGatheringState);
  };


  connection.onicecandidate = (evt) => {
    let real_send;
    if (evt.candidate != null) {
      real_send = JSON.stringify(evt.candidate);
    } else {
      real_send = JSON.stringify({
        flag: '_',
      })
    }
    log("发送 candidate:", real_send);
    letter.msg = "candidate diy_split " + real_send;
    socket.send(JSON.stringify(letter));
  };
  log("create:", connection);

  let sendChannel = connection.createDataChannel("BData")

  sendChannel.onopen = (e) => {
    log("data sendChannel 打开!!!")
    sendDataChannelMap.set(letter.targetId, sendChannel)
  }
  sendChannel.onclose = (e) => {
    log("data sendChannel 删除!!")
    sendDataChannelMap.delete(letter.targetId)
  }

  connection.ondatachannel = (e) => {
    let receChan = e.channel
    receChan.onopen = () => {
      log("receChan  打开")
    }
    receChan.onmessage = (e) => {

      // { nickname: currentUser.nickname, avatar: currentUser.avatar, giftId: gift.id, giftCount: gift.count }
      // {relayToUser：true ,targetUserId,msg}
      if (handleDataChannelMessage)
        handleDataChannelMessage(JSON.parse(e.data), letter.targetId)
      else {
        log("消息处理函数未初始化!")
      }
    }
  }


  return connection;
}

let RtcSend = async (connection, letter) => {
  let offer = await connection.createOffer({});
  letter.msg = "offer diy_split " + JSON.stringify(offer);
  socket.send(JSON.stringify(letter));
  await connection.setLocalDescription(offer);
  log("set_local")
  log(connection);
}

async function addIce(connection, candidateInit) {
  if (!candidateInit.candidate) {
    await connection.addIceCandidate(null);
    log(candidateInit + "add candidate finished! ")
  } else {
    await connection.addIceCandidate(candidateInit);
    log(candidateInit + "add candidate successful! ")
  }
}


export {
  init, initAndGet, clear, sendMessgeByDataChannel
}