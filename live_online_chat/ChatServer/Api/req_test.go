package Api

import (
	"ChatServer/Api/LiveSpaceApi"
	"fmt"
	"testing"
)

type User struct {
	Id       int64  `json:"id"`
	Nickname string `json:"nickname"`
}

func TestReq(t *testing.T) {
	//data := []int64{
	//	1,
	//	7,
	//}

	roomInfo, err := LiveSpaceApi.GetRoomInfoById(11)
	if err != nil {
		fmt.Printf("%v\n", err)
	}
	fmt.Printf("%v", roomInfo)

	//res, err := SendRequestWithConfig[[]User]("localhost:8079", "post", "getUserByIds", data, &config)
	//if err != nil {
	//	println("错误")
	//	fmt.Println(err)
	//	return
	//}

}
