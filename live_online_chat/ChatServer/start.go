package main

import (
	"ChatServer/Gws"
	"github.com/gin-gonic/gin"
)

func main() {
	gin.SetMode("debug")
	e := gin.Default()
	e.Use(CorsHandler())
	Gws.WsServerHandler(e)
	WebServer(e)
	e.Run(":8000")
}
