package Store

import (
	"ChatServer/Gws/Utils"
	"ChatServer/Model"
	"fmt"
	"github.com/lxzan/gws"
	"log"
	"sync"
)

type Room struct {
	lock           *sync.RWMutex
	anchorConn     *gws.Conn
	userConnMap    *gws.ConcurrentMap
	visitorConnMap *gws.ConcurrentMap
	roomInfo       *Model.RoomInfo
	banPostList    *gws.ConcurrentMap
}

func NewRoom(roomInfo *Model.RoomInfo) *Room {
	r := &Room{
		lock:           &sync.RWMutex{},
		userConnMap:    gws.NewConcurrentMap(16),
		visitorConnMap: gws.NewConcurrentMap(16),
		roomInfo:       roomInfo,
		banPostList:    gws.NewConcurrentMap(16),
	}
	return r
}

func (r *Room) getRoomId() int64 {
	id := r.roomInfo.Id
	return id
}
func (r *Room) GetRoomId() int64 {
	r.lock.RLock()
	id := r.roomInfo.Id
	r.lock.RUnlock()
	return id
}

func (r *Room) AddBanPostUser(userId int64) {
	r.lock.Lock()
	r.banPostList.Store(userId, 1)
	r.lock.Unlock()
}
func (r *Room) DeleteBanPostUser(userId int64) {
	r.lock.Lock()
	r.banPostList.Delete(userId)
	r.lock.Unlock()
}

func (r *Room) ExistsBanPostUser(userId int64) bool {
	r.lock.RLock()
	var result bool
	r.banPostList.Range(func(key interface{}, value interface{}) bool {
		if key == userId {
			result = true
			return false
		}
		return true
	})
	r.lock.RUnlock()
	return result
}

func (r *Room) GetAnchorConn() *gws.Conn {
	r.lock.RLock()
	conn := r.anchorConn
	r.lock.RUnlock()
	return conn
}

// PutConn put方法返回旧值，交给上层处理
func (r *Room) PutConn(who int8, id int64, conn *gws.Conn) *gws.Conn {
	r.lock.Lock()

	if who == 1 {
		tmp := r.anchorConn
		r.anchorConn = conn
		r.lock.Unlock()
		//因为close方法会调用DeleteConn方法,而锁不能重入，所以要放在锁外面来关;这里一定要返回给最上层，因为close调的就是最上层方法
		return tmp
	}
	opMap := r.getOpMap(who)
	oldConn, ok := opMap.Load(id)
	opMap.Store(id, conn)
	r.lock.Unlock()
	//因为close方法会调用DeleteConn方法,而锁不能重入，所以要放在锁外面来关
	if ok {
		return oldConn.(*gws.Conn)
	}
	return nil
}
func (r *Room) GetConn(who int8, id int64) (*gws.Conn, error) {

	r.lock.RLock()
	if who == 1 {
		if r.anchorConn == nil {
			err := fmt.Errorf("该房间没有主播连接")
			r.lock.RUnlock()
			return nil, err
		}
		r.lock.RUnlock()
		return r.anchorConn, nil
	}
	opMap := r.getOpMap(who)
	value, exist := opMap.Load(id)

	if !exist {
		err := fmt.Errorf("没有这个连接！")
		r.lock.RUnlock()
		return nil, err
	}
	conn, ok := value.(*gws.Conn)
	if !ok {
		panic("数据存储错误导致取错误")
	}
	r.lock.RUnlock()
	return conn, nil
}
func (r *Room) DeleteConn(conn *gws.Conn) {
	who, id, _ := Utils.TakeSomeOfConn(conn)
	r.lock.Lock()
	if who == 1 {
		if r.anchorConn == nil {
			log.Printf("本来就不存在！")
		} //这里不要关闭流, 这里关闭流会造成，关闭流又会调用delete，就循环了，并且锁还不能重入，所以结果为死锁; 源码好像是WriteClose之后一定会执行onError逻辑，无论是否已经关闭流

		//由于可能是先拿到旧链接，再设置新连接，再删除旧连接，who和id是与原来一样的，所以这里要判断一下，要防止已经被别人改了
		if r.anchorConn == conn {
			r.anchorConn = nil
		}
		r.lock.Unlock()
		return
	}
	opMap := r.getOpMap(who)
	value, exist := opMap.Load(id)
	if exist && value.(*gws.Conn) == conn { //同上,不关闭旧流并判断是否为旧流，再删
		opMap.Delete(id)
	} else {
		log.Printf("本来就不存在！或者已被覆盖")
	}
	r.lock.Unlock()
}

// RangeAll 不要在f里面执行  Store下 调用了绑定在Room上方法的方法 和 Room上方法本身; 包装了range1,使得并发安全
func (r *Room) RangeAll(f func(*gws.Conn)) {
	r.lock.RLock()
	r.range1(1, f)
	r.range1(0, f)
	r.range1(-1, f)
	r.lock.RUnlock()
}

// f中可执行任何方法，除了 Gws.ConcurrentMap 上的方法；只能在在单线程上操作的room上使用
func (r *Room) range1(who int8, f func(*gws.Conn)) {
	if who == 1 {
		if r.anchorConn != nil {
			f(r.anchorConn)
		}
		return
	}
	opMap := r.getOpMap(who)
	opMap.Range(
		func(id interface{}, conn interface{}) bool {
			if c, ok := conn.(*gws.Conn); ok {
				f(c)
			} else {
				panic("map存数据错误!")
			}
			return true
		})
}

func (r *Room) getOpMap(who int8) *gws.ConcurrentMap {
	var opMap *gws.ConcurrentMap
	if who == 0 {
		opMap = r.userConnMap
	} else if who == -1 {
		opMap = r.visitorConnMap
	} else {
		panic("无效who参数")
	}
	return opMap
}
