package com.aghajari.api;

import com.aghajari.OnlineUsers;
import com.aghajari.UserThread;
import com.aghajari.bot.BotHandler;
import com.aghajari.database.Connection;
import com.aghajari.shared.ChatsList;
import com.aghajari.shared.models.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SocketApi {

    private SocketApi() {
    }

    public static ChatsList getChats(UserThread thread) {
        ArrayList<OpenedChatModel> list = Connection.getOpenedChats().get(thread.getClientId());
        try {
            ApiService.fixUsers(thread.getToken(), thread.getClientId(), list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<ServerModel> servers = Connection.getUserServers().get(thread.getClientId());
        ChatsList c = new ChatsList();
        c.openedChats = list;
        c.servers = servers;
        return c;
    }

    public static UserModel getUserInfo(UserThread thread, String id) {
        try {
            if (Connection.getBlocked().hasBlocked(id, thread.getClientId())) {
                return ApiService.copy(ApiService.getData(thread.getToken(), id));
            } else {
                return ApiService.getData(thread.getToken(), id);
            }
        } catch (IOException e) {
            return null;
        }
    }

    public static OpenedChatModel openChat(UserThread thread, String userId) {
        Connection.getOpenedChats().insert(thread.getClientId(), userId);
        OpenedChatModel model = Connection.getOpenedChats().select(thread.getClientId(), userId);
        OnlineUsers.openChat(thread, model, false);
        return model;
    }

    public static void closeChat(UserThread thread, String userId) {
        Connection.getOpenedChats().delete(thread.getClientId(), userId);
    }

    public static boolean isChatOpen(UserThread thread, String userId) {
        return Connection.getOpenedChats().select(thread.getClientId(), userId) != null;
    }

    public static boolean isOnline(UserThread thread, String id) {
        return OnlineUsers.isOnline(id);
    }

    public static ServerModel createServer(UserThread thread, String name, String image) {
        ServerModel model = Connection.getServers().insert(thread.getClientId(), name, image);
        if (model != null)
            Connection.getUserServers().insert(thread.getClientId(), model.serverId);
        OnlineUsers.openChat(thread, model, false);
        return model;
    }

    public static boolean deleteServer(UserThread thread, String id) {
        if (Connection.getServers().delete(id)) {
            OnlineUsers.requestDeleteServer(thread, id);
            return true;
        }
        return false;
    }

    public static String revokeServerInviteCode(UserThread thread, String id) {
        String code = Connection.getServers().revoke(id);
        if (code != null)
            OnlineUsers.requestUpdateServer(thread, id);
        return code;
    }

    public static boolean updateServerPermissions(UserThread thread, String id, Integer permissions) {
        if (Connection.getServers().updatePermissions(id, permissions)) {
            OnlineUsers.requestUpdateServer(thread, id);
            return true;
        }
        return false;
    }

    public static boolean updateServerAvatar(UserThread thread, String id, String image) {
        if (Connection.getServers().updateAvatar(id, image)) {
            OnlineUsers.requestUpdateServer(thread, id);
            return true;
        }
        return false;
    }

    public static boolean updateServerName(UserThread thread, String id, String name) {
        if (Connection.getServers().updateName(id, name)) {
            OnlineUsers.requestUpdateServer(thread, id);
            return true;
        }
        return false;
    }

    public static boolean updateServerChannels(UserThread thread, String id, String channels) {
        if (Connection.getServers().updateChannels(id, channels)) {
            OnlineUsers.requestUpdateServer(thread, id);
            return true;
        }
        return false;
    }

    public static boolean leaveServer(UserThread thread, String id) {
        boolean ok = Connection.getUserServers().delete(id, thread.getClientId());
        if (ok) {
            OnlineUsers.requestLeaveServerToMe(thread, id);
        }
        return ok;
    }

    public static boolean removeFromServer(UserThread thread, String serverId, String userId) {
        if (Connection.getUserServers().delete(serverId, userId)) {
            OnlineUsers.requestDeleteUserFromServer(userId, serverId);
            return true;
        }
        return false;
    }

    public static int getMemberPermission(UserThread thread, String serverId, String userId) {
        return Connection.getUserServers().getPermission(serverId, userId);
    }

    public static void updateMemberPermission(UserThread thread, String serverId, String userId, Integer permission) {
        Connection.getUserServers().updateMemberPermission(serverId, userId, permission);
        OnlineUsers.requestUpdatePermission(userId, serverId, permission);
    }

    public static RequestToJoinServer joinServer(UserThread thread, String inviteCode) {
        ServerModel serverModel = Connection.getServers().getByInviteCode(inviteCode);
        if (serverModel == null)
            return new RequestToJoinServer(404, null);

        if (Connection.getUserServers().exists(thread.getClientId(), serverModel.serverId))
            return new RequestToJoinServer(300, serverModel);

        Connection.getUserServers().insert(thread.getClientId(), serverModel.serverId);
        OnlineUsers.openChat(thread, serverModel, false);
        return new RequestToJoinServer(200, serverModel);
    }

    public static List<FriendshipModel> getFriendshipList(UserThread thread, Boolean accepted) {
        List<FriendshipModel> list = Connection.getFriendship().get(thread.getClientId(), accepted);
        try {
            ApiService.fixUsers(thread.getToken(), thread.getClientId(), list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static FriendshipModel getFriendshipStatus(UserThread thread, String id) {
        return Connection.getFriendship().select(thread.getClientId(), id);
    }

    public static List<FriendshipModel> getMyFriends(UserThread thread) {
        return getFriendshipList(thread, true);
    }

    public static List<FriendshipModel> getMyPendingFriends(UserThread thread) {
        return getFriendshipList(thread, false);
    }

    public static List<IDModel> getMutualFriends(UserThread thread, String id) {
        List<IDModel> list = Connection.getFriendship().mutual(thread.getClientId(), id);
        try {
            ApiService.fixUsers(thread.getToken(), thread.getClientId(), list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<IDModel> getOnlineFriends(UserThread thread) {
        List<FriendshipModel> list = Connection.getFriendship().get(thread.getClientId());
        List<IDModel> models = new ArrayList<>();
        for (FriendshipModel m : list) {
            String id = m.fromId.equals(thread.getClientId()) ? m.toId : m.fromId;
            if (OnlineUsers.isOnline(id))
                models.add(new IDModel(id));
        }
        try {
            ApiService.fixUsers(thread.getToken(), thread.getClientId(), models);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return models;
    }

    public static List<IDModel> getMembersOfServer(UserThread thread, String serverId) {
        List<IDModel> models = Connection.getUserServers().select(serverId);
        try {
            ApiService.fixUsers(thread.getToken(), thread.getClientId(), models);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return models;
    }

    public static List<MessageModel> getMessages(UserThread thread) {
        return Connection.getMessage().getSavedMessages(thread.getClientId());
    }

    public static List<MessageModel> getMessages(UserThread thread, String id, Boolean server) {
        List<MessageModel> list;
        if (server && id.contains("#"))
            list = Connection.getMessage().get(id);
        else
            list = Connection.getMessage().get(thread.getClientId(), id);

        try {
            ApiService.fixAllUsers(thread.getToken(), thread.getClientId(), list);
        } catch (IOException ignore) {
        }
        OnlineUsers.requestIsTyping(thread, id, server);
        return list;
    }

    public static MessageModel sendMessage(UserThread thread, MessageModel model) {
        if (model.fromId.equals(thread.getClientId())) {
            int index = Connection.getMessage().insert(model);
            try {
                model.setUser(ApiService.getData(thread.getToken(), thread.getClientId()));
            } catch (IOException ignore) {
            }
            if (model.toId.equals(model.fromId)) {
                BotHandler.sendMessage(thread, model);
                return model;
            }

            if (index != -1) {
                if (!model.toId.contains("#") && !model.fromId.contains("#")) {
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
                OnlineUsers.sendMessage(thread, model);
                BotHandler.sendMessage(thread, model);
                return model;
            }
        }
        return null;
    }

    public static void reaction(UserThread thread, Integer index, Integer reaction) {
        MessageModel model = Connection.getMessage().reaction(index, thread.getClientId(), reaction);
        if (model != null)
            OnlineUsers.sendMessage(thread, model);
    }

    public static void blockUser(UserThread thread, String id) {
        Connection.getBlocked().insert(thread.getClientId(), id);
        OnlineUsers.updateBlockUnblock(thread, id);
    }

    public static void unblockUser(UserThread thread, String id) {
        Connection.getBlocked().delete(thread.getClientId(), id);
        OnlineUsers.updateBlockUnblock(thread, id);
    }

    public static boolean hasBlocked(UserThread thread, String id) {
        return Connection.getBlocked().hasBlocked(thread.getClientId(), id);
    }

    public static int getBlockStatus(UserThread thread, String id) {
        return Connection.getBlocked().getBlockState(thread.getClientId(), id);
    }

    public static ArrayList<OpenedChatModel> getBlockList(UserThread thread) {
        ArrayList<OpenedChatModel> list = Connection.getBlocked().get(thread.getClientId());
        try {
            ApiService.fixUsers(thread.getToken(), thread.getClientId(), list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void updateProfile(UserThread thread) {
        ApiService.users.asMap().remove(thread.getClientId());
        UserModel model;
        try {
            model = ApiService.getData(thread.getToken(), thread.getClientId());
        } catch (IOException e) {
            return;
        }
        if (model == null)
            return;
        ApiService.users.put(thread.getClientId(), model);
        OnlineUsers.notifyProfileUpdate(thread);
        List<IDModel> list = Connection.getOpenedChats().getOwners(thread.getClientId());
        OnlineUsers.requestUpdateProfile(list, ApiService.fullCopy(model));
    }

    public static List<DeviceInfo> getDevices(UserThread thread) {
        Vector<UserThread> vector = OnlineUsers.getDevices(thread);
        if (vector == null)
            return List.of(thread.getDeviceInfo());

        ArrayList<DeviceInfo> list = new ArrayList<>(vector.size());
        if (thread.getDeviceInfo() != null)
            list.add(thread.getDeviceInfo());

        for (UserThread t : vector) {
            if (t != thread && t.getDeviceInfo() != null)
                list.add(t.getDeviceInfo());
        }
        return list;
    }

    public static DeviceInfo authDeviceByQRCode(UserThread thread, String key, UserModel model) {
        UserThread qr = OnlineUsers.getDeviceByQRCode(key);
        if (qr == null || qr.getDeviceInfo() == null)
            return null;
        qr.registerByQRCode(thread, model);
        return qr.getDeviceInfo();
    }

    public static String getBotLink(UserThread thread) {
        return Connection.getBot().select(thread.getClientId());
    }

    public static boolean updateBotLink(UserThread thread, String link) {
        Connection.getBot().insert(thread.getClientId(), link);
        return true;
    }
}
