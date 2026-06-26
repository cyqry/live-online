package com.ytyo.Manager;

import com.ytyo.Model.User;
import com.ytyo.Option.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import static com.ytyo.Const.RedisConst.COOKIE_HASH_KEY;

@Component
public class RedisManager {
    @Autowired
    HashOperations<String, Object, Object> opHash;

    public Option<User> getUserByCookie(String cookie) {
        Object o = opHash.get(COOKIE_HASH_KEY, cookie);
        if (o == null) {
            return Option.None();
        }

        if (o instanceof User user) {
            return Option.from(user);
        } else {
            throw new IllegalArgumentException("Cookie Hash 存值错误！");
        }
    }

}
