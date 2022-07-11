package com.aghajari.models;

import com.aghajari.api.ApiService;
import com.aghajari.shared.models.UserModel;
import com.google.gson.Gson;

public class LoginModel extends BaseApiModel {

    public UserModel user;
    public String token;

    public static LoginModel parse(String json) {
        LoginModel model = new Gson().fromJson(json, LoginModel.class);
        if (model.success) {
            MyInfo.instance = model.user;
            ApiService.saveUser(model.user);
            ApiService.saveToken(model.token);
        }
        return model;
    }
}
