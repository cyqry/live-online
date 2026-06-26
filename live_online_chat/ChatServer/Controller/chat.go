package Controller

import (
	"ChatServer/Api/LiveSpaceApi"
	"ChatServer/Gws/Store"
	"ChatServer/Service"
	"ChatServer/Util"
	"github.com/gin-gonic/gin"
	"log"
	"net/http"
	"strconv"
)

func BannedPost(ctx *gin.Context) {

	cookieString, err := Util.TakeCookieStringByRequest(ctx.Request)
	if err != nil {
		ctx.String(http.StatusBadRequest, "未登录")
		return
	}

	roomId, e1 := strconv.ParseInt(ctx.PostForm("roomId"), 10, 64)
	userId, e2 := strconv.ParseInt(ctx.PostForm("userId"), 10, 64)

	if e1 != nil || e2 != nil {
		ctx.String(http.StatusBadRequest, "错误的参数!")
		return
	}
	if _, b := Store.GetRoom(roomId); !b {
		log.Printf("未找到要禁言的房间")
		ctx.String(http.StatusBadRequest, "禁言失败")
		return
	}

	//生成记录成功则说明校验正确
	if Service.IsBanedPost(roomId, userId) {
		ctx.String(http.StatusBadRequest, "用户已经被禁言")
		return
	}

	msg, err := LiveSpaceApi.GenerateBannedPostRecord(roomId, userId, 0, cookieString)
	if err != nil {
		var returnMsg string
		if err.Error() == "" {
			returnMsg = "操作失败!"
		} else {
			returnMsg = err.Error()
		}
		ctx.String(http.StatusBadRequest, returnMsg)
		return
	}

	if !Service.BanedPost(roomId, userId) {
		log.Printf("刚刚查了有房间现在又没有")
		ctx.String(http.StatusBadRequest, "禁言失败")
		return
	}

	ctx.String(http.StatusOK, msg)
}

func IsBanedPost(ctx *gin.Context) {
	roomId, e1 := strconv.ParseInt(ctx.Query("roomId"), 10, 64)
	userId, e2 := strconv.ParseInt(ctx.Query("userId"), 10, 64)
	if e1 != nil || e2 != nil {
		ctx.String(http.StatusBadRequest, "错误的请求")
	}
	room, b := Store.GetRoom(roomId)
	if !b {
		ctx.String(http.StatusBadRequest, "没有这个房间!")
		return
	}
	result := "false"
	if room.ExistsBanPostUser(userId) {
		result = "true"
	}
	ctx.String(http.StatusOK, result)
}

func UnBanedPost(ctx *gin.Context) {
	cookieString, err := Util.TakeCookieStringByRequest(ctx.Request)
	if err != nil {
		ctx.String(http.StatusBadRequest, "未登录")
		return
	}

	roomId, e1 := strconv.ParseInt(ctx.PostForm("roomId"), 10, 64)
	userId, e2 := strconv.ParseInt(ctx.PostForm("userId"), 10, 64)

	if e1 != nil || e2 != nil {
		ctx.String(http.StatusBadRequest, "错误的参数!")
		return
	}

	if _, b := Store.GetRoom(roomId); !b {
		log.Printf("未找到要解禁言的房间")
		ctx.String(http.StatusBadRequest, "解禁言失败")
		return
	}
	if !Service.IsBanedPost(roomId, userId) {
		ctx.String(http.StatusBadRequest, "用户未被禁言")
		return
	}

	//生成记录成功则说明校验正确
	msg, err := LiveSpaceApi.GenerateBannedPostRecord(roomId, userId, 1, cookieString)
	if err != nil {
		var returnMsg string
		if err.Error() == "" {
			returnMsg = "操作失败!"
		} else {
			returnMsg = err.Error()
		}
		ctx.String(http.StatusBadRequest, returnMsg)
		return
	}

	if !Service.UnBanedPost(roomId, userId) {
		log.Printf("刚刚查了有房间现在又没有")
		ctx.String(http.StatusBadRequest, "解禁言失败")
		return
	}

	ctx.String(http.StatusOK, msg)
}
