package Util

import (
	"errors"
	"net/http"
)

func TakeCookieStringByRequest(r *http.Request) (string, error) {
	var cookie string
	for _, c := range r.Cookies() {
		if c.Name == "live_session" {
			cookie = c.String()
			break
		}
	}
	if cookie == "" {
		return "", errors.New("missing cookie")
	}
	return cookie, nil
}
