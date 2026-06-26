package com.ytyo.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.CommonConst.GiftConst;
import com.ytyo.Dao.AnchorMapper;
import com.ytyo.Dao.UserMapper;
import com.ytyo.Manager.UserManager;
import com.ytyo.Model.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
public class GiftService {
    final
    AnchorMapper anchorMapper;
    final
    UserMapper userMapper;
    final
    Redisson redisson;
    final
    ObjectMapper objectMapper;

    final UserManager userManager;

    final RLock userPropertyLock;
    final RLock anchorPropertyLock;


    public GiftService(AnchorMapper anchorMapper, UserMapper userMapper, Redisson redisson, ObjectMapper objectMapper, UserManager userManager) {
        this.anchorMapper = anchorMapper;
        this.userMapper = userMapper;
        this.redisson = redisson;
        userPropertyLock = redisson.getLock("user_property_lockkey");
        anchorPropertyLock = redisson.getLock("anchor_property_lockkey");
        this.objectMapper = objectMapper;
        this.userManager = userManager;
    }

    @Transactional
    public boolean sendGiftToAnchor(Long userId, Long anchorId, AnchorProperty anchorProperty, String cookieValue) throws RuntimeException {
        if (anchorId == null || userId == null || Objects.equals(userId, anchorId)) {
            return false;
        }
        if (anchorProperty == null || anchorProperty.getGifts() == null)
            return true;


        //暂无需要锁的读操作
        userPropertyLock.lock();
        anchorPropertyLock.lock();
        try {
            Anchor anchor = anchorMapper.selectAnchorById(anchorId);
            if (anchor == null) {
                return false;
            }
            User user = userMapper.getUserById(userId);
            if (user == null) {
                return false;
            }
            String propertyJson = user.getProperty();
            if (propertyJson == null) {
                return false;
            }
            Property oldProperty = objectMapper.readValue(propertyJson, Property.class);
            if (oldProperty == null)
                return false;
            for (AnchorProperty.Gift gift : anchorProperty.getGifts()) {
                if (gift.getGiftId() != null) {
                    Property property = GiftConst.GIFT_PRICES.get(gift.getGiftId());
                    if (property != null&& gift.getCount()!=null) {
                        for (int i = 0; i < gift.getCount(); i++) {
                            if (!oldProperty.subtract(property)) {
                                return false;
                            }
                        }
                    }
                }
            }
            User update = new User();
            update.setId(userId);
            update.setProperty(objectMapper.writeValueAsString(oldProperty));

            if (!userManager.updateUser(update, cookieValue))
                return false;

            String oldAnchorPropertyJson = anchor.getAnchorProperty();
            AnchorProperty oldAnchorProperty;
            if (oldAnchorPropertyJson == null) {
                oldAnchorProperty = new AnchorProperty();
            } else {
                oldAnchorProperty = objectMapper.readValue(oldAnchorPropertyJson, AnchorProperty.class);
            }
            oldAnchorProperty.add(anchorProperty);
            Anchor updateAnchor = new Anchor();
            updateAnchor.setId(anchorId);
            updateAnchor.setAnchorProperty(objectMapper.writeValueAsString(oldAnchorProperty));
            int rows = anchorMapper.updateAnchorById(updateAnchor);
            if (rows < 1) {
                //触发事务
                throw new RuntimeException("主播礼物更新失败");
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            userPropertyLock.unlock();
            anchorPropertyLock.unlock();
        }
    }

    @Transactional
    public boolean withdraw(Long anchorId, AnchorProperty anchorProperty) {
        if (anchorId == null || anchorProperty == null)
            return false;
        anchorPropertyLock.lock();
        //todo
        anchorPropertyLock.unlock();
        return true;
    }

}
