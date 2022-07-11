package com.aghajari.shared.models;

import java.io.Serializable;

public class SoundModel implements Serializable {

    private static final long serialVersionUID = 8683452581122892192L;

    public int bytesRead;
    public final byte[] bytes = new byte[100];
    public String userId;

    public SoundModel(String userId) {
        this.userId = userId;
    }
}
