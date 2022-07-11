package com.aghajari.shared.models;

import com.aghajari.shared.IDFinder;

import java.io.Serializable;

public class IDModel implements Serializable, IDFinder {

    private static final long serialVersionUID = 8683452581122892285L;

    public final String id;
    private UserModel user;

    public IDModel(String id) {
        this.id = id;
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
        return id;
    }
}