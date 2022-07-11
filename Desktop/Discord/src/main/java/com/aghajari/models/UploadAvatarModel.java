package com.aghajari.models;

import com.aghajari.api.ApiService;
import com.aghajari.shared.models.UserModel;
import com.google.gson.Gson;

public class UploadAvatarModel extends BaseApiModel {

    public String url;

    public static UploadAvatarModel parse(String json) {
        UploadAvatarModel model = new Gson().fromJson(json, UploadAvatarModel.class);
        if (model.success) {
            MyInfo.getInstance().avatar = model.url;
            MyInfo.getInstance().setImage(null);
            MyInfo.getInstance().setColor(null);
            ApiService.saveUser(MyInfo.getInstance());
        }
        return model;
    }

    public static UploadAvatarModel parseOnlyUrl(String json) {
        try {
            UploadAvatarModel model = new Gson().fromJson(json, UploadAvatarModel.class);
            if (model.success) {
                return model;
            }
        }catch (Exception ignore){}

        return null;
    }
}
