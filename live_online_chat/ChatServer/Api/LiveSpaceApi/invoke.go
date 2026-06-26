package LiveSpaceApi

import (
	"ChatServer/Gws/Invoke"
	"ChatServer/Model"
	"fmt"
	"strconv"
)

func GetRoomInfoById(roomId int64) (*Model.RoomInfo, error) {
	config := Invoke.Config{Remote: true}
	return spaceRequestWithConfig[Model.RoomInfo]("get", "/getRoomInfo", map[string]string{"roomId": fmt.Sprintf("%v", roomId)}, &config)
}

func GenerateBannedPostRecord(roomId int64, userId int64, operatingType int32, cookie string) (string, error) {
	msg, err := gateWayAccountRequestWithConfigReturnString("post", "/generateBannedPostRecord", map[string]string{
		"roomId": strconv.FormatInt(roomId, 10),
		"userId": strconv.FormatInt(userId, 10),
		"type":   strconv.FormatInt(int64(operatingType), 10),
	}, &Invoke.Config{
		Form: true,
		CustomHeaders: map[string]string{
			"Cookie": cookie,
		},
		Remote: true,
	})
	if err != nil {
		return "", err
	}
	return msg, nil
}
func gateWayAccountRequestWithConfigReturnString(method, path string, data interface{}, config *Invoke.Config) (string, error) {
	config.Security = true

	//return "ok", nil
	return Invoke.SendRequestWithConfigReturnString("localhost:8080", method, "/space"+path, data, config)
}

func spaceRequestWithConfig[T any](method, path string, data interface{}, config *Invoke.Config) (*T, error) {
	return Invoke.SendRequestWithConfig[T]("localhost:8081", method, path, data, config)
}
func spaceRequest[T any](method, path string, data interface{}) (*T, error) {
	return Invoke.SendRequest[T]("localhost:8081", method, path, data)
}
