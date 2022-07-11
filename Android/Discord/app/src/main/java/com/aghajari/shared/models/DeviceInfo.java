package com.aghajari.shared.models;

import java.io.Serializable;

public class DeviceInfo implements Serializable {

    public static final int WINDOWS = 0;
    public static final int MAC = 1;
    public static final int ANDROID = 2;

    public final String token;
    public final String name;
    public final String os;
    public final int type;
    public final String key;

    public DeviceInfo(String token, String name, String os, int type, String key) {
        this.token = token;
        this.name = name;
        this.type = type;
        this.os = os;
        this.key = key;
    }
}
