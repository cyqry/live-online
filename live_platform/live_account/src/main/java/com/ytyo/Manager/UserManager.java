package com.ytyo.Manager;

import com.ytyo.Dao.UserMapper;
import com.ytyo.Model.User;
import com.ytyo.Utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import static com.ytyo.Const.RedisConst.COOKIE_HASH_KEY;

@Component
@Slf4j
public class UserManager {

    @Autowired
    UserMapper userMapper;

    public boolean updateUser(User user, String cookieValue) {
        if (user == null || cookieValue == null)
            return false;
        try {
            int i = userMapper.updateUserById(user);
            if (i > 0) {
                boolean redisUpdated = updateRedisUserByCookie(cookieValue, user);
                if (!redisUpdated) {
                    log.error("缓存更新错误");
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("user更新错误,user:{},error:{}", user, e);
            return false;
        }
    }


    @Autowired
    HashOperations<String, Object, Object> opHash;

    public boolean saveCookieUser(String cookie, User user, int days) {
        //TODO 设置过期时间
        opHash.put(COOKIE_HASH_KEY, cookie, user);
        return true;
    }

    public boolean deleteUser(String cookie) {
        if (cookie == null) {
            return false;
        }
        Long aLong = opHash.delete(COOKIE_HASH_KEY, cookie);
        return aLong > 0;
    }

    private boolean updateRedisUserByCookie(String cookie, User user) {
        Object o = opHash.get(COOKIE_HASH_KEY, cookie);
        if (o instanceof User redisUser) {
            try {
                BeanUtil.updateNonNullFields(user, redisUser);
                opHash.put(COOKIE_HASH_KEY, cookie, redisUser); // 更新缓存中的用户信息
            } catch (IllegalAccessException e) {
                log.error("参数错误", e);
                return false;
            }
        }
        return true;
    }
}
