package com.aghajari.bot;

import com.aghajari.shared.models.ServerModel;

import java.util.List;

class BotData {

    public UserData from;
    public UserData to;
    public String chatId;
    public boolean isPrivateMessage;

    public MessageData message;
    public Server server;
    public ServerModel.ServerChannel channel;

    static class MessageData {
        public long time;
        public String text;
        public String fromId;
        public String toId;
    }

    static class UserData {
        public String avatar;
        public String nickname;
        public String username;
        public String email;
        public boolean isOnline;
    }

    static class Server {
        public String id;
        public String name;
        public String avatar;
        public List<ServerModel.ServerChannel> channels;
    }
}
