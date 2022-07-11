package com.aghajari.shared.models;

import com.aghajari.shared.IDFinder;

import java.io.Serializable;

public class FriendshipModel implements Serializable, IDFinder {

    @java.io.Serial
    private static final long serialVersionUID = 8683452581122892195L;

    public int index;
    public String fromId;
    public String toId;
    public boolean accepted;
    public UserModel user;

    public FriendshipModel(int index, String fromId, String toId, boolean accepted) {
        this.index = index;
        this.fromId = fromId;
        this.toId = toId;
        this.accepted = accepted;
    }

    public boolean exists() {
        return index != -1;
    }

    @Override
    public UserModel getUser() {
        return user;
    }

    @Override
    public void setUser(UserModel model) {
        user = model;
    }

    @Override
    public String getUserId(String clientId) {
        return fromId.equals(clientId) ? toId : fromId;
    }
}