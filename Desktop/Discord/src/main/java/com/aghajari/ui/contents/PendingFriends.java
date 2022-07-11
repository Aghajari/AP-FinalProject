package com.aghajari.ui.contents;

import com.aghajari.shared.IDFinder;
import com.aghajari.shared.models.FriendshipModel;
import com.aghajari.store.EasyApi;
import com.aghajari.store.StaticStorage;
import com.aghajari.ui.StaticListeners;

import java.util.List;

public class PendingFriends extends MyFriends {

    @Override
    public void init() {
        StaticListeners.updatePendingFriendshipList = this::load;
    }

    @Override
    public List<? extends IDFinder> getList() {
        return StaticStorage.pendingFriendships;
    }

    @Override
    public void callApi() {
        EasyApi.getListOfPendingFriends();
    }

    @Override
    public String getSearchPrompt() {
        return "Search for requests";
    }

    @Override
    public String getImage() {
        return "pending.png";
    }

    @Override
    public String getEmptyText() {
        return "There are no pending friend request.";
    }

    @Override
    public int getType() {
        return 2;
    }
}
