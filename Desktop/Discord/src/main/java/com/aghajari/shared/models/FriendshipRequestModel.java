package com.aghajari.shared.models;

import java.io.Serializable;

public class FriendshipRequestModel implements Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 8683452581122892194L;

    public final static int SEND_REQUEST = 0;
    public final static int ACCEPT_REQUEST = 1;
    public final static int CANCEL_FRIENDSHIP = 2;
    public final static int NOTIFY_FRIENDSHIP = 3;

    public final int type;
    public final String id;
    public final int keyIndex;

    public FriendshipRequestModel(int type, String id, int keyIndex) {
        this.type = type;
        this.id = id;
        this.keyIndex = keyIndex;
    }
}