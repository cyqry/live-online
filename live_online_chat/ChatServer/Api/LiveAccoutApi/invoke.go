package LiveAccoutApi

import (
	"ChatServer/Gws/Invoke"
	"ChatServer/Model"
	"fmt"
)

func GetLoginUser(cookie string) (*Model.User, error) {
	user, err := gateWayAccountRequestWithConfig[Model.User]("get", "/getUser", nil, &Invoke.Config{
		Form: true,
		CustomHeaders: map[string]string{
			"Cookie": cookie,
		},
	})
	if err != nil {
		return nil, err
	}
	return user, nil
}

func GetAnchor(cookie string) (*Model.Anchor, error) {
	anchor, err := gateWayAccountRequestWithConfig[Model.Anchor]("get", "/getAnchor", nil, &Invoke.Config{
		Form: true,
		CustomHeaders: map[string]string{
			"Cookie": cookie,
		},
	})
	if err != nil {
		return nil, err
	}
	return anchor, nil
}

func GetUsersByIds(userIds []int64) (*[]Model.User, error) {
	if userIds == nil {
		return nil, fmt.Errorf("参数错误")
	}
	res, err := accountRequest[[]Model.User]("get", "/getUserIds", userIds)
	if err != nil {
		return nil, err
	}
	return res, nil
}

func gateWayAccountRequestWithConfig[T any](method, path string, data interface{}, config *Invoke.Config) (*T, error) {
	config.Security = true
	return Invoke.SendRequestWithConfig[T]("localhost:8080", method, "/account"+path, data, config)
}

func accountRequestWithConfig[T any](method, path string, data interface{}, config *Invoke.Config) (*T, error) {
	return Invoke.SendRequestWithConfig[T]("localhost:8079", method, path, data, config)
}
func accountRequest[T any](method, path string, data interface{}) (*T, error) {
	return Invoke.SendRequest[T]("localhost:8079", method, path, data)
}
