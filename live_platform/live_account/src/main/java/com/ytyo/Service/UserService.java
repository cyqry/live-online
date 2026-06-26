package com.ytyo.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.Dao.UserMapper;
import com.ytyo.Manager.UserManager;
import com.ytyo.Model.Property;
import com.ytyo.Model.User;
import com.ytyo.Option.Option;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserManager userManager;

    final
    Redisson redisson;
    RLock userPropertyLock;

    public UserService(Redisson redisson) {
        this.redisson = redisson;
        userPropertyLock = redisson.getLock("user_property_lockkey");
    }

    public boolean updateUserByIdCookie(User user, String cookieValue) {
        return userManager.updateUser(user, cookieValue);
    }


    public Option<User> getUserById(Long id) {
        if (id == null) {
            return Option.None();
        }
        return Option.from(userMapper.getUserById(id));
    }

    public Option<List<User>> getUserByIds(List<Long> userIds) {
        if (userIds == null) {
            return Option.None();
        }
        if (userIds.isEmpty()) {
            return Option.Some(new ArrayList<>());
        }
        return Option.from(userMapper.getUsersByIds(userIds));
    }

    public boolean recharge(Long userId, Property property, String cookieValue) {
        if (userId == null || property == null || property.getCurrencies() == null || property.getCurrencies().isEmpty()) {
            return false;
        }
        userPropertyLock.lock();
        try {
            String oldPropertyString = userMapper.getUserPropertyById(userId);
            Property oldProperty = null;
            if (oldPropertyString != null) {
                oldProperty = objectMapper.readValue(oldPropertyString, Property.class);
            }
            if (oldProperty == null)
                oldProperty = new Property(new ArrayList<>());

            oldProperty.add(property);

            User user = new User();
            user.setId(userId);
            user.setProperty(objectMapper.writeValueAsString(oldProperty));
            return userManager.updateUser(user, cookieValue);
        } catch (JsonProcessingException e) {
            log.error("反序列化异常");
            return false;
        } finally {
            userPropertyLock.unlock();
        }
    }

    public boolean existPersonalityId(String personalityId) {
        if (personalityId == null)
            return false;
        return userMapper.countByMap(Map.of("personality_id", personalityId)) > 0;
    }

    public boolean existEmail(String email) {
        if (email == null)
            return false;
        return userMapper.countByMap(Map.of("email", email)) > 0;
    }
    public boolean existPhone(String phone){
        if (phone == null)
            return false;
        return userMapper.countByMap(Map.of("phone", phone)) > 0;
    }

    public Option<User> getUserByPersonalityId(String personalityId) {
        if (personalityId == null)
            return Option.None();
        return Option.from(userMapper.getUserByPersonalityId(personalityId));
    }
}
