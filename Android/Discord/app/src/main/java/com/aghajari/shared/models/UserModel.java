package com.aghajari.shared.models;

import com.aghajari.shared.Profile;

import java.io.Serializable;

public class UserModel implements Serializable, Profile {

    private static final long serialVersionUID = 8683452581322892192L;

    public String email = "", username = "", nickname = "",
            id = "", _id = "", avatar = "";

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public String getAvatarName() {
        return nickname;
    }

    public String getName() {
        if (username == null || username.isEmpty())
            return nickname;
        return username;
    }

    public String getId() {
        return id == null || id.isEmpty() ? _id : id;
    }

    private transient Object image = null;
    private transient Integer color = null;

    public Object getImage() {
        return image;
    }

    public void setImage(Object image) {
        this.image = image;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

}
