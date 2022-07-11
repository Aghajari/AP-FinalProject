package com.aghajari.store;

import com.aghajari.api.ApiService;
import com.aghajari.api.SocketApi;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;
import com.aghajari.shared.models.*;
import com.aghajari.ui.StaticListeners;
import com.aghajari.util.Utils;
import com.google.gson.Gson;
import javafx.application.Platform;

public class EasyApi {

    private static DeviceInfo getDeviceInfo(String token){
        return new DeviceInfo(
                token,
                Utils.getComputerName(),
                Utils.getOperatingSystemName(),
                Utils.getOperatingSystemType(),
                Utils.rnd()
        );
    }

    public static void auth(SocketApi.Listener listener) {
        SocketApi.Listener listener2;
        SocketApi.getInstance().register(SocketEvents.AUTH, listener2 = new SocketApi.Listener() {
            @Override
            public void onReceive(SocketModel model) {
                listener.onReceive(model);
                SocketApi.getInstance().unregister(this);
            }
        });

        if (!SocketApi.getInstance().write(new SocketModel(SocketEvents.AUTH,
                getDeviceInfo(ApiService.getToken())))) {
            SocketApi.getInstance().unregister(listener2);
            listener.onReceive(new SocketModel(SocketEvents.AUTH, false));
        }
    }

    public static void getChats(SocketApi.Listener listener) {
        String name = "getChats";
        SocketApi.getInstance().register(name, new SocketApi.Listener() {
            @Override
            public void onReceive(SocketModel model) {
                System.out.println("Hi");
                StaticStorage.chats = model.get();

                if (StaticListeners.updateOpenedChatsListener != null)
                    StaticListeners.updateOpenedChatsListener.update();

                listener.onReceive(model);
                SocketApi.getInstance().unregister(this);
            }
        });
        SocketApi.getInstance().write(new SocketModel(name));
    }

    public static void openChat(UserModel user, SocketApi.Listener listener) {
        String name = "openChat";
        SocketApi.getInstance().register(name, new SocketApi.Listener() {
            @Override
            public void onReceive(SocketModel model) {
                OpenedChatModel oc = model.get();
                oc.setUser(user);
                for (OpenedChatModel finder : StaticStorage.chats.openedChats) {
                    if (finder.userId.equals(oc.userId)) {
                        StaticStorage.chats.openedChats.remove(finder);
                        break;
                    }
                }

                StaticStorage.chats.openedChats.add(oc);

                if (StaticListeners.updateOpenedChatsListener != null)
                    StaticListeners.updateOpenedChatsListener.update();

                listener.onReceive(model);
                SocketApi.getInstance().unregister(this);
            }
        });
        SocketApi.getInstance().write(new SocketModel(name, user.getId()));
    }

    public static void requestQRCode(SocketApi.Listener listener) {
        SocketApi.getInstance().register(SocketEvents.QRCODE, new SocketApi.Listener() {
            @Override
            public void onReceive(SocketModel model) {
                listener.onReceive(model);
                SocketApi.getInstance().unregister(this);
            }
        });
        SocketApi.getInstance().write(new SocketModel(SocketEvents.QRCODE,
                getDeviceInfo(null)));
    }

    public static void closeChat(UserModel user) {
        String name = "closeChat";
        for (OpenedChatModel finder : StaticStorage.chats.openedChats) {
            if (finder.userId.equals(user.getId())) {
                StaticStorage.chats.openedChats.remove(finder);
                break;
            }
        }
        if (StaticListeners.updateOpenedChatsListener != null)
            StaticListeners.updateOpenedChatsListener.update();
        SocketApi.getInstance().write(new SocketModel(name, user.getId()));
    }

    public static void isOnline(UserModel user, SocketApi.Listener listener) {
        call("isOnline", listener, user.getId());
    }

    public static void registerCallListener(SocketApi.Listener listener) {
        SocketApi.Listener l = answer -> Platform.runLater(() -> listener.onReceive(answer));
        SocketApi.getInstance().clear(SocketEvents.ANSWER_CALL);
        SocketApi.getInstance().clear(SocketEvents.REJECT_CALL);
        SocketApi.getInstance().clear(SocketEvents.ALREADY_IN_CALL);
        SocketApi.getInstance().register(SocketEvents.ANSWER_CALL, l);
        SocketApi.getInstance().register(SocketEvents.REJECT_CALL, l);
        SocketApi.getInstance().register(SocketEvents.ALREADY_IN_CALL, l);
    }

    public static void requestCall(UserModel model) {
        SocketApi.getInstance().write(new SocketModel(SocketEvents.REQUEST_CALL, model.getId()));
    }

    public static void answerCall(UserModel model) {
        SocketApi.getInstance().write(new SocketModel(SocketEvents.ANSWER_CALL, model.getId()));
    }

    public static void rejectCall(UserModel model) {
        SocketApi.getInstance().write(new SocketModel(SocketEvents.REJECT_CALL, model.getId()));
    }

    public static void alreadyInCall(UserModel model) {
        SocketApi.getInstance().write(new SocketModel(SocketEvents.ALREADY_IN_CALL, model.getId()));
    }

    public static void friendshipRequest(int type, UserModel user) {
        friendshipRequest(type, user, -1);
    }

    public static void friendshipRequest(int type, UserModel user, int keyIndex) {
        SocketApi.getInstance().write(new SocketModel(SocketEvents.FRIENDSHIP_REQUEST,
                new FriendshipRequestModel(type, user.getId(), keyIndex)));
    }

    public static void getListOfMyFriends() {
        SocketApi.getInstance().write(new SocketModel("getMyFriends"));
    }

    public static void getListOfPendingFriends() {
        SocketApi.getInstance().write(new SocketModel("getMyPendingFriends"));
    }

    public static void getMutualFriends(SocketApi.Listener listener, String id) {
        call("getMutualFriends", listener, id);
    }

    public static void getOnlineFriends(SocketApi.Listener listener) {
        call("getOnlineFriends", listener);
    }

    public static void getMembersOfServer(String serverId, SocketApi.Listener listener) {
        call("getMembersOfServer", listener, serverId);
    }

    public static void getMessages(String id, Boolean fromServer, SocketApi.Listener listener) {
        if (!fromServer && id.equals(MyInfo.getInstance().getId())) {
            getMessages(listener);
            return;
        }
        call("getMessages", listener, id, fromServer);
    }

    public static void getMessages(SocketApi.Listener listener) {
        call("getMessages", listener);
    }

    public static void sendMessage(MessageModel model, SocketApi.Listener listener) {
        call("sendMessage", listener, model);
    }

    public static void reaction(Integer index, Integer reaction) {
        SocketApi.getInstance().write(new SocketModel("reaction", index, reaction));
    }

    public static void createServer(String serverName, String image, SocketApi.Listener listener) {
        call("createServer", listener, serverName, image);
    }

    public static void getUserInfo(String id, SocketApi.Listener listener) {
        call("getUserInfo", listener, id);
    }

    public static void deleteServer(String id, SocketApi.Listener listener) {
        call("deleteServer", listener, id);
    }

    public static void leaveServer(String id, SocketApi.Listener listener) {
        call("leaveServer", listener, id);
    }

    public static void removeFromServer(String id, String user) {
        SocketApi.getInstance().write(new SocketModel("removeFromServer", id, user));
    }

    public static void revokeServerInviteCode(String id, SocketApi.Listener listener) {
        call("revokeServerInviteCode", listener, id);
    }

    public static void getMemberPermission(String serverId, String userId, SocketApi.Listener listener) {
        call("getMemberPermission", listener, serverId, userId);
    }

    public static void updateMemberPermission(String serverId, String userId, Integer permission) {
        SocketApi.getInstance().write(new SocketModel("updateMemberPermission", serverId, userId, permission));
    }

    public static void joinServer(String code, SocketApi.Listener listener) {
        call("joinServer", listener, code);
    }

    public static void updateServerAvatar(ServerModel server) {
        SocketApi.getInstance().write(new SocketModel("updateServerAvatar", server.serverId, server.avatar));
    }

    public static void updateServerName(ServerModel server) {
        SocketApi.getInstance().write(new SocketModel("updateServerName", server.serverId, server.name));
    }

    public static void updateServerPermissions(ServerModel server) {
        SocketApi.getInstance().write(new SocketModel("updateServerPermissions", server.serverId, server.permissions));
    }

    public static void updateServerChannels(ServerModel server) {
        SocketApi.getInstance().write(new SocketModel("updateServerChannels", server.serverId,
                new Gson().toJson(server.channels)));
    }

    public static void getBlockList(SocketApi.Listener listener) {
        call("getBlockList", listener);
    }

    public static void hasBlocked(String id, SocketApi.Listener listener) {
        call("hasBlocked", listener, id);
    }

    public static void getBlockStatus(String id, SocketApi.Listener listener) {
        call("getBlockStatus", listener, id);
    }

    public static void block(String id, boolean block) {
        SocketApi.getInstance().write(new SocketModel(block ? "blockUser" : "unblockUser", id));
    }

    public static void updateProfile() {
        SocketApi.getInstance().write(new SocketModel("updateProfile"));
    }

    public static void getDevices(SocketApi.Listener listener) {
        call("getDevices", listener);
    }

    public static void getBotLink(SocketApi.Listener listener) {
        call("getBotLink", listener);
    }

    public static void updateBotLink(String link, SocketApi.Listener listener) {
        call("updateBotLink", listener, link);
    }

    public static void terminate(DeviceInfo device) {
        SocketApi.getInstance().write(new SocketModel(SocketEvents.TERMINATE, device.key));
    }

    public static void requestIsTyping(boolean typing, String id, String server) {
        SocketApi.getInstance().write(new SocketModel(SocketEvents.IS_TYPING,
                new IsTypingModel(typing, id, server, MyInfo.getInstance().getName())));
    }

    private static void call(String name, SocketApi.Listener listener, Object... args) {
        SocketApi.getInstance().register(name, new SocketApi.Listener() {
            @Override
            public void onReceive(SocketModel model) {
                Platform.runLater(() -> listener.onReceive(model));
                SocketApi.getInstance().unregister(this);
            }
        });
        SocketApi.getInstance().write(new SocketModel(name, args));
    }
}
