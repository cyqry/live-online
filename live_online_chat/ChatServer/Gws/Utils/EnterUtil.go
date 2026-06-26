package Utils

import (
	"ChatServer/Model"
	"fmt"
	"github.com/lxzan/gws"
)

func TakeSomeOfConn(conn *gws.Conn) (int8, int64, int64) {
	value, exist1 := conn.SessionStorage.Load("who")
	who := value.(int8)
	value, exist2 := conn.SessionStorage.Load("id")
	id := value.(int64)
	value, exist3 := conn.SessionStorage.Load("roomId")
	roomId := value.(int64)
	if !exist1 || !exist2 || !exist3 {
		panic("绝对不可能!")
	}
	return who, id, roomId
}
func TackUserOfConn(conn *gws.Conn) (*Model.User, error) {
	value, exist := conn.SessionStorage.Load("user")
	if !exist {
		return nil, fmt.Errorf("不是已登录用户")
	}

	if user, ok := value.(*Model.User); ok {
		return user, nil
	}
	return nil, fmt.Errorf("这就奇怪了")
}
