package com.ytyo.Model;

import lombok.Data;

@Data
public class UserPublic {
    String nickname;
    String personalityId;
    Short age;
    String gender;
    String avatar;
    String region;
    String signature;

    public UserPublic(){}
    public UserPublic(User user) {
        this.age = user.getAge();
        this.gender = user.getGender();
        this.avatar = user.getAvatar();
        this.region = user.getRegion();
        this.nickname = user.getNickname();
        this.personalityId = user.getPersonalityId();
        this.signature = user.getSignature();
    }
}
