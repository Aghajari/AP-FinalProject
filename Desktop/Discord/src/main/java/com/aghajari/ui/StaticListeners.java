package com.aghajari.ui;

import com.aghajari.shared.models.FriendshipModel;
import com.aghajari.shared.models.IsTypingModel;
import com.aghajari.shared.models.MessageModel;
import com.aghajari.shared.models.UserModel;

public class StaticListeners {

    public static Updater updateOpenedChatsListener;
    public static Updater updateMyProfile;
    public static Updater updateOnlineStatus;
    public static Updater updateFriendshipList;
    public static Updater updatePendingFriendshipList;
    public static GenericUpdater<FriendshipModel> friendshipUpdater;
    public static ResponseUpdater<MessageModel, Boolean> messageUpdater;
    public static GenericUpdater<IsTypingModel.IsTypingResponse> isTypingUpdater;
    public static GenericUpdater<UserModel> updateBlockUnblock;
    public static GenericUpdater<UserModel> updateProfile;
    public static ResponseUpdater<UserModel, Boolean> updateProfileContent;

    public interface Updater {
        void update();
    }

    public interface GenericUpdater<T> {
        void update(T t);
    }

    public interface ResponseUpdater<T, R> {
        R update(T t);
    }

}
