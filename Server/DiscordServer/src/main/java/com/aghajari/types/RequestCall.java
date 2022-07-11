package com.aghajari.types;

import com.aghajari.OnlineUsers;
import com.aghajari.UserThread;
import com.aghajari.api.ApiService;
import com.aghajari.shared.IDFinder;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;
import com.aghajari.shared.models.UserModel;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RequestCall {

    private RequestCall() {
    }

    public static void run(UserThread thread, SocketModel model) {
        if (model.eventType == SocketEvents.REQUEST_CALL) {
            List<IDFinder> list = Collections.singletonList(new IDFinder() {
                UserModel model;

                @Override
                public UserModel getUser() {
                    return model;
                }

                @Override
                public void setUser(UserModel model) {
                    this.model = model;
                }

                @Override
                public String getUserId(String clientId) {
                    return thread.getClientId();
                }
            });
            try {
                ApiService.fixUsers(thread.getToken(), thread.getClientId(), list);
                OnlineUsers.send(model.get(), new SocketModel(SocketEvents.REQUEST_CALL, list.get(0).getUser()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            OnlineUsers.send(model.get(), new SocketModel(model.eventType, thread.getClientId()));
        }
    }

}
