
class RtcEntity {
    connection
    hasRemote
    iceTmp = []

    constructor(connection) {
        this.hasRemote = false
        this.iceTmp = []
        this.connection = connection
    }

    addIceToTmp = (ice) => {
        this.iceTmp.push(ice)
    }

    iceTmpForeach = (f) => {
        this.iceTmp.forEach(async ice => {
            await f(ice)
        })
    }

    close = () => {
        this.connection.close()
    }

}

class Letter {
    targetId
    targetWho
    msg
}

class UserRtcMap {
    constructor() {
        this.map = new Map();
        this.reverseMap = new Map();
    }

    forEach(f) {
        this.map.forEach((entity, id) => {
            f(id, entity)
        })
    }

    put(userId, rtcEntity) {
        this.map.set(userId, rtcEntity);
        this.reverseMap.set(rtcEntity, userId);
    }
    getRtcEntity(userId) {
        return this.map.get(userId);
    }
    getUserId(rtcEntity) {
        return this.reverseMap.get(rtcEntity);
    }
    removeByUserId(userId) {
        let rtcEntity = this.map.get(userId);
        if (rtcEntity) {
            this.map.delete(userId);
            this.reverseMap.delete(rtcEntity);
        }
    }
    removeByRtcEntity(rtcEntity) {
        let userId = this.map.get(rtcEntity);
        if (userId) {
            this.reverseMap.delete(rtcEntity);
            this.map.delete(userId);
        }
    }
    hasKey(key) {
        return this.map.has(key);
    }

    hasValue(value) {
        return this.reverseMap.has(value);
    }

    get keys() {
        return [...this.map.keys()];
    }

    get values() {
        return [...this.reverseMap.keys()];
    }

    get size() {
        return this.map.size;
    }
}


// class RoomRtcMap {
//     constructor() {
//         this.map = new Map();
//         this.reverseMap = new Map();
//     }

//     forEach(f) {
//         this.map.forEach((entity, id) => {
//             f(id, entity)
//         })
//     }

//     put(roomId, rtcEntity) {
//         this.map.set(roomId, rtcEntity);
//         this.reverseMap.set(rtcEntity, roomId);
//     }
//     getRtcEntity(roomId) {
//         return this.map.get(roomId);
//     }
//     getRoomId(rtcEntity) {
//         return this.reverseMap.get(rtcEntity);
//     }
//     removeByRoomId(roomId) {
//         let rtcEntity = this.map.get(roomId);
//         if (rtcEntity) {
//             this.map.delete(roomId);
//             this.reverseMap.delete(rtcEntity);
//         }
//     }
//     removeByRtcEntity(rtcEntity) {
//         let roomId = this.map.get(rtcEntity);
//         if (roomId) {
//             this.reverseMap.delete(rtcEntity);
//             this.map.delete(roomId);
//         }
//     }
//     hasKey(key) {
//         return this.map.has(key);
//     }

//     hasValue(value) {
//         return this.reverseMap.has(value);
//     }

//     get keys() {
//         return [...this.map.keys()];
//     }

//     get values() {
//         return [...this.reverseMap.keys()];
//     }

//     get size() {
//         return this.map.size;
//     }
// }

export {
    Letter, UserRtcMap, RtcEntity
}