package com.aghajari.store;

import com.aghajari.shared.ChatsList;
import com.aghajari.shared.models.FriendshipModel;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class StaticStorage {

    public static ChatsList chats = new ChatsList();
    public static final List<FriendshipModel> friendships = new ArrayList<>();
    public static final List<FriendshipModel> pendingFriendships = new ArrayList<>();


    public static Image spaceImage = null;
}
