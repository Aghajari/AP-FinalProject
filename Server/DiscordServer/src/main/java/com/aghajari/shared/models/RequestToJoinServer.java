package com.aghajari.shared.models;

import java.io.Serializable;

public class RequestToJoinServer implements Serializable {
    @java.io.Serial
    private static final long serialVersionUID = 8683452581141632191L;

    public final int resultCode;
    public final ServerModel serverModel;

    public RequestToJoinServer(int resultCode, ServerModel serverModel) {
        this.resultCode = resultCode;
        this.serverModel = serverModel;
    }
}
