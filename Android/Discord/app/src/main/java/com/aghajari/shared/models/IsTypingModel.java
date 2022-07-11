package com.aghajari.shared.models;

import java.io.Serializable;
import java.util.HashMap;

public class IsTypingModel implements Serializable {

    private static final long serialVersionUID = 8683371921141632191L;

    public boolean isTyping;
    public final String id;
    public final String serverId;
    public final String name;

    public IsTypingModel(boolean isTyping, String id, String serverId, String name) {
        this.isTyping = isTyping;
        this.id = id;
        this.serverId = serverId;
        this.name = name;
    }

    public static class IsTypingResponse implements Serializable {
        public final HashMap<String, String> typings;
        public final String id;

        public IsTypingResponse(HashMap<String, String> typings, String id) {
            this.typings = typings;
            this.id = id;
        }
    }
}
