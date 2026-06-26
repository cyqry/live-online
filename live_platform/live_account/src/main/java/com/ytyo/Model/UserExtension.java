package com.ytyo.Model;

public class UserExtension extends User {
    private String avatarBase;

    private String oldPassword;
    public String getAvatarBase() {
        return avatarBase;
    }

    public void setAvatarBase(String avatarBase) {
        this.avatarBase = avatarBase;
    }
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
