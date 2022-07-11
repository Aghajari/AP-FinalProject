package com.aghajari.shared;

public interface Profile {

    String getAvatar();

    String getAvatarName();

    String getName();

    Object getColor();

    Object getImage();

    void setColor(Object color);

    void setImage(Object img);
}
