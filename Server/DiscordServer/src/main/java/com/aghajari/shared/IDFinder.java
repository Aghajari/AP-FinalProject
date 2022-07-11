package com.aghajari.shared;

import com.aghajari.shared.models.UserModel;

public interface IDFinder {

    UserModel getUser();
    void setUser(UserModel model);

    String getUserId(String clientId);
}
