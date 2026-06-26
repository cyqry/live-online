package com.ytyo.Manager;

import com.ytyo.Option.Option;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisManager {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Option<Object> get(String key)  {
        return Option.from(redisTemplate.opsForValue().get(key));
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
}
