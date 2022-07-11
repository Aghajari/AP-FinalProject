package com.aghajari.shared.models;

import com.aghajari.shared.IDFinder;

import java.io.Serializable;

public class OpenedChatModel implements Serializable, IDFinder {

    @java.io.Serial
    private static final long serialVersionUID = 8683452581122892191L;

    public final int index;
    public final String userId;
    private UserModel model;

    public OpenedChatModel(int index, String userId) {
        this.index = index;
        this.userId = userId;
    }

    @Override
    public UserModel getUser() {
        if (model != null)
            return model;
        model = new UserModel();
        model.id = userId;
        model.nickname = "?";
        return model;
    }

    @Override
    public void setUser(UserModel model) {
        this.model = model;
    }

    @Override
    public String getUserId(String clientId) {
        return userId;
    }

}
