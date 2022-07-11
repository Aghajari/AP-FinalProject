package com.aghajari.store;

import android.os.Build;
import android.provider.Settings;

import com.aghajari.Application;
import com.aghajari.api.ApiService;
import com.aghajari.api.SocketApi;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;
import com.aghajari.shared.models.*;
import com.aghajari.views.Utils;
import com.google.gson.Gson;

import java.lang.reflect.Field;

public class EasyApi {

    public static void auth(SocketApi.Listener listener) {
        SocketApi.Listener listener2;
        SocketApi.getInstance().register(SocketEvents.AUTH, listener2 = new SocketApi.Listener() {
            @Override
            public void onReceive(SocketModel model) {
                listener.onReceive(model);
                SocketApi.getInstance().unregister(this);
            }
        });

        String deviceName = Settings.Global.getString(
                Application.context.getContentResolver(), "device_name");
        if (deviceName == null || deviceName.trim().isEmpty())
            deviceName = Build.MANUFACTURER + " " + Build.MODEL;

        String osName = "Android " + Build.VERSION.RELEASE;
        try {
            Field[] fields = Build.VERSION_CODES.class.getFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                int fieldValue = -1;

                try {
                    fieldValue = field.getInt(null);
                } catch (Exception ignore) {
                }

                if (fieldValue == Build.VERSION.SDK_INT) {
                    fieldName = fieldName.replace('_', ' ').toLowerCase();
                    if (fieldName.length() == 1)
                        fieldName = fieldName.toUpperCase();
                    else
                        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    osName += " " + fieldName;
                    break;
                }
            }
        } catch (Exception ignore) {
        }
        osName += " (API " + Build.VERSION.SDK_INT + ")";

        DeviceInfo info = new DeviceInfo(
                ApiService.getToken(),
                deviceName.trim(),
                osName,
                DeviceInfo.ANDROID,
                Utils.rnd()
        );
        if (!SocketApi.getInstance().write(new SocketModel(SocketEvents.AUTH, info))) {
            SocketApi.getInstance().unregister(listener2);
            listener.onReceive(new SocketModel(SocketEvents.AUTH, false));
        }
    }

    public static void getChats(SocketApi.Listener listener) {
        String name = "getChats";
        SocketApi.getInstance().register(name, new SocketApi.Listener() {
            @Override
            public void onReceive(SocketModel model) {
                Utils.ui(() -> listener.onReceive(model));
                SocketApi.getInstance().unregister(this);
            }
        });
        SocketApi.getInstance().write(new SocketModel(name));
    }

    public static void openChat(UserModel user) {
        SocketApi.getInstance().write(new SocketModel("openChat", user.getId()));
    }

    public static void closeChat(UserModel user) {
        SocketApi.getInstance().write(new SocketModel("closeChat", user.getId()));
    }

    public static void isChatOpen(UserModel user, SocketApi.Listener listener) {
        call("isChatOpen", listener, user.getId());
    }

    public static void isOnline(UserModel user, SocketApi.Listener listener) {
        call("isOnline", listener, user.getId());
    }

    public static void registerCallListener(SocketApi.Listener listener) {
        SocketApi.getInstance().clear(SocketEvents.ANSWER_CALL);
        SocketApi.getInstance().clear(SocketEvents.REJECT_CALL);
        SocketApi.getInstance().clear(SocketEvents.ALREADY_IN_CALL);
        SocketApi.getInstance().register(SocketEvents.ANSWER_CALL, listener);
        SocketApi.getInstance().register(SocketEvents.REJECT_CALL, listener);
        SocketApi.getInstance().register(SocketEvents.ALREADY_IN_CALL, listener);
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

    public static void getFriendshipStatus(String id, SocketApi.Listener listener) {
        call("getFriendshipStatus", listener, id);
    }

    public static void getListOfMyFriends(SocketApi.Listener listener) {
        call("getMyFriends", listener);
    }

    public static void getListOfPendingFriends(SocketApi.Listener listener) {
        call("getMyPendingFriends", listener);
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
        call2("getMessages", listener, id, fromServer);
    }

    public static void getMessages(SocketApi.Listener listener) {
        call2("getMessages", listener);
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

    public static void deleteServer(String id) {
        SocketApi.getInstance().write(new SocketModel("deleteServer", id));
    }

    public static void leaveServer(String id) {
        SocketApi.getInstance().write(new SocketModel("leaveServer", id));
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

    public static void requestIsTyping(boolean typing, String id, String server) {
        SocketApi.getInstance().write(new SocketModel(SocketEvents.IS_TYPING,
                new IsTypingModel(typing, id, server, MyInfo.getInstance().getName())));
    }

    public static void getDevices(SocketApi.Listener listener) {
        call("getDevices", listener);
    }

    public static void authDeviceByQRCode(String key, SocketApi.Listener listener) {
        call("authDeviceByQRCode", listener, key, MyInfo.getInstance());
    }

    public static void terminate(DeviceInfo device) {
        SocketApi.getInstance().write(new SocketModel(SocketEvents.TERMINATE, device.key));
    }

    public static void getBotLink(SocketApi.Listener listener) {
        call("getBotLink", listener);
    }

    public static void updateBotLink(String link, SocketApi.Listener listener) {
        call("updateBotLink", listener, link);
    }

    private static void call(String name, SocketApi.Listener listener, Object... args) {
        SocketApi.getInstance().register(name, new SocketApi.Listener() {
            @Override
            public void onReceive(SocketModel model) {
                Utils.ui(() -> listener.onReceive(model));
                SocketApi.getInstance().unregister(this);
            }
        });
        SocketApi.getInstance().write(new SocketModel(name, args));
    }

    private static void call2(String name, SocketApi.Listener listener, Object... args) {
        SocketApi.getInstance().register(name, new SocketApi.Listener() {
            @Override
            public void onReceive(SocketModel model) {
                listener.onReceive(model);
                SocketApi.getInstance().unregister(this);
            }
        });
        SocketApi.getInstance().write(new SocketModel(name, args));
    }
}
