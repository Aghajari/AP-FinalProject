package com.aghajari.models;

import com.aghajari.shared.models.UserModel;
import com.google.gson.Gson;

import java.util.List;

public class SearchUsers {

    public List<UserModel> users;

    public static SearchUsers parse(String json) {
        return new Gson().fromJson(json, SearchUsers.class);
    }
}
