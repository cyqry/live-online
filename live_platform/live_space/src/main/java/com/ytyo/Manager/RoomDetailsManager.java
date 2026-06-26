package com.ytyo.Manager;

import com.ytyo.Model.RoomDetails;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import com.ytyo.Option.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.lang.reflect.Field;
import java.util.*;

import static com.ytyo.Utils.BeanUtil.extractNonNullFields;
import static com.ytyo.Utils.BeanUtil.updateNonNullFields;

@Component
@Slf4j
public class RoomDetailsManager {

    private final HashOperations<String, Object, Object> opHash;
    private final StringRedisSerializer stringRedisSerializer;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RLock writeLock;
    private final RLock readLock;

    private static final String ROOM_DETAILS_PREFIX = "roomDetails:";

    public RoomDetailsManager(HashOperations<String, Object, Object> opHash, Redisson redisson, StringRedisSerializer stringRedisSerializer, RedisTemplate<String, Object> redisTemplate) {
        this.opHash = opHash;
        this.stringRedisSerializer = stringRedisSerializer;
        this.redisTemplate = redisTemplate;
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("lockkey");
        writeLock = readWriteLock.writeLock();
        readLock = readWriteLock.readLock();
    }

    /**
     * @param target 旧的对象
     * @param source 更新条件对象
     * @return 被更新的字段组成的对象
     * @throws IllegalAccessException
     */
    private RoomDetails getNonNullFieldsOld(RoomDetails target, RoomDetails source) throws IllegalAccessException {
        if (target == null || source == null) {
            throw new IllegalArgumentException("Target and source objects cannot be null.");
        }
        RoomDetails result = new RoomDetails();
        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object sourceValue = field.get(source);
            if (sourceValue != null) {
                field.set(result, field.get(target));
            }
        }
        return result;
    }


    private void setFieldValue(RoomDetails roomDetails, String fieldName, Object value) {
        try {
            Field field = roomDetails.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(roomDetails, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    //返回完整的旧room
    private Result<Option<RoomDetails>, Exception> putRoomDetailsUnsafe(long roomId, RoomDetails roomDetails, boolean updated) {
        Option<RoomDetails> oldRoom = getRoomDetailsUnSafe(roomId);
        if (updated && oldRoom.isNone()) {
            return Result.Err(new RuntimeException("更新房间失败!不存在的房间"));
        }
        Map<String, Object> roomDetailsMap = extractNonNullFields(roomDetails);
        opHash.putAll(ROOM_DETAILS_PREFIX + roomId, roomDetailsMap);
        return Result.Ok(oldRoom);
    }


    private Option<RoomDetails> getRoomDetailsUnSafe(long roomId) {
        Map<Object, Object> entries = opHash.entries(ROOM_DETAILS_PREFIX + roomId);
        if (entries.size() == 0) {
            return Option.None();
        }
        RoomDetails roomDetails = new RoomDetails();
        entries.forEach((k, v) -> setFieldValue(roomDetails, k.toString(), v));
        return Option.Some(roomDetails);
    }

    @Test
    public void test() throws NoneException {
        System.out.println(getAllRoomDetails().unwrap());
    }


    public Option<RoomDetails.RoomInfo> getRoomInfoById(long roomId) {
        readLock.lock();
        Object o = opHash.get(ROOM_DETAILS_PREFIX + roomId, "roomInfo");
        readLock.unlock();
        if (o == null)
            return Option.None();
        if (o instanceof RoomDetails.RoomInfo roomInfo) {
            return Option.Some(roomInfo);
        }
        return Option.None();
    }


    public Option<List<RoomDetails>> getRoomDetailsByRoomIds(Long... roomIds) {
        if (roomIds == null)
            return Option.None();
        if (roomIds.length == 0)
            return Option.Some(new ArrayList<>());

        HashSet<String> roomKeys = new HashSet<>();
        for (Long roomId : roomIds) {
            roomKeys.add(ROOM_DETAILS_PREFIX + roomId);
        }

        readLock.lock();
        List<RoomDetails> roomDetails;
        try {
            roomDetails = new ArrayList<>();
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (String userKey : roomKeys) {
                    byte[] bytes = stringRedisSerializer.serialize(userKey);
                    if (bytes != null)
                        connection.hashCommands().hGetAll(bytes);
                    else {
                        System.out.println("神奇");
                    }
                }
                return null;
            }).forEach(result -> {
                RoomDetails roomDetail = new RoomDetails();
                Map<String, Object> resultMap = (Map<String, Object>) result;
                if (resultMap.isEmpty()) {
                    return;
                }
                resultMap.forEach((key, value) -> setFieldValue(roomDetail, key, value));
                roomDetails.add(roomDetail);
            });

            return Option.Some(roomDetails);
        } catch (Exception e) {
            log.error("查询多RoomDetails失败", e);
            return Option.None();
        } finally {
            readLock.unlock();
        }


    }

    public boolean updateRoomInfo(RoomDetails.RoomInfo roomInfo) {
        if (roomInfo == null || roomInfo.getId() == null) {
            return false;
        }

        writeLock.lock();
        try {
            Object o = opHash.get(ROOM_DETAILS_PREFIX + roomInfo.getId(), "roomInfo");
            if (o == null) {
                return false;
            }
            if (o instanceof RoomDetails.RoomInfo oldRoomInfo) {
                updateNonNullFields(roomInfo, oldRoomInfo);
                opHash.put(ROOM_DETAILS_PREFIX + roomInfo.getId(), "roomInfo", oldRoomInfo);
                return true;
            } else {
                log.error("存储类型错误！");
                return false;
            }
        } catch (Exception e) {
            log.error("updateRoomInfo失败", e);
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    public Result<Option<RoomDetails>, Exception> updateRoomDetails(long roomId, RoomDetails roomDetails) {
        writeLock.lock();
        Result<Option<RoomDetails>, Exception> oldRoomDetail = putRoomDetailsUnsafe(roomId, roomDetails, true);
        writeLock.unlock();
        if (oldRoomDetail.isErr())//更新失败,之前没有这个房间
            return Result.Err(new RuntimeException("没有这个房间!"));
        else {
            try {
                if (oldRoomDetail.unwrap().isSome()) {
                    return Result.Ok(Option.Some(getNonNullFieldsOld(oldRoomDetail.unwrap().unwrap(), roomDetails)));
                } else {
                    return Result.Ok(Option.None());
                }
            } catch (Exception e) {
                return Result.Err(e);
            }
        }
    }

    //没有就是key不存在在或io失败
    public Option<RoomDetails> getRoomDetails(long roomId) {
        readLock.lock();
        try {
            return getRoomDetailsUnSafe(roomId);
        } finally {
            readLock.unlock();
        }
    }


    public Result<Option<RoomDetails>, Exception> putRoomDetails(@Validated RoomDetails roomDetails) {
        if (roomDetails == null || roomDetails.getRoomInfo() == null || roomDetails.getRoomInfo().getId() == null)
            return Result.Err(new RuntimeException("参数错误!"));

        Long roomId = roomDetails.getRoomInfo().getId();
        try {
            writeLock.lock();
            return putRoomDetailsUnsafe(roomId, roomDetails, false);
        } finally {
            writeLock.unlock();
        }
    }


    public Option<RoomDetails> deleteRoomDetails(Long roomId) {
        if (roomId == null)
            return Option.None();
        writeLock.lock();//lock放在try外
        try {
            Option<RoomDetails> oldRoomDetails = getRoomDetailsUnSafe(roomId);
            if (oldRoomDetails.isSome()) {
                redisTemplate.delete(ROOM_DETAILS_PREFIX + roomId);
            }
            return oldRoomDetails;
        } finally {
            writeLock.unlock();
        }
    }


    public Option<List<RoomDetails>> getAllRoomDetails() {
        readLock.lock();
        List<RoomDetails> roomDetails;
        try {
            Set<String> roomKeys = redisTemplate.keys(ROOM_DETAILS_PREFIX + "*");
            if (roomKeys == null || roomKeys.size() == 0) {
                return Option.None();
            }

            roomDetails = new ArrayList<>();
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (String userKey : roomKeys) {
                    byte[] bytes = stringRedisSerializer.serialize(userKey);
                    if (bytes != null)
                        connection.hashCommands().hGetAll(bytes);
                    else {
                        System.out.println("神奇");
                    }
                }
                return null;
            }).forEach(result -> {
                RoomDetails roomDetail = new RoomDetails();
                Map<String, Object> resultMap = (Map<String, Object>) result;
                if (resultMap.isEmpty()) {
                    return;
                }
                resultMap.forEach((key, value) -> setFieldValue(roomDetail, key, value));
                roomDetails.add(roomDetail);
            });
            return Option.Some(roomDetails);
        } catch (Exception e) {
            log.error("getAllRoomDetails错误", e);
            return Option.None();
        } finally {
            readLock.unlock();
        }


    }

    /**
     * @return -2 :房间不存在，-1：无旧id ，其他: 旧id
     */
    public long addMember(long roomId, short who, long id) {
        writeLock.lock();
        try {
            Option<RoomDetails> roomDetailsOption = getRoomDetailsUnSafe(roomId);
            if (roomDetailsOption.isNone()) {
                return -2;
            }
            RoomDetails roomDetails = roomDetailsOption.unwrap();
            roomDetails.setCoverSrc(null);
            if (who == 0) {
                roomDetails.setVisitorList(null);
                roomDetails.setRoomInfo(null);
            } else if (who == -1) {
                roomDetails.setUserList(null);
                roomDetails.setRoomInfo(null);
            } else {
                throw new IllegalStateException("错误的who");
            }
            long i = roomDetails.addMember(id, who);
            //更新
            return putRoomDetailsUnsafe(roomId, roomDetails, true).isOk() ? i : -2;
        } catch (NoneException e) {
            throw new RuntimeException("神奇！！");
        } finally {
            writeLock.unlock();
        }
    }


    /**
     * @return -1：没有这个房间 或 房间里之前不存在这个成员；   其他: 旧id
     */
    public long deleteMember(long roomId, short who, long id) {
        writeLock.lock();
        try {
            Option<RoomDetails> roomDetailsOption = getRoomDetailsUnSafe(roomId);
            if (roomDetailsOption.isNone()) {
                return -1;
            }
            RoomDetails roomDetails = roomDetailsOption.unwrap();
            roomDetails.setCoverSrc(null);
            if (who == 0) {
                roomDetails.setVisitorList(null);
                roomDetails.setRoomInfo(null);
            } else if (who == -1) {
                roomDetails.setUserList(null);
                roomDetails.setRoomInfo(null);
            } else {
                throw new IllegalStateException("错误的who");
            }
            long oldId = roomDetails.deleteMember(id, who);
            //更新
            putRoomDetailsUnsafe(roomId, roomDetails, true);
            return oldId;
        } catch (NoneException e) {
            throw new RuntimeException("神奇！！！");
        } finally {
            writeLock.unlock();
        }
    }

}
