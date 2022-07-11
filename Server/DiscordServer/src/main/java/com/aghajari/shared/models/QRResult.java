package com.aghajari.shared.models;

import java.io.Serializable;

public class QRResult implements Serializable {

    public final String token;
    public final UserModel userModel;

    public QRResult(String token, UserModel userModel) {
        this.token = token;
        this.userModel = userModel;
    }
}
