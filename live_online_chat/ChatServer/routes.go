package main

import (
	"ChatServer/Controller"
	"github.com/gin-gonic/gin"
)

func WebServer(e *gin.Engine) {
	e.POST("/banedPost", Controller.BannedPost)
	e.GET("/isBanedPost", Controller.IsBanedPost)
	e.POST("/unBanedPost", Controller.UnBanedPost)
}
