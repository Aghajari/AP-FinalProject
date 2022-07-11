package com.aghajari.bot;

import com.aghajari.OnlineUsers;
import com.aghajari.UserThread;
import com.aghajari.api.ApiService;
import com.aghajari.api.PermissionUtils;
import com.aghajari.database.Connection;
import com.aghajari.shared.models.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotHandler {

    public static void sendMessage(UserThread thread, MessageModel model) {
        boolean dm = !model.toId.contains("#") && !model.fromId.contains("#");

        String link_from = Connection.getBot().select(model.fromId);
        String link_to = dm ? Connection.getBot().select(model.toId) : null;

        HashMap<String, String> serverIds = new HashMap<>();

        String serverId = null, channelId = null;
        if (!dm) {
            serverId = model.toId.substring(0, model.toId.indexOf('#'));
            channelId = model.toId.substring(model.toId.indexOf('#') + 1);
            try {
                List<IDModel> list = Connection.getUserServers().select(serverId);
                for (IDModel id : list) {
                    String link = Connection.getBot().select(id.id);
                    if (link != null)
                        serverIds.put(id.id, link);
                }
            } catch (Exception ignore) {
            }
        }

        if (link_from != null || link_to != null || !serverIds.isEmpty()) {
            BotData data = new BotData();
            data.isPrivateMessage = dm;
            data.message = new BotData.MessageData();
            data.message.fromId = model.fromId;
            data.message.toId = model.toId;
            data.message.text = model.text;
            data.message.time = model.time;

            if (!dm) {
                try {
                    ServerModel serverModel = Connection.getServers().get(serverId);
                    BotData.Server server = new BotData.Server();
                    server.channels = serverModel.channels;
                    server.name = serverModel.name;
                    server.id = serverModel.serverId;
                    server.avatar = serverModel.avatar;
                    data.server = server;

                    for (ServerModel.ServerChannel c : serverModel.channels) {
                        if (c.id.equals(channelId)) {
                            data.channel = c;
                            break;
                        }
                    }
                } catch (Exception ignore) {
                }
            }

            ApiService.findFromAndTo(thread.getToken(), model.fromId,
                    dm ? model.toId : null,
                    map -> {
                        UserModel m = map.get(model.fromId);
                        if (m != null) {
                            BotData.UserData user = new BotData.UserData();
                            user.avatar = m.avatar;
                            user.email = m.email;
                            user.username = m.username;
                            user.nickname = m.nickname;
                            user.isOnline = OnlineUsers.isOnline(model.fromId);
                            data.from = user;
                        }
                        m = map.get(model.toId);

                        if (m != null) {
                            BotData.UserData user = new BotData.UserData();
                            user.avatar = m.avatar;
                            user.email = m.email;
                            user.username = m.username;
                            user.nickname = m.nickname;
                            user.isOnline = OnlineUsers.isOnline(model.toId);
                            data.to = user;
                        }

                        Gson gson = new Gson();
                        if (link_from != null) {
                            data.chatId = model.toId;
                            ApiService.bot(link_from, gson.toJson(data), new ApiService.Callback() {
                                @Override
                                public void onResponse(String body) {
                                    parseResponse(thread, body, model.fromId);
                                }

                                @Override
                                public void onError(boolean network, int code) {
                                }
                            });
                        }

                        if (link_to != null) {
                            data.chatId = model.fromId;
                            ApiService.bot(link_to, gson.toJson(data), new ApiService.Callback() {
                                @Override
                                public void onResponse(String body) {
                                    parseResponse(thread, body, model.toId);
                                }

                                @Override
                                public void onError(boolean network, int code) {
                                }
                            });
                        }

                        if (!dm && data.server != null && !serverIds.isEmpty()) {
                            data.chatId = model.toId;
                            String json = gson.toJson(data);
                            try {
                                for (Map.Entry<String, String> entry : serverIds.entrySet()) {
                                    ApiService.bot(entry.getValue(), json, new ApiService.Callback() {
                                        @Override
                                        public void onResponse(String body) {
                                            parseResponse(thread, body, entry.getKey());
                                        }

                                        @Override
                                        public void onError(boolean network, int code) {
                                        }
                                    });
                                }
                            } catch (Exception ignore) {
                            }
                        }
                    }
            );
        }
    }

    private static void parseResponse(UserThread thread, String body, String from) {
        if (body == null || body.isEmpty())
            return;

        List<BotResponse> botResponse = BotResponse.parse(body);
        for (BotResponse res : botResponse) {
            MessageModel model = new MessageModel();
            model.toId = res.to;
            model.fromId = from;
            model.text = res.text;
            if (model.text == null || model.toId == null
                    || model.text.isEmpty() || model.toId.isEmpty())
                return;

            boolean dm = !model.toId.contains("#") && !model.fromId.contains("#");
            if (!dm) {
                try {
                    String serverId = model.toId.substring(0, model.toId.indexOf('#'));
                    String channelId = model.toId.substring(model.toId.indexOf('#') + 1);
                    ServerModel serverModel = Connection.getServers().get(serverId);
                    ServerModel.ServerChannel channel = null;
                    for (ServerModel.ServerChannel c : serverModel.channels) {
                        if (c.id.equals(channelId)) {
                            channel = c;
                            break;
                        }
                    }
                    if (channel == null || !PermissionUtils.canSendMessage(from,
                            serverModel, channel))
                        continue;
                }catch (Exception ignore){
                    continue;
                }
            }
            sendMessageFromBot(thread, model);
        }
    }

    private static void sendMessageFromBot(UserThread thread, MessageModel model) {
        boolean dm = !model.toId.contains("#") && !model.fromId.contains("#");
        if (dm) {
            try {
                if (ApiService.getData(thread.getToken(), model.toId) == null)
                    return;
            } catch (IOException e) {
                return;
            }
        }

        int index = Connection.getMessage().insert(model);
        try {
            model.setUser(ApiService.getData(thread.getToken(), model.fromId));
        } catch (IOException ignore) {
        }

        if (index != -1) {
            if (dm && !model.fromId.equals(model.toId)) {
                if (Connection.getOpenedChats().select(model.toId, model.fromId) == null) {
                    Connection.getOpenedChats().insert(model.toId, model.fromId);
                    OpenedChatModel openedChatModel = Connection.getOpenedChats().select(model.toId, model.fromId);
                    try {
                        openedChatModel.setUser(ApiService.getData(thread.getToken(), openedChatModel.userId));
                    } catch (IOException ignore) {
                    }
                    OnlineUsers.openChat(model.toId, openedChatModel);
                }
            }
            OnlineUsers.sendMessage(null, model, true);
        }
    }

}
