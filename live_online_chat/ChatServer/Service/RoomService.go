package Service

import "ChatServer/Gws/Store"

func BanedPost(roomId int64, userId int64) bool {
	room, b := Store.GetRoom(roomId)
	if !b {
		return false
	}
	room.AddBanPostUser(userId)
	return true
}

func UnBanedPost(roomId int64, userId int64) bool {
	room, b := Store.GetRoom(roomId)
	if !b {
		return false
	}
	room.DeleteBanPostUser(userId)
	return true
}

func IsBanedPost(roomId int64, userId int64) bool {
	room, b := Store.GetRoom(roomId)
	if !b {
		return false
	}
	return room.ExistsBanPostUser(userId)
}
