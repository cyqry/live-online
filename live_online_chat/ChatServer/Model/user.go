package Model

type User struct {
	Id       int64  `json:"id"`
	Nickname string `json:"nickname"`
	Role     int8   `json:"role"`
	Avatar   string `json:"avatar"`
}

type Anchor struct {
	Id          int64  `json:"id"`
	Description string `json:"description"`
}

type UserMessage struct {
	Message string `json:"message"`
	User    User   `json:"user"`
}
