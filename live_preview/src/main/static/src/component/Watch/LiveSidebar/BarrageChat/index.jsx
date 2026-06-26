import React, { useState, useEffect, useRef } from "react";

import { Avatar, Box, Button, Card, CardContent, IconButton, List, ListItem, ListItemAvatar, ListItemText, TextField, Typography } from "@mui/material";
import { makeStyles } from "@material-ui/styles";
import { EmojiEmotions, Send, Warning } from "@mui/icons-material";
import EmojiPicker from "emoji-picker-react";
import twemoji from "twemoji";
import { DanMuHeigth } from "../../size";
import { clearChat, isCanSend, openChat, sendMessage } from "../../../../Api/chat";
import { base64ToSrcOrDefault, getDataFromErrorOrDefault, getRandomLong, log } from "../../../../Common/util";
import { getImage } from "../../../../Api/storeApi";
import { banedPost, isBanedPost } from "../../../../Api/chatServerApi";
import { isRoomAdmin } from "../../../../Api/spaceApi";
import { useNotification } from '../../../NotificationProvider';
import { notityAnchor } from "../../../LiveVideo/watch";
import { sendMessgeByDataChannel } from "../../../Anchor/live";
const useStyles = makeStyles((theme) => ({
    root: {
        display: "flex",
        flexDirection: "column",
        position: "relative",
    },
    messagesContainer: {
        flexGrow: 1,
        overflowY: "auto",
        height: DanMuHeigth,
        border: "5px solid orange",
        borderRadius: "10px"
    },
    inputContainer: {
        display: "flex",
        alignItems: "center",
        padding: "1%",
    },
    inputField: {
        flexGrow: 1,
    },
    customEmoji: {
        width: "1.5em",
        height: "1.5em",
        verticalAlign: "text-bottom"
    },
    emojiPicker: {
        position: "absolute",
        bottom: "63px",
        right: "-89px"
    },


    item: {
        paddingTop: '0px',
        paddingBottom: '0px',
        alignItems: 'normal',
    },
    itemAvatar: {
        paddingTop: '10px',
        position: 'relative',
        '&:hover $avatarAdmin': {
            filter: 'brightness(0.7)', // 图片变暗
        },
        '&:hover $adminBanButton': {
            opacity: 1, // 按钮出现
        },
        cursor: 'pointer',
    },
    avatarAdmin: {
        transition: 'all 0.75s',
    },
    adminBanButton: {
        position: 'absolute',
        top: '35%',
        left: '3px',
        display: "inline-block",
        opacity: 0,
        transition: 'all 0.75s',
        backgroundColor: '#f00', // 修改背景颜色
        color: '#fff', // 修改字体颜色
        boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.5)', // 添加阴影效果
        fontSize: '13px', // 调整字体大小
        borderRadius: '10px',
        fontWeight: 'bold', // 加粗字体
        width: '2.5em',
        height: '1.6em',
        lineHeight: '1.6em',
        textAlign: 'center'
    },
    banTip: {
        display: 'flex',
        alignItems: 'center',
        backgroundColor: theme.palette.error.main,
        padding: theme.spacing(2),
        borderRadius: theme.spacing(1),
        color: theme.palette.error.contrastText,
    },
    banIcon: {
        marginRight: theme.spacing(1),
    },
}));
let avatarBase64Map = new Map()
const maxLength = 30; // 列表最大长度
/**
 * 
 * @param {*} currentUser 可能为游客User,也可能为已登录用户，需要带有who字段
 * @returns 
 */
const BarrageChat = ({ width, height, currentUser, roomBase, banPostState }) => {
    let { roomId } = roomBase;
    const classes = useStyles();
    //count用于触发渲染
    let [{ userMessages, count }, setUserMessages] = useState({ userMessages: [], count: 0 });
    const [input, setInput] = useState("");
    const [emojiPickerVisible, setEmojiPickerVisible] = useState(false);
    const [isManager, setIsManager] = useState(false)
    const messagesEndRef = useRef(null);
    //使用这个状态,setMap后触发一下渲染
    const [_, setIgnore] = useState(0)
    let isBanPost
    let setIsBanPost
    if (banPostState) {

        isBanPost = banPostState[0]
        setIsBanPost = banPostState[1]
        log(isBanPost)
        log(setIsBanPost)
    }

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };
    const { show } = useNotification()


    useEffect(() => {
        scrollToBottom();
    }, [userMessages.length]);
    log(currentUser)

    useEffect(() => {


        if (currentUser.who == 1) {
            setIsManager(true)
        } else if (Number(currentUser.role) < 2) {
            isRoomAdmin(roomId).then(
                (res) => {
                    if (res && (res == 'true' || res == true)) {
                        setIsManager(true)
                    }
                },
                (e) => {
                    log(e)
                }
            ).catch(e => {
                log(e)
            })
        } else if (Number(currentUser.role) >= 2) {
            setIsManager(true)
        }
        log(currentUser, (currentUser.who == 0 || currentUser.who == 1), setIsBanPost);
        (currentUser && (currentUser.who == 0 || currentUser.who == 1)) && setIsBanPost &&
            isBanedPost(roomId, currentUser.id).then(
                (res) => {
                    if (res.toString() == 'true') {
                        log("setIsBanPost(true)")
                        setIsBanPost(true)
                    } else {
                        log("setIsBanPost(false)", res)
                        setIsBanPost(false)
                    }
                },
                (e) => {
                    log(e)
                    setIsBanPost(false)
                }
            ).catch(e => {
                log(e)
                setIsBanPost(false)
            })

        openChat({
            roomId,
            id: currentUser.id ? currentUser.id : getRandomLong(),
            onMessageEvent: (msgEntityString) => {
                const msgEntity = JSON.parse(msgEntityString)
                if (msgEntity && msgEntity.message && msgEntity.user && msgEntity.message != '')
                    receiveMessage(msgEntity)
            },
            handleOpen: (e) => {
            },
            handleClose: (e) => {
            }
        })

        return () => {
            setUserMessages({ userMessages: [], count: 0 });
            clearChat()
        }
    }, [roomId])


    const handleInputChange = (e) => {
        setInput(e.target.value);
    };

    const receiveMessage = ({ message, user: senderUser }) => {
        let { avatar, id } = senderUser
        if (!avatarBase64Map.get(id)) {
            getImage(avatar).then(
                (res) => {
                    avatarBase64Map.set(id, res)
                    //由于闭包只捕获一次，所以set一次size就完了
                    setIgnore(avatarBase64Map.size)
                },
                (e) => {
                    log(e)
                }
            ).catch((e) => {
                log(e)
            })
        } else {
            log("从base64Map缓存加载")
        }
        log("userMessage:", userMessages)

        //确保setState刷新
        count = count + (count > 0 ? -1 : 1)
        // 添加新元素到列表末尾
        userMessages.push({ message, user: senderUser })

        // 确保列表长度不超过最大长度
        if (userMessages.length > maxLength) {
            const removeCount = userMessages.length - maxLength; // 需要删除的元素数量
            userMessages.splice(0, removeCount); // 删除最早添加的元素
        }

        setUserMessages({ userMessages, count })
    };

    const handleSendMessage = () => {
        if (input.trim() !== "") {
            if (!isCanSend()) {
                show("请等待连接初始化完成或刷新界面", "warning", 2000)
                return
            }
            sendMessage(input.trim())
            if (!avatarBase64Map.get(currentUser.id)) {
                getImage(currentUser.avatar).then(
                    (res) => {
                        avatarBase64Map.set(currentUser.id, res)
                        setIgnore(avatarBase64Map.size)
                    },
                    (e) => {
                        log(e)
                    }
                ).catch((e) => {
                    log(e)
                })
            } else {
                log("从缓存加载")
            }






            count = count + (count > 0 ? -1 : 1)
            userMessages.push({ message: input.trim(), user: currentUser })

            // 确保列表长度不超过最大长度
            if (userMessages.length > maxLength) {
                const removeCount = userMessages.length - maxLength; // 需要删除的元素数量
                userMessages.splice(0, removeCount); // 删除最早添加的元素
            }

            setUserMessages({ userMessages, count })
            setInput("");
            setEmojiPickerVisible(false);
        }
    };

    const handleAdminClickAvatar = (userMessage) => {
        return (e) => {
            if (currentUser) {
                if (userMessage.user.role <= currentUser.role)
                    banedPost(roomId, userMessage.user.id).then(
                        (res) => {
                            if (currentUser.who == 1) {
                                //chat调用live.js
                                sendMessgeByDataChannel(userMessage.user.id, { banPostNotify: true })
                            } else {
                                //chat调用watch.js
                                notityAnchor({ relayToUser: true, targetUserId: userMessage.user.id, msg: { banPostNotify: true } })
                            }
                            show("禁言成功!")
                        },
                        (e) => {
                            show(getDataFromErrorOrDefault(e, "网络错误!"), "error", 2000)
                        }
                    )
                else {
                    show("不能操作权限大于您的用户！", "warning", 3000)
                }
            } else {
                show('出错了，请联系管理人员', "error")
            }
        }
    }
    const handleEmojiSelect = (emojiObject, event) => {
        setInput(input + emojiObject.emoji);
    };

    const toggleEmojiPicker = () => {
        setEmojiPickerVisible(!emojiPickerVisible);
    };
    const handleEmojiIconBlur = () => {
        setEmojiPickerVisible(false)
    }

    const parseEmojis = (text) => {
        return (
            <span style={{ display: "block", maxWidth: "200px", overflowWrap: "break-word", wordBreak: "break-all" }}
                dangerouslySetInnerHTML={{
                    __html: twemoji.parse(text, { folder: "svg", ext: ".svg", className: classes.customEmoji }),
                }}
            />
        );
    };

    let canBanedPost = (userMessage) => {
        log(`canBanedPost: userMessage:${userMessage} userMessage.user.id: ${userMessage.user.id} currentUser.id:${currentUser.id} isManager:${isManager}  `)
        return isManager && (userMessage.user.id != currentUser.id)
    }

    return (
        <Box className={classes.root} width={width}  >
            <List className={classes.messagesContainer} style={{ height }} >
                <div style={{ maxHeight: "5000px", overflow: "hidden" }}>
                    <SystemMessage />
                    {userMessages.map((userMessage, index) => (
                        <ListItem className={canBanedPost(userMessage) ? classes.item : ""} sx={{ paddingTop: "0px", paddingBottom: "0px", alignItems: "normal" }} key={index}>
                            <ListItemAvatar onClick={canBanedPost(userMessage) ? handleAdminClickAvatar(userMessage) : undefined} className={canBanedPost(userMessage) ? classes.itemAvatar : ""} sx={{ paddingTop: "10px", position: "relative" }}>
                                <Avatar className={canBanedPost(userMessage) ? classes.avatarAdmin : ""} src={base64ToSrcOrDefault(userMessage.user && avatarBase64Map ? avatarBase64Map.get(userMessage.user.id) : undefined)} />
                                {canBanedPost(userMessage) && <span className={classes.adminBanButton} >禁言</span>}
                            </ListItemAvatar>
                            <ListItemText
                                primary={userMessage.user.nickname}
                                secondary={parseEmojis(userMessage.message)}
                            />
                        </ListItem>
                    ))}
                    <div ref={messagesEndRef} />
                </div>
            </List>
            {(!(currentUser && (currentUser.who == 0 || currentUser.who == 1))) ? <></> :
                isBanPost != 'false' && isBanPost ?
                    <Box className={classes.banTip}>
                        <Warning className={classes.banIcon} />
                        <Typography variant="body1">已被禁言,有问题请联系管理员~</Typography>
                    </Box> :
                    <>
                        <Box className={classes.inputContainer}>
                            <TextField
                                className={classes.inputField}
                                value={input}
                                onChange={handleInputChange}
                                onKeyPress={(e) => {
                                    if (e.key === "Enter") {
                                        handleSendMessage();
                                    }
                                }}
                                placeholder="发送消息"
                            />
                            <IconButton onClick={toggleEmojiPicker}>
                                <EmojiEmotions />
                            </IconButton>
                            <IconButton onClick={handleSendMessage}>
                                <Send />
                            </IconButton>
                        </Box>
                        {emojiPickerVisible && (
                            <div className={classes.emojiPicker} >
                                <EmojiPicker onEmojiClick={handleEmojiSelect} />
                            </div>
                        )}
                    </>}
        </Box >
    );
};


const SystemMessage = () => {
    return (
        <Card >
            <CardContent sx={{ paddingBottom: "10px !important" }}>
                <Typography variant="body2" gutterBottom color={"orange"}>
                    平台会依法对直播内容进行24小时巡查，禁止传播违法违规、封建迷信、暴力血腥、低俗色情、招嫖诈骗、违禁品等不良信息，禁止未成年人直播或打赏，坚决维护青少年群体精神文明健康。请勿轻信各类招聘征婚、代练代抽、刷钻、购买礼包码、游戏币等广告信息，且如主播在推广商品中诱导私下交易，请谨慎判断，以免上当受骗。
                </Typography>
                <Box display="flex" alignItems="center" >
                    <Warning color="error" />
                    <Typography variant="body2" color="error" style={{ marginLeft: '8px' }}>
                        禁止未成年人在直播间消费
                    </Typography>
                </Box>
            </CardContent>
        </Card>
    );
};


export default BarrageChat;