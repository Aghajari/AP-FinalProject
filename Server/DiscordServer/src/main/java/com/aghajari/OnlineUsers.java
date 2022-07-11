package com.aghajari;

import com.aghajari.api.ApiService;
import com.aghajari.api.SocketApi;
import com.aghajari.database.Connection;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;
import com.aghajari.shared.models.*;

import java.io.IOException;
import java.util.*;

public class OnlineUsers {

    private OnlineUsers() {
    }

    private final static Hashtable<String, Vector<UserThread>> onlineUsers = new Hashtable<>();
    private final static Hashtable<String, UserThread> waitingForQR = new Hashtable<>();
    private final static Hashtable<String, HashMap<String, String>> typings = new Hashtable<>();

    public static Vector<UserThread> getDevices(UserThread thread) {
        if (thread == null || thread.getClientId() == null)
            return null;
        return onlineUsers.get(thread.getClientId());
    }

    public static void addQRListener(String key, UserThread thread) {
        waitingForQR.put(key, thread);
    }

    public static void removeQR(String key) {
        waitingForQR.remove(key);
    }

    public static UserThread getDeviceByQRCode(String key) {
        return waitingForQR.get(key);
    }

    public static void addUser(String id, UserThread thread) {
        Vector<UserThread> vector = onlineUsers.get(id);
        if (vector == null)
            vector = new Vector<>();
        vector.add(thread);
        onlineUsers.put(id, vector);

        if (thread.getQRKey() == null) {
            for (UserThread t : vector) {
                if (t != null && t != thread) {
                    t.setToken(thread.getToken());
                    t.write(new SocketModel(SocketEvents.UPDATE_TOKEN, thread.getToken()));
                }
            }
        }
        notify(id);
    }

    public static void notifyProfileUpdate(UserThread thread) {
        Vector<UserThread> vector = onlineUsers.get(thread.getClientId());
        if (vector == null)
            return;

        UserModel model = null;
        for (UserThread t : vector) {
            if (t != null && t != thread) {
                if (model == null) {
                    try {
                        model = ApiService.getData(thread.getToken(), thread.getClientId());
                    } catch (IOException ignore) {
                    }
                }
                if (model == null)
                    break;
                t.write(new SocketModel(SocketEvents.UPDATE_MY_PROFILE, model));
            }
        }
    }

    public static void removeUser(UserThread user) {
        if (user == null || user.getClientId() == null)
            return;
        Vector<UserThread> vector = onlineUsers.get(user.getClientId());
        if (vector == null)
            return;

        vector.remove(user);
        if (vector.isEmpty())
            onlineUsers.remove(user.getClientId());
        notify(user.getClientId());
    }

    public static boolean isOnline(String id) {
        return onlineUsers.containsKey(id) && !onlineUsers.get(id).isEmpty();
    }

    public static void send(String id, SocketModel model) {
        try {
            if (isOnline(id)) {
                for (UserThread thread : onlineUsers.get(id))
                    thread.write(model);
            }
        } catch (Exception ignore) {
        }
    }

    public static void sendFriendship(UserThread main, String id, SocketModel model) {
        try {
            if (isOnline(id)) {
                for (UserThread thread : onlineUsers.get(id))
                    thread.write(model);
            }
            Vector<UserThread> vector = onlineUsers.get(main.getClientId());
            for (UserThread thread : vector) {
                if (thread != null && thread != main)
                    thread.write(model);
            }
        } catch (Exception ignore) {
        }
    }

    private static void notify(String id) {
        synchronized (onlineUsers) {
            for (Vector<UserThread> vector : onlineUsers.values()) {
                for (UserThread thread : vector)
                    if (id.equals(thread.getCheckOnlineForUserId()))
                        thread.write(new SocketModel(SocketEvents.REQUEST_UPDATE_PROFILE, id));
            }
        }
    }

    public static void sendMessage(UserThread current, MessageModel model) {
        sendMessage(current, model, false);
    }

    public static void sendMessage(UserThread current, MessageModel model, boolean bot) {
        synchronized (onlineUsers) {
            if (model.toId.contains("#") && !model.fromId.contains("#")) {
                for (Vector<UserThread> vector : onlineUsers.values()) {
                    for (UserThread thread : vector) {
                        if (thread == current)
                            continue;
                        if (model.fromId.equals(thread.getCheckChatForUserId())
                                || model.toId.equals(thread.getCheckChatForUserId()))
                            thread.write(new SocketModel(SocketEvents.MESSAGE_NOTIFY, model), true);
                    }
                }
            } else {
                Vector<UserThread> vector = null, vector2;
                if (bot) {
                    vector = onlineUsers.get(model.fromId);
                    vector2 = onlineUsers.get(model.toId);
                } else {
                    vector2 = onlineUsers.get(current.getClientId());
                    if (model.toId.equals(current.getClientId())) {
                        vector = onlineUsers.get(model.fromId);
                    } else if (model.fromId.equals(current.getClientId())) {
                        vector = onlineUsers.get(model.toId);
                    }
                }

                if (vector != null)
                    for (UserThread thread : vector) {
                        if (thread != null && thread != current)
                            thread.write(new SocketModel(SocketEvents.MESSAGE_NOTIFY, model), true);
                    }
                if (vector2 != null)
                    for (UserThread thread : vector2) {
                        if (thread != null && thread != current)
                            thread.write(new SocketModel(SocketEvents.MESSAGE_NOTIFY, model), true);
                    }
            }
        }
    }

    public static void requestUpdatePermission(String userId, String serverId, int permissions) {
        synchronized (onlineUsers) {
            ServerModel serverModel = new ServerModel(serverId);
            serverModel.permissions = serverModel.permissions2 = permissions;

            Vector<UserThread> vector = onlineUsers.get(userId);

            if (vector != null)
                for (UserThread thread : vector)
                    thread.write(new SocketModel(SocketEvents.REQUEST_UPDATE_PERMISSION, serverModel));
        }
    }

    public static void requestDeleteUserFromServer(String userId, String serverId) {
        synchronized (onlineUsers) {
            ServerModel serverModel = new ServerModel(serverId);
            serverModel.permissions = serverModel.permissions2 = -1;

            Vector<UserThread> vector = onlineUsers.get(userId);

            if (vector != null)
                for (UserThread thread : vector)
                    thread.write(new SocketModel(SocketEvents.REQUEST_UPDATE_SERVER, serverModel));
        }
    }

    public static void requestDeleteServer(UserThread main, String serverId) {
        synchronized (onlineUsers) {
            ServerModel serverModel = new ServerModel(serverId);
            serverModel.permissions = serverModel.permissions2 = -1;
            ArrayList<IDModel> ids = Connection.getUserServers().select(serverId);
            Connection.getUserServers().deleteAll(serverId);

            for (IDModel id : ids) {
                Vector<UserThread> vector = onlineUsers.get(id.id);

                if (vector != null)
                    for (UserThread thread : vector)
                        if (thread != null && thread != main)
                            thread.write(new SocketModel(SocketEvents.REQUEST_UPDATE_SERVER, serverModel));
            }
        }
    }

    public static void requestUpdateServer(UserThread main, String serverId) {
        synchronized (onlineUsers) {
            ServerModel serverModel = Connection.getServers().get(serverId);
            ArrayList<IDModel> ids = Connection.getUserServers().select(serverId);
            for (IDModel id : ids) {
                Vector<UserThread> vector = onlineUsers.get(id.id);

                if (vector != null)
                    for (UserThread thread : vector)
                        if (thread != null && thread != main)
                            thread.write(new SocketModel(SocketEvents.REQUEST_UPDATE_SERVER, serverModel));
            }
        }
    }

    public static void requestLeaveServerToMe(UserThread main, String serverId) {
        Vector<UserThread> vector = onlineUsers.get(main.getClientId());
        if (vector == null || vector.size() <= 1)
            return;
        ServerModel serverModel = new ServerModel(serverId);
        serverModel.permissions = serverModel.permissions2 = -1;

        for (UserThread thread : vector)
            if (thread != null && thread != main)
                thread.write(new SocketModel(SocketEvents.REQUEST_UPDATE_SERVER, serverModel));
    }

    public synchronized static void requestIsTyping(UserThread thread, String id, boolean server) {
        if (server) {
            HashMap<String, String> list = typings.get(id);
            if (list != null) {
                IsTypingModel.IsTypingResponse res = new IsTypingModel.IsTypingResponse(new HashMap<>(list), id);
                thread.write(new SocketModel(SocketEvents.IS_TYPING, res), true);
            }
        } else {
            Vector<UserThread> vector = onlineUsers.get(id);

            if (vector != null)
                for (UserThread m : vector)
                    if (m != null && m.getTypingModel() != null
                            && m.getTypingModel().serverId == null
                            && m.getTypingModel().isTyping
                            && m.getTypingModel().id.equals(thread.getClientId())) {

                        HashMap<String, String> map = new HashMap<>(2);
                        map.put(m.getClientId(), m.getTypingModel().name);

                        thread.write(new SocketModel(SocketEvents.IS_TYPING,
                                new IsTypingModel.IsTypingResponse(map, m.getClientId())), true);
                    }
        }
    }

    public synchronized static void requestIsTyping(UserThread main, IsTypingModel model) {
        if (model.serverId != null) {
            HashMap<String, String> list = typings.get(model.id);
            if (model.isTyping) {
                if (list == null) {
                    list = new HashMap<>();
                    typings.put(model.id, list);
                }
                list.put(main.getClientId(), model.name);
            } else if (list != null) {
                list.remove(main.getClientId());
                if (list.isEmpty())
                    typings.remove(model.id);
            }

            if (list == null)
                list = new HashMap<>();

            IsTypingModel.IsTypingResponse res = new IsTypingModel.IsTypingResponse(new HashMap<>(list), model.id);
            ArrayList<IDModel> ids = Connection.getUserServers().select(model.serverId);
            for (IDModel id : ids) {
                Vector<UserThread> vector = onlineUsers.get(id.id);

                if (vector != null)
                    for (UserThread thread : vector)
                        if (thread != null && thread != main)
                            thread.write(new SocketModel(SocketEvents.IS_TYPING, res), true);
            }
        } else {
            Vector<UserThread> vector = onlineUsers.get(model.id);

            if (vector != null)
                for (UserThread thread : vector)
                    if (thread != null && thread != main) {
                        HashMap<String, String> map = new HashMap<>(2);
                        if (model.isTyping)
                            map.put(main.getClientId(), model.name);

                        thread.write(new SocketModel(SocketEvents.IS_TYPING,
                                new IsTypingModel.IsTypingResponse(map, main.getClientId())), true);
                    }
        }
    }

    public static void updateBlockUnblock(UserThread thread, String id) {
        Vector<UserThread> vector = onlineUsers.get(id);

        if (vector != null)
            for (UserThread t : vector)
                if (t != null) {
                    UserModel m = SocketApi.getUserInfo(t, thread.getClientId());
                    if (m != null) {
                        m = ApiService.fullCopy(m);
                        t.write(new SocketModel(SocketEvents.UPDATE_BLOCK_UNBLOCK, m));
                    }
                }

        vector = onlineUsers.get(thread.getClientId());
        UserModel m = null;
        if (vector != null)
            for (UserThread t : vector)
                if (t != null && t != thread) {
                    if (m == null)
                        m = SocketApi.getUserInfo(thread, id);
                    if (m == null)
                        break;
                    t.write(new SocketModel(SocketEvents.UPDATE_BLOCK_UNBLOCK, m));
                }
    }

    public static void requestUpdateProfile(List<IDModel> ids, UserModel model) {
        if (ids == null || model == null)
            return;
        for (IDModel id : ids) {
            Vector<UserThread> vector = onlineUsers.get(id.id);

            if (vector != null)
                for (UserThread thread : vector)
                    if (thread != null)
                        thread.write(new SocketModel(SocketEvents.UPDATE_PROFILE, model));
        }
    }

    public static void openChat(String id, Object data) {
        Vector<UserThread> vector = onlineUsers.get(id);
        if (vector == null || data == null)
            return;

        for (UserThread thread : vector)
            if (thread != null)
                thread.write(new SocketModel(SocketEvents.OPEN_CHAT, data));
    }

    public static void openChat(UserThread main, Object data, boolean includingMe) {
        Vector<UserThread> vector = onlineUsers.get(main.getClientId());
        if (vector == null || data == null)
            return;

        for (UserThread thread : vector)
            if (thread != null && (includingMe || thread != main))
                thread.write(new SocketModel(SocketEvents.OPEN_CHAT, data));
    }
}
