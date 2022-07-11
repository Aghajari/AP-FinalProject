package com.aghajari.shared.models;

import com.aghajari.shared.IDFinder;

import java.io.Serializable;
import java.util.HashMap;

public class MessageModel implements Serializable, IDFinder {

    private static final long serialVersionUID = 8683432581122892194L;

    public int index;
    public String text;
    public long time;
    public String fromId;
    public String toId;

    public HashMap<String, Integer> reactions;
    public boolean seen;
    public boolean edited;

    public MessageModel() {
    }

    public MessageModel(String text, long time, String fromId, String toId) {
        this.text = text;
        this.time = time;
        this.fromId = fromId;
        this.toId = toId;
    }

    private UserModel user;

    @Override
    public UserModel getUser() {
        return user;
    }

    @Override
    public void setUser(UserModel model) {
        this.user = model;
    }

    @Override
    public String getUserId(String clientId) {
        return fromId;
    }
}