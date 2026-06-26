import React, { useEffect, useState } from 'react';
import { CircularProgress, Container, Grid } from '@mui/material';
import GiftsDisplay from './GiftsDisplay';
import EndStreamButton from './EndStreamButton';
import { LiveVideo } from '../../LiveVideo';
import BarrageChat from '../../Watch/LiveSidebar/BarrageChat';
import { clear, init, initAndGet, sendMessgeByDataChannel } from '../live';
import { HeaderWidth } from '../../Watch/size';
import { addAndGetLength, log, width } from '../../../Common/util';
import { useNavigate } from 'react-router';
import { useNotification } from '../../NotificationProvider';
import { giftMap } from '../../../Common/const';
import Loading from '../../Loading';

//roomBaseExtra 由 roomInfo, roomTag, choose 组成的一个实体，需要传递给该开播页面
function OpenPlay({ roomExtra: roomBaseExtra, wholeUser }) {
  let [mediaStream, setMediaStream] = useState();
  let [ready, setReady] = useState(false);
  let { roomInfo, roomTag, choose } = roomBaseExtra;
  let nav = useNavigate()
  let { show } = useNotification()
  let [{ gifts, ignore }, setGifts] = useState({ gifts: [{ id: 1, count: 0 }, { id: 2, count: 0 }, { id: 3, count: 0 }], ignore: 0 })
  let [giftAnimateList, setGiftAnimateList] = useState([{ id: 1, animate: false }, { id: 2, animate: false }, { id: 3, animate: false }])

  useEffect(() => {
    async function run() {
      try {
        let m = await initAndGet(choose);
        setMediaStream(m);
      } catch (e) {
        log("流获取异常!error:", e)
        show("直播开启失败!", "error", 2000)
      }
    };
    run()
    return () => {
      clear()
    }
  }, [])


  //处理dataChannel传来的msg
  /**
   * 对所有观众的dataChannel生效
   * data: 观众发过来的data ，userId 谁发的
   */
  const handleMessage = (data, userId) => {
    log("handleMessage")
    log(data)


    if (data) {
      if (data.relayToUser) {
        sendMessgeByDataChannel(data.targetUserId, data.msg)
      } else {
        gifts.forEach(gift => {
          if (gift.id == data.giftId) {
            gift.count += data.giftCount
          }
        })
        setGifts({ gifts, ignore: ignore + 1 })

        setGiftAnimateList(
          prevAnimateList => prevAnimateList.map(giftAnimate => {
            if (giftAnimate.id === data.giftId) {
              return { ...giftAnimate, animate: !giftAnimate.animate };
            }
            return giftAnimate;
          })
        )
      }
    }
  }

  const handleEndStream = (e) => {
    //todo show gifts
    clear()
  }



  useEffect(() => {
    if (mediaStream) {
      async function run() {
        await init(roomInfo.id, roomInfo.anchorId, handleMessage, () => {
          clear()
          show("您已下播", "info", 2000)
        });
        setReady(true);
      }
      run()
    }
  }, [mediaStream])

  return (
    mediaStream ?
      <Grid container spacing={1} sx={{ justifyContent: "center", padding: "8px 0px", width: width(90), margin: "0px auto" }}>
        <Grid item xs={8.5}>
          <LiveVideo width={HeaderWidth} remote={false} mediaStream={mediaStream} />
          <GiftsDisplay width={addAndGetLength(HeaderWidth, -20)} gifts={gifts} giftAnimateList={giftAnimateList} />
        </Grid>
        <Grid item xs={3.5} >
          <BarrageChat roomBase={{ ...roomInfo, ...roomTag }} currentUser={wholeUser} />
        </Grid>
        <Grid item xs={12} textAlign={"center"}>
          {ready && <EndStreamButton onEnd={handleEndStream} />}
        </Grid>
      </Grid>
      :
      <Loading style={{ marginTop: "50px" }} model="other" size={400} />
  )
}

export default OpenPlay;
