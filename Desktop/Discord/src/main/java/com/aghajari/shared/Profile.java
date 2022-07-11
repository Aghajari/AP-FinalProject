package com.aghajari.shared;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public interface Profile {

    String getAvatar();

    String getAvatarName();

    String getName();

    Color getColor();

    Image getImage();

    void setColor(Color color);

    void setImage(Image img);
}
