package com.ytyo.Model;

import com.ytyo.annotation.vaild.Property;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

//包含主播部分的信息和User部分的信息;只有主播才能getWholeUser
@Data
public class WholeUser {
    Long id;
    Short role;
    String personalityId;
    String nickname;

    Short age;
    String gender;
    /**
     * 头像url
     */
    String avatar;

    String region;

    @Length(max = 12)
    String signature;

    @Length(max = 20, min = 9)
    String phone;

    /**
     * 资产
     */
    @Property
    String property;
    /**
     * 余额
     */
    Long balance;
    @Email
    String email;

    String description;

    /**
     * 主播收到的礼物
     */
    String anchorProperty;

    public WholeUser(User user, Anchor anchor) {
        if (user == null || anchor == null)
            throw new IllegalArgumentException("错误的参数");
        if (!Objects.equals(user.getId(), anchor.getId())) {
            throw new IllegalArgumentException("错误的融合");
        }
        this.age = user.getAge();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.id = user.getId();
        this.phone = user.getPhone();
        this.anchorProperty = anchor.getAnchorProperty();
        this.gender = user.getGender();
        this.avatar = user.getAvatar();
        this.balance = user.getBalance();
        this.description = anchor.getDescription();
        this.property = user.getProperty();
        this.region = user.getRegion();
        this.signature = user.getSignature();
        this.personalityId = user.getPersonalityId();
        this.role = user.getRole();

    }
}
