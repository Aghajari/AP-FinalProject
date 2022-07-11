package com.aghajari.shared;

import java.io.Serializable;

public class SocketModel implements Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 8683452581122892189L;

    public int eventType;
    public String name;
    public Object data;

    public SocketModel(String methodName, Object... args) {
        this(SocketEvents.API, args);
        this.name = methodName;
    }

    public SocketModel(int type, String methodName, Object data) {
        this(type, data);
        this.name = methodName;
    }

    public SocketModel(int eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(){
        return (T) data;
    }
}
