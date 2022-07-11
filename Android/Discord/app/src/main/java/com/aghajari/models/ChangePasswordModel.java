package com.aghajari.models;

import com.aghajari.api.ApiService;
import com.aghajari.shared.models.UserModel;
import com.google.gson.Gson;

public class ChangePasswordModel extends BaseApiModel {

    public String token;

    public static ChangePasswordModel parse(String json) {
        try {
            ChangePasswordModel model = new Gson().fromJson(json, ChangePasswordModel.class);
            if (model.success) {
                ApiService.saveToken(model.token);
            }
            return model;
        }catch (Exception ignore){}

        return null;
    }
}
