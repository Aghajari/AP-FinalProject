package com.aghajari.types;

import com.aghajari.UserThread;
import com.aghajari.api.ApiService;
import com.aghajari.shared.*;
import com.aghajari.shared.models.DeviceInfo;
import com.google.gson.Gson;

public class Auth {

    private Auth() {
    }

    public static void run(UserThread thread, SocketModel model) {
        DeviceInfo info = model.get();
        ApiService.auth(info.token, new ApiService.Callback() {
            @Override
            public void onResponse(String body) {
                try {
                    Model model = new Gson().fromJson(body, Model.class);
                    if (model.isSuccess()) {
                        System.out.println(info.name + " (" + info.os + ")" + ": " + model.userinfo._id);
                        thread.setDeviceInfo(info);
                        thread.setToken(info.token);
                        thread.setClientId(model.userinfo._id);
                        thread.write(new SocketModel(SocketEvents.AUTH, true));
                        return;
                    }
                } catch (Exception ignore) {
                }
                onError(false, 0);
            }

            @Override
            public void onError(boolean network, int code) {
                thread.write(new SocketModel(SocketEvents.AUTH, false));
                thread.close();
            }
        });
    }

    static class Model {
        public boolean success;
        public Info userinfo;

        public boolean isSuccess() {
            return success && userinfo != null && userinfo._id != null && userinfo._id.length() > 0;
        }
    }

    static class Info {

        public String _id;
    }
}
