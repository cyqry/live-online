package com.ytyo.Model;


import com.ytyo.annotation.vaild.EnumShort;
import com.ytyo.annotation.vaild.EnumString;
import com.ytyo.annotation.vaild.Json;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Data
@ToString
public class User {
    Long id;
    /**
     * 0：普通用户 2：超级管理员 5：后台管理员
     */
    @EnumShort(value = {(short) 0, (short) 2, (short) 5}, message = "没有这个权限")
    Short role;
    /**
     * 个性id
     */
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "个性Id必须由数字组成")
    String personalityId;

    String nickname;
    @Range(min = 1, max = 150)
    Short age;

    @EnumString(value = {"男", "女"}, message = "没有这种性别")
    String gender;
    /**
     * 头像url
     */
    String avatar;

    String password;

    String realName;

    @Length(max = 18,message = "身份证号格式不正确")
    String idNumber;

    String region;

    @Length(max = 12,message = "个性id最长12位")
    String signature;

    @Length(max = 20, min = 9,message = "手机号格式不正确")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "手机号必须由数字组成")
    String phone;

    /**
     * 资产
     */
    @Json(classes = Property.class)
    String property;
    /**
     * 余额
     */
    Long balance;
    @Email(message = "邮箱格式不正确")
    String email;


    public User() {
    }

    public User(User user) {
        this.avatar = user.avatar;
        this.id = user.id;
        this.gender = user.gender;
        this.age = user.age;
        this.password = user.password;
        this.region = user.region;
        this.signature = user.signature;
        this.phone = user.phone;
        this.property = user.property;
        this.email = user.email;
        this.role = user.role;
        this.personalityId = user.personalityId;
        this.balance = user.balance;
        this.nickname = user.nickname;
        this.idNumber = user.idNumber;
        this.realName = user.realName;
    }
}
