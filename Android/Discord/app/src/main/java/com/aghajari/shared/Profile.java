package com.aghajari.shared;


public interface Profile {

    String getAvatar();

    String getAvatarName();

    String getName();

    Integer getColor();

    Object getImage();

    void setColor(Integer color);

    void setImage(Object img);
}
