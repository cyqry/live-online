package Store

import (
	"ChatServer/Gws/Utils"
	"fmt"
	"github.com/lxzan/gws"
	"log"
	"sync"
)

var rooms = gws.NewConcurrentMap(16)
var lock sync.RWMutex

func AddRoom(room *Room, postOldConnHandler func(conn *gws.Conn)) *Room {
	lock.Lock()
	var oldRoom *Room
	value, exist := rooms.Load(room.getRoomId())
	if exist {
		oldRoom = value.(*Room)
		log.Printf("之前有的房间 %d 被替换!", oldRoom.GetRoomId())
	}
	rooms.Store(room.GetRoomId(), room)
	log.Println("add room:", room.roomInfo)
	lock.Unlock()
	//由于oldRoom已被抛弃，所以只会在这处理，所以无需加锁
	if postOldConnHandler != nil && oldRoom != nil {
		oldRoom.range1(1, postOldConnHandler)
		oldRoom.range1(0, postOldConnHandler)
		oldRoom.range1(-1, postOldConnHandler)
	}
	return oldRoom
}

func DeleteRoom(roomId int64) {
	lock.Lock()
	value, exist := rooms.Load(roomId)
	if !exist {
		log.Printf("roomId: %d 本来就不存在！", roomId)
		lock.Unlock()
		return
	}
	room := value.(*Room)
	rooms.Delete(roomId)
	lock.Unlock()
	f := func(conn *gws.Conn) {
		conn.WriteClose(0, nil)
	}
	room.range1(1, f)
	room.range1(0, f)
	room.range1(-1, f)
}

func getRoom(roomId int64) (*Room, bool) {
	value, exist := rooms.Load(roomId)
	if !exist {
		return nil, false
	}
	room := value.(*Room)
	return room, true
}

func GetRoom(roomId int64) (*Room, bool) {

	lock.RLock()
	value, exist := rooms.Load(roomId)
	if !exist {
		lock.RUnlock()
		return nil, false
	}
	room := value.(*Room)
	lock.RUnlock()
	return room, true
}

func PutConn(roomId int64, who int8, id int64, conn *gws.Conn, postHandle func(oldConn *gws.Conn)) *gws.Conn {
	lock.Lock()
	room, exists := getRoom(roomId)

	if !exists {
		log.Printf("PutConn  roomId: %d 不存在！", roomId)
		lock.Unlock()
		return nil
	}
	println("tag 0.1")
	oldConn := room.PutConn(who, id, conn)
	lock.Unlock()
	println("tag 0.2")
	if postHandle != nil {
		postHandle(oldConn)
	}

	return oldConn
}
func GetConn(who int8, id int64) (*gws.Conn, error) {
	var result *gws.Conn
	var err = fmt.Errorf("完全找不到！")
	lock.RLock()
	rooms.Range(func(key interface{}, value interface{}) bool {
		room := value.(*Room)
		conn, err2 := room.GetConn(who, id)
		err = err2
		result = conn
		if err == nil {
			//存在，就不用找了
			return false
		}
		return true
	})
	lock.RUnlock()
	return result, err
}

func GetConnInRoom(roomId int64, who int8, id int64) (*gws.Conn, error) {
	lock.RLock()
	room, exists := getRoom(roomId)
	if !exists {
		log.Printf("GetConnInRoom roomId: %d 不存在！", roomId)
		lock.RUnlock()
		return nil, fmt.Errorf("不存在！")
	}
	conn, err := room.GetConn(who, id)
	lock.RUnlock()
	return conn, err
}
func DeleteConn(conn *gws.Conn) {
	lock.Lock()
	_, id, roomId := Utils.TakeSomeOfConn(conn)
	room, b := getRoom(roomId)
	if !b {
		log.Printf("DeleteConn roomId: %d 不存在！", roomId)
		lock.Unlock()
		return
	}
	room.DeleteConn(conn)
	lock.Unlock()
	log.Println("tag 1.2")
	log.Printf("roomId: %d 删除了连接 %d", roomId, id)
}
