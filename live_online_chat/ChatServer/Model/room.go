package Model

type RoomInfo struct {
	Id       int64 `json:"id"`
	AnchorId int64 `json:"anchorId"`
}

func NewRoomInfo(roomId int64, anchorId int64) *RoomInfo {
	return &RoomInfo{
		Id:       roomId,
		AnchorId: anchorId,
	}
}
