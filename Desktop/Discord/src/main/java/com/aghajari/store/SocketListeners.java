package com.aghajari.store;

import com.aghajari.MainUI;
import com.aghajari.api.ApiService;
import com.aghajari.api.SocketApi;
import com.aghajari.call.CallUI;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.models.OpenedChatModel;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.ui.HomeController;
import com.aghajari.ui.StaticListeners;
import com.aghajari.util.Toast;
import javafx.application.Platform;
import javafx.stage.Stage;

public class SocketListeners {

    private SocketListeners() {
    }

    public static void init() {

        SocketApi.getInstance().register(SocketEvents.UPDATE_MY_PROFILE, model -> {
            UserModel m = model.get();
            if (m == null)
                return;

            if (m.getId().equals(MyInfo.getInstance().getId())) {
                if (!m.avatar.equals(MyInfo.getInstance().avatar)) {
                    MyInfo.getInstance().setImage(null);
                    MyInfo.getInstance().setColor(null);
                }
                MyInfo.getInstance().avatar = m.avatar;
                MyInfo.getInstance().nickname = m.nickname;
                MyInfo.getInstance().email = m.email;
                MyInfo.getInstance().username = m.username;
                ApiService.saveUser(MyInfo.getInstance());

                if (StaticListeners.updateMyProfile != null)
                    Platform.runLater(() -> {
                        if (StaticListeners.updateMyProfile != null)
                            StaticListeners.updateMyProfile.update();
                    });

                if (StaticListeners.updateProfile != null)
                    Platform.runLater(() -> {
                        if (StaticListeners.updateProfile != null)
                            StaticListeners.updateProfile.update(MyInfo.getInstance());
                    });
            }
        });

        SocketApi.getInstance().register(SocketEvents.UPDATE_TOKEN, model ->
                ApiService.saveToken(model.get()));

        SocketApi.getInstance().register(SocketEvents.REQUEST_UPDATE_PROFILE, model -> {
            if (StaticListeners.updateOnlineStatus != null)
                StaticListeners.updateOnlineStatus.update();
        });

        SocketApi.getInstance().register(SocketEvents.REQUEST_CALL, model -> {
            if (CallUI.isInCall()) {
                EasyApi.alreadyInCall(model.get());
                return;
            }
            Platform.runLater(() -> CallUI.start(model.get(), false));
        });

        SocketApi.getInstance().register(SocketEvents.FRIENDSHIP_NOTIFY, model -> {
            if (StaticListeners.friendshipUpdater != null)
                Platform.runLater(() -> {
                    if (StaticListeners.friendshipUpdater != null)
                        StaticListeners.friendshipUpdater.update(model.get());
                });
        });

        SocketApi.getInstance().register(SocketEvents.MESSAGE_NOTIFY, model -> {
            if (StaticListeners.messageUpdater != null)
                Platform.runLater(() -> {
                    if (StaticListeners.messageUpdater != null)
                        StaticListeners.messageUpdater.update(model.get());
                });
        });

        SocketApi.getInstance().register("getMyFriends", model -> {
            StaticStorage.friendships.clear();
            StaticStorage.friendships.addAll(model.get());

            if (StaticListeners.updateFriendshipList != null)
                Platform.runLater(() -> {
                    if (StaticListeners.updateFriendshipList != null)
                        StaticListeners.updateFriendshipList.update();
                });
        });

        SocketApi.getInstance().register("getMyPendingFriends", model -> {
            StaticStorage.pendingFriendships.clear();
            StaticStorage.pendingFriendships.addAll(model.get());

            if (StaticListeners.updatePendingFriendshipList != null)
                Platform.runLater(() -> {
                    if (StaticListeners.updatePendingFriendshipList != null)
                        StaticListeners.updatePendingFriendshipList.update();
                });
        });

        SocketApi.getInstance().register(SocketEvents.REQUEST_UPDATE_SERVER, model -> {
            ServerModel server = model.get();
            try {
                synchronized (StaticStorage.class) {
                    for (int i = 0; i < StaticStorage.chats.servers.size(); i++) {
                        ServerModel old = StaticStorage.chats.servers.get(i);
                        if (old.serverId.equals(server.serverId)) {
                            if (server.permissions2 == -1 && server.permissions == -1) {
                                StaticStorage.chats.servers.remove(i);
                                HomeController.forceUpdate = true;
                            } else {
                                if (server.avatar.equals(old.avatar)) {
                                    server.setImage(old.getImage());
                                    server.setColor(old.getColor());
                                }
                                server.permissions2 = old.permissions2;
                                StaticStorage.chats.servers.set(i, server);
                            }
                            break;
                        }
                    }
                }
            } catch (Exception ignore) {
            }

            if (StaticListeners.updateOpenedChatsListener != null)
                Platform.runLater(() -> {
                    HomeController.keepOldIndex = true;
                    if (StaticListeners.updateOpenedChatsListener != null)
                        StaticListeners.updateOpenedChatsListener.update();
                });
        });

        SocketApi.getInstance().register(SocketEvents.REQUEST_UPDATE_PERMISSION, model -> {
            ServerModel server = model.get();
            try {
                synchronized (StaticStorage.class) {
                    for (int i = 0; i < StaticStorage.chats.servers.size(); i++) {
                        ServerModel old = StaticStorage.chats.servers.get(i);
                        if (old.serverId.equals(server.serverId)) {
                            old.permissions2 = server.permissions2;
                            break;
                        }
                    }
                }
            } catch (Exception ignore) {
            }

            if (StaticListeners.updateOpenedChatsListener != null)
                Platform.runLater(() -> {
                    HomeController.keepOldIndex = true;
                    if (StaticListeners.updateOpenedChatsListener != null)
                        StaticListeners.updateOpenedChatsListener.update();
                });
        });

        SocketApi.getInstance().register(SocketEvents.IS_TYPING, model -> {
            if (StaticListeners.isTypingUpdater != null)
                Platform.runLater(() -> {
                    if (StaticListeners.isTypingUpdater != null)
                        StaticListeners.isTypingUpdater.update(model.get());
                });
        });

        SocketApi.getInstance().register(SocketEvents.UPDATE_BLOCK_UNBLOCK, model -> {
            UserModel user = model.get();
            boolean opened = false;
            try {
                synchronized (StaticStorage.class) {
                    for (OpenedChatModel u : StaticStorage.chats.openedChats) {
                        if (u.getUser().getId().equals(user.getId())) {
                            opened = true;
                            u.getUser().avatar = user.avatar;
                            u.getUser().setImage(null);
                            u.getUser().setColor(null);
                            break;
                        }
                    }
                }
            } catch (Exception ignore) {
            }

            if (StaticListeners.updateBlockUnblock != null)
                Platform.runLater(() -> {
                    if (StaticListeners.updateBlockUnblock != null)
                        StaticListeners.updateBlockUnblock.update(user);
                });

            if (opened && StaticListeners.updateProfile != null)
                Platform.runLater(() -> {
                    if (StaticListeners.updateProfile != null)
                        StaticListeners.updateProfile.update(user);
                });
        });

        SocketApi.getInstance().register(SocketEvents.UPDATE_PROFILE, model -> {
            UserModel user = model.get();
            boolean opened = false;
            try {
                synchronized (StaticStorage.class) {
                    for (OpenedChatModel u : StaticStorage.chats.openedChats) {
                        if (u.getUser().getId().equals(user.getId())) {
                            opened = true;
                            if (!u.getUser().avatar.equals(user.avatar)) {
                                u.getUser().avatar = user.avatar;
                                u.getUser().setImage(null);
                                u.getUser().setColor(null);
                            }
                            u.getUser().nickname = user.nickname;
                            u.getUser().username = user.username;
                            u.getUser().email = user.email;
                            break;
                        }
                    }
                }
            } catch (Exception ignore) {
            }

            if (opened && StaticListeners.updateProfile != null)
                Platform.runLater(() -> {
                    if (StaticListeners.updateProfile != null)
                        StaticListeners.updateProfile.update(user);
                });
        });

        SocketApi.getInstance().register(SocketEvents.OPEN_CHAT, model -> {
            if (model.data instanceof ServerModel) {
                ServerModel me = model.get();
                for (ServerModel m : StaticStorage.chats.servers) {
                    if (m.serverId.equals(me.serverId))
                        return;
                }
                StaticStorage.chats.servers.add(me);
            } else {
                OpenedChatModel me = model.get();
                for (OpenedChatModel m : StaticStorage.chats.openedChats) {
                    if (m.userId.equals(me.userId))
                        return;
                }
                StaticStorage.chats.openedChats.add(me);
            }

            if (StaticListeners.updateOpenedChatsListener != null)
                Platform.runLater(() -> {
                    if (StaticListeners.updateOpenedChatsListener != null) {
                        HomeController.keepOldProfile = true;
                        StaticListeners.updateOpenedChatsListener.update();
                    }
                });
        });

        SocketApi.getInstance().register(SocketEvents.TERMINATE, model -> {
            Platform.runLater(() -> {
                ApiService.logOut();
                Toast.ownerStage.close();
                try {
                    new MainUI().start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
