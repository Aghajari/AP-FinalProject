package com.aghajari.shared.models;

import com.aghajari.shared.Profile;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.Serializable;

public class UserModel implements Serializable, Profile {
    @java.io.Serial
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

    private transient Image image = null;
    private transient Color color = null;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
