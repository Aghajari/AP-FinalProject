package com.aghajari.store;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.aghajari.Application;
import com.aghajari.api.ApiService;
import com.aghajari.api.SocketApi;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.models.MessageModel;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.views.NotificationCenter;
import com.aghajari.views.Utils;

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
                    Utils.ui(() -> {
                        if (StaticListeners.updateMyProfile != null)
                            StaticListeners.updateMyProfile.update();
                    });
            }
        });

        SocketApi.getInstance().register(SocketEvents.UPDATE_TOKEN, model ->
                ApiService.saveToken(model.get()));

        SocketApi.getInstance().register(SocketEvents.REQUEST_UPDATE_PROFILE, model -> {
            if (StaticListeners.updateOnlineStatus != null)
                Utils.ui(() -> {
                    if (StaticListeners.updateOnlineStatus != null)
                        StaticListeners.updateOnlineStatus.update();
                });
        });

        SocketApi.getInstance().register(SocketEvents.FRIENDSHIP_NOTIFY, model -> {
            if (StaticListeners.friendshipUpdater != null)
                Utils.ui(() -> {
                    if (StaticListeners.friendshipUpdater != null)
                        StaticListeners.friendshipUpdater.update(model.get());
                });
        });

        SocketApi.getInstance().register(SocketEvents.MESSAGE_NOTIFY, model -> {
            MessageModel msg = model.get();
            boolean notify = true;
            if (StaticListeners.messageUpdater != null)
                notify = !StaticListeners.messageUpdater.update(msg);

            System.out.println(notify);
            if (notify)
                NotificationCenter.notify(msg);
        });

        SocketApi.getInstance().register(SocketEvents.IS_TYPING, model -> {
            if (StaticListeners.isTypingUpdater != null)
                Utils.ui(() -> {
                    if (StaticListeners.isTypingUpdater != null)
                        StaticListeners.isTypingUpdater.update(model.get());
                });
        });

        SocketApi.getInstance().register(SocketEvents.UPDATE_BLOCK_UNBLOCK, model -> {
            if (StaticListeners.updateBlockUnblock != null)
                Utils.ui(() -> {
                    if (StaticListeners.updateBlockUnblock != null)
                        StaticListeners.updateBlockUnblock.update(model.get());
                });

            if (StaticListeners.updateProfileBlockUnblock != null)
                Utils.ui(() -> {
                    if (StaticListeners.updateProfileBlockUnblock != null)
                        StaticListeners.updateProfileBlockUnblock.update(model.get());
                });

            if (StaticListeners.updateProfile != null)
                Utils.ui(() -> {
                    if (StaticListeners.updateProfile != null)
                        StaticListeners.updateProfile.update(model.get());
                });
        });

        SocketApi.getInstance().register(SocketEvents.UPDATE_PROFILE, model -> {
            if (StaticListeners.updateProfile != null)
                Utils.ui(() -> {
                    if (StaticListeners.updateProfile != null)
                        StaticListeners.updateProfile.update(model.get());
                });
        });

        SocketApi.getInstance().register(SocketEvents.REQUEST_UPDATE_SERVER, model -> {
            ServerModel m = model.get();
            if (m.permissions == -1 && m.permissions2 == -1) {
                if (StaticListeners.updateOpenedChatsListener != null)
                    Utils.ui(() -> {
                        if (StaticListeners.updateOpenedChatsListener != null)
                            StaticListeners.updateOpenedChatsListener.update();
                    });
            }

            if (StaticListeners.updateServer != null)
                Utils.ui(() -> {
                    if (StaticListeners.updateServer != null)
                        StaticListeners.updateServer.update(m);
                });
        });

        SocketApi.getInstance().register(SocketEvents.REQUEST_UPDATE_PERMISSION, model -> {
            ServerModel m = model.get();
            if (m.permissions == -1 && m.permissions2 == -1) {
                if (StaticListeners.updateOpenedChatsListener != null)
                    Utils.ui(() -> {
                        if (StaticListeners.updateOpenedChatsListener != null)
                            StaticListeners.updateOpenedChatsListener.update();
                    });
            }

            if (StaticListeners.updateServer != null)
                Utils.ui(() -> {
                    if (StaticListeners.updateServer != null)
                        StaticListeners.updateServer.update(m);
                });
        });

        SocketApi.getInstance().register(SocketEvents.OPEN_CHAT, model -> {
            if (StaticListeners.updateOpenedChatsListener != null)
                Utils.ui(() -> {
                    if (StaticListeners.updateOpenedChatsListener != null)
                        StaticListeners.updateOpenedChatsListener.update();
                });
        });

        SocketApi.getInstance().register(SocketEvents.TERMINATE, model -> {
            ApiService.logOut();
            PackageManager packageManager = Application.context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(Application.context.getPackageName());
            ComponentName componentName = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(componentName);
            Application.context.startActivity(mainIntent);
            android.os.Process.killProcess(android.os.Process.myPid());
        });
    }
}
