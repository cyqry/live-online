import GiftItem from "./GiftItem";
import useStyles from "./styles";
import { sendGift } from "../../../Api/userApi";
import { useNotification } from "../../NotificationProvider";
import { isReadyNotityAnchor, notityAnchor } from "../../LiveVideo/watch";
import { giftMap } from "../../../Common/const";
import { log } from "../../../Common/util";
const gifts = [
  {
    id: 1,
    name: '礼物1',
    image: 'https://huyaimg.msstatic.com/cdnimage/actprop/21087_1__45_1667887967.jpg',
    gifImage: 'gift1.gif',
    price: "80炫币",
    description: '一个礼物',
    otherDescription: '礼物1的其他描述',
  },
  {
    id: 2,
    name: '礼物2',
    image: 'https://huyaimg.msstatic.com/cdnimage/actprop/21087_1__45_1667887967.jpg',
    gifImage: 'gift1.gif',
    price: "1炫币和50炫点",
    description: '一个礼物',
    otherDescription: '礼物2的其他描述',
  },
  {
    id: 3,
    name: '礼物3',
    image: 'https://huyaimg.msstatic.com/cdnimage/actprop/21087_1__45_1667887967.jpg',
    gifImage: 'gift1.gif',
    price: "50炫点",
    description: '一个礼物',
    otherDescription: '礼物3的其他描述',
  },
];




const GiftBar = ({ height, roomBase, currentUser }) => {
  const classes = useStyles();
  let { show } = useNotification()
  let onSendGift = (gift, count) => {

    log("send")
    sendGift(roomBase.roomId, [{ giftId: gift.id, count: count }])
      .then(
        (res) => {

          show('赠送成功!', "success", 2000)
          if (isReadyNotityAnchor()) {
            notityAnchor({ nickname: currentUser.nickname, avatar: currentUser.avatar, giftId: gift.id, giftCount: count })
          } else {
            console.log("未通知主播")
          }
        },
        (e) => {
          show('余额不足!', "error", 2000)
        }
      ).catch(
        (e) => {
          show('网络错误!')
        }
      )

  }

  return (
    !(currentUser && (currentUser.who == 1 || currentUser.who == 0)) ? <></> :
      <div className={classes.container} style={{ height }} >
        {Array.from(giftMap).reverse().map(([id, gift]) => (
          <GiftItem key={gift.id} gift={gift} onSendGift={onSendGift} />
        ))}
      </div>
  );
}
export default GiftBar;