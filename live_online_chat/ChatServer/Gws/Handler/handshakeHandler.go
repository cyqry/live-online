package Handler

import (
	"ChatServer/Api/LiveAccoutApi"
	"ChatServer/Api/LiveSpaceApi"
	"ChatServer/Gws/Store"
	Model2 "ChatServer/Model"
	"ChatServer/Util"
	"errors"
	"fmt"
	"github.com/lxzan/gws"
	"log"
	"net/http"
	"strconv"
	"strings"
)

// VerifyAndGet 验证id和cookie的正确性,返回who
func VerifyAndGet(r *http.Request) (int8, *Model2.User, error) {
	cookie, e := Util.TakeCookieStringByRequest(r)
	if e != nil {
		return -1, nil, nil
	}
	user, err2 := LiveAccoutApi.GetLoginUser(cookie)
	if err2 != nil {
		log.Printf("错误是getUser错误: %v", err2)
		return -1, nil, nil
	}
	path, err1 := TakeParamOfPath(r.URL.Path, 1)
	id, err2 := strconv.Atoi(path)
	if err1 != nil || err2 != nil {
		log.Printf("错误是路径错误: err1:%v ,err2:%v", err1, err2)
		return -9, nil, errors.New("路径不合法")
	}
	if int64(id) != user.Id {
		log.Printf("错误是id不等, id: %v,userId:%v  ", id, user.Id)
		return -9, nil, errors.New("id不合法")
	}
	_, err := LiveAccoutApi.GetAnchor(cookie)
	if err != nil {
		return 0, user, nil
	}
	return 1, user, nil
}

func TakeParamOfPath(path string, i int) (string, error) {
	split := strings.Split(path, "/")
	fmt.Printf("%v\n", split)
	if len(split) <= 2+i {
		//让上传
		return "", errors.New("传入路径错误")
	}
	return split[i+2], nil
}

func RoomSync(roomId int64, remoteRoomInfo *Model2.RoomInfo) {
	//执行与远端房间信息的一个同步
	//java那边有而这里没有则创建，
	//java那边没有而这里有则删除
	//这样判断会导致只有新连接来了之后，才能知道房间还在不在;反正聊天房间，只能多不能少

	_, localExist := Store.GetRoom(roomId)
	if remoteRoomInfo != nil && !localExist {
		room := Store.NewRoom(remoteRoomInfo)
		log.Println("添加聊天房间")
		Store.AddRoom(room, func(oldConn *gws.Conn) {
			oldConn.WriteClose(1000, []byte("添加房间时被替换的原房间连接"))
		})
	}
	if remoteRoomInfo == nil && localExist {
		log.Println("删除聊天房间")
		Store.DeleteRoom(roomId)
	}
}

func RemoteRoomInfo(roomId int64) (*Model2.RoomInfo, bool) {
	roomInfo, err := LiveSpaceApi.GetRoomInfoById(roomId)
	if err != nil {
		return nil, false
	}
	return roomInfo, true
}
