package com.aghajari.api;

import com.aghajari.shared.models.ServerModel;

public class PermissionUtils {

    public static int SEND_MESSAGE = 0b1;
    public static int SEE_INVITE_CODE = 0b10;

    public static int NOT_SEND_MESSAGE = 0b100;
    public static int NOT_SEE_INVITE_CODE = 0b1000;

    public static int CHANGE_SERVER_PROFILE = 0b10000;
    public static int CHANGE_SERVER_NAME = 0b100000;

    public static int NOT_CHANGE_SERVER_PROFILE = 0b1000000;
    public static int NOT_CHANGE_SERVER_NAME = 0b10000000;

    public static int REVOKE_INVITE_CODE = 0b100000000;

    public static int REMOVE_MEMBERS = 0b1000000000;
    public static int CHANGE_PERMISSIONS = 0b10000000000;
    public static int SEND_MESSAGE_TO_CHANNELS = 0b100000000000;

    public static boolean canSendMessage(String id, ServerModel serverModel, ServerModel.ServerChannel channel) {
        if (isOwner(id, serverModel))
            return true;
        if (!hasPermission(serverModel, SEND_MESSAGE, NOT_SEND_MESSAGE))
            return false;
        return channel.type != 0 || hasPermission(serverModel.permissions2, SEND_MESSAGE_TO_CHANNELS);
    }

    public static boolean hasPermission(ServerModel serverModel, int key, int notKey) {
        if (notKey == 0)
            return hasPermission(serverModel.permissions2, key);

        return hasPermission(serverModel.permissions2, key)
                || (hasPermission(serverModel.permissions, key) &&
                !hasPermission(serverModel.permissions2, notKey));
    }

    public static boolean hasPermission(int p, int key) {
        return (p & key) == key;
    }

    public static boolean isOwner(String id, ServerModel serverModel) {
        return serverModel.owner.equals(id);
    }
}
