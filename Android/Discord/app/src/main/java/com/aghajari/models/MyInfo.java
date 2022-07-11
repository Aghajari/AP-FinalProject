package com.aghajari.models;

import com.aghajari.shared.models.UserModel;

public class MyInfo {

    static UserModel instance;

    public static UserModel getInstance() {
        if (instance == null)
            instance = new UserModel();

        return instance;
    }

    public static String getUsername(String def){
        return getUsername(getInstance(), def);
    }

    public static String getUsername(UserModel user, String def){
        if (user.username == null || user.username.isEmpty())
            return def;
        return user.username;
    }

}
