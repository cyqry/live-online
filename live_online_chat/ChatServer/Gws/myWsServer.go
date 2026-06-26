package Gws

import (
	"ChatServer/Common"
	"ChatServer/Gws/Handler"
	"ChatServer/Gws/Store"
	"ChatServer/Gws/Utils"
	"ChatServer/Model"
	"encoding/json"
	"fmt"
	"github.com/gin-gonic/gin"
	"github.com/lxzan/gws"
	"log"
	"net/http"
	"strconv"
	"time"
)

func WsServerHandler(e *gin.Engine) {
	var upgrader = gws.NewUpgrader(new(WebsocketEvent), &gws.ServerOption{
		CompressEnabled:     true,
		CheckUtf8Enabled:    true,
		ReadMaxPayloadSize:  32 * 1024 * 1024,
		WriteMaxPayloadSize: 32 * 1024 * 1024,
		ReadAsyncEnabled:    true,
		ReadBufferSize:      4 * 1024,
		CheckOrigin:         CheckOrigin,
	})

	e.GET("/chat/:id/:roomId", func(context *gin.Context) {
		log.Printf("出现!")
		socket, err := upgrader.Accept(context.Writer, context.Request)
		if err != nil {
			return
		}
		socket.Listen()
	})
	//http.HandleFunc("/", func(writer http.ResponseWriter, request *http.Request) {
	//
	//})
}

func CheckOrigin(r *http.Request, session gws.SessionStorage) bool {

	//路径合法性校验
	_, e := Handler.TakeParamOfPath(r.URL.Path, 2)
	if e == nil {
		return false
	}

	path1, err1 := Handler.TakeParamOfPath(r.URL.Path, 0)
	path2, err2 := Handler.TakeParamOfPath(r.URL.Path, 1)
	v1, err3 := strconv.Atoi(path1)
	v2, err4 := strconv.Atoi(path2)
	if err1 != nil || err2 != nil || err3 != nil || err4 != nil {
		log.Printf("路径类型错误")
		return false
	}
	roomId := int64(v1)
	id := int64(v2)

	//用户检验
	who, user, e := Handler.VerifyAndGet(r)
	if e != nil {
		log.Printf("错误:%v", e)
		return false
	}
	fmt.Println("与", id, "握手开始")

	roomInfo, remoteExist := Handler.RemoteRoomInfo(roomId)
	//房间同步执行
	Handler.RoomSync(roomId, roomInfo)
	if !remoteExist {
		log.Printf("房间 %d就不存在！", roomId)
		return false
	}

	if user != nil {
		session.Store("user", user)
	}

	switch who {
	case 1:
		if roomInfo.AnchorId != id {
			log.Printf("主播作为观众进入聊天,user:%v", *user)
		} else {
			log.Printf("主播进入聊天,user:%v", *user)
		}
	case 0:
		log.Printf("已登录用户进入聊天,user:%v", *user)
	case -1:
		log.Println("游客进入聊天")
	}

	//todo: 修复主播进自己的直播间当观众，自己直播间的聊天连接会被挤掉
	if who == 1 && roomInfo.AnchorId != id {
		who = 0
	}
	session.Store("who", who)
	session.Store("id", id)
	session.Store("roomId", roomId)

	fmt.Println("与 who:", who, "id:", id, "握手完成")
	return true
}

type WebsocketEvent struct{}

func (c *WebsocketEvent) OnClose(socket *gws.Conn, code uint16, reason []byte) {
	fmt.Printf("onclose: code=%d, payload=%s\n", code, string(reason))
	Store.DeleteConn(socket)
}

func (c *WebsocketEvent) OnError(socket *gws.Conn, err error) {
	fmt.Printf("onerror: err=%s\n", err.Error())
	Store.DeleteConn(socket)
}

func (c *WebsocketEvent) OnOpen(socket *gws.Conn) {
	log.Printf("open socket: %v\n", socket)
	//设置 读写（消息而非ping pong）事件未发生 超时的时间，零值为无穷大
	err := socket.SetDeadline(time.Time{})
	if err != nil {
		println("读写超时设置失败")
		socket.WriteClose(0, nil)
		return
	}
	who, id, roomId := Utils.TakeSomeOfConn(socket)
	go heartbeat(socket)
	Store.PutConn(roomId, who, id, socket, func(oldConn *gws.Conn) {
		if oldConn != nil {
			oldConn.WriteClose(1000, []byte("connection replace"))
		}
	})
	log.Printf("who:%d,id:%d : connected\n", who, id)
	println("connected")

}

func heartbeat(socket *gws.Conn) {
	for {
		time.Sleep(Common.PingInterval)
		err := socket.WritePing([]byte("heartbeat"))
		if err != nil {
			return
		}
	}
}

func (c *WebsocketEvent) OnPing(socket *gws.Conn, payload []byte) {
	fmt.Printf("onping: payload=%s\n", string(payload))
	err := socket.WritePong(payload)
	if err != nil {
		return
	}
}

func (c *WebsocketEvent) OnPong(socket *gws.Conn, payload []byte) {
	fmt.Printf("OnPong: payload=%s\n", string(payload))
}

func (c *WebsocketEvent) OnMessage(socket *gws.Conn, message *gws.Message) {
	defer message.Close()
	fmt.Printf("socket: %v\n", socket)
	fmt.Printf("msg: %v\n", message)
	fmt.Printf("session: %v\n", socket.SessionStorage)

	senderWho, senderId, senderRoomId := Utils.TakeSomeOfConn(socket)
	user, err := Utils.TackUserOfConn(socket)
	if err != nil {
		log.Printf("游客不允许发消息")
		return
	}

	room, b := Store.GetRoom(senderRoomId)
	if !b {
		panic("神奇事件发生!")
		return
	}

	if room.ExistsBanPostUser(senderId) {
		log.Printf("被禁言的用户")
		return
	}

	//todo，判断大小
	message.Data.Bytes()
	room.RangeAll(func(conn *gws.Conn) {
		targetWho, targetId, targetRoomId := Utils.TakeSomeOfConn(conn)

		//no need code
		if senderRoomId != targetRoomId {
			log.Fatal("  senderRoomId != targetRoomId 大错误")
		}
		if user.Id != senderId {
			log.Fatal(" user.Id != senderId  大错误")
		} //no need code//

		if !(targetWho == senderWho && targetId == senderId) {
			msg := message.Data.String()
			userMessageBytes, err2 := json.Marshal(Model.UserMessage{
				Message: msg,
				User:    Model.User{Id: senderId, Nickname: user.Nickname, Role: user.Role, Avatar: user.Avatar},
			})
			if err2 != nil {
				log.Printf("发送的消息序列化失败!")
				return
			}
			err := conn.WriteAsync(message.Opcode, userMessageBytes)
			if err != nil {
				log.Printf("在房间 %d 向 who:%d,id:%d : 发消息%s错误: %s\n", targetRoomId, targetWho, targetId, string(userMessageBytes), err)
				return
			}
		}
	})
	//socket.WriteMessage(message.Opcode, message.Data.Bytes())
}
