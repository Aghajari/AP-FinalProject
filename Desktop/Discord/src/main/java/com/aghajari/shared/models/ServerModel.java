package com.aghajari.shared.models;

import com.aghajari.shared.Profile;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;

public class ServerModel implements Serializable, Profile {

    @java.io.Serial
    private static final long serialVersionUID = 8672152581122892191L;

    public String name;
    public String avatar;
    public String owner;
    public String inviteCode;
    public final String serverId;
    public int permissions;
    public int permissions2;
    public ArrayList<ServerChannel> channels;

    public ServerModel(String serverId) {
        this.serverId = serverId;
    }

    public ServerModel(String name, String image, String serverId, String owner, int permissions, String inviteCode) {
        this.name = name;
        this.avatar = image;
        this.owner = owner;
        this.inviteCode = inviteCode;
        this.serverId = serverId;
        this.permissions = this.permissions2 = permissions;
        this.channels = new ArrayList<>();
    }

    private transient Image image = null;
    private transient Color color = null;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public String getAvatarName() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public static class ServerChannel implements Serializable {
        public String name;
        public String id;
        public int type;

        public ServerChannel(String name, String id, int type) {
            this.name = name;
            this.id = id;
            this.type = type;
        }
    }
}
