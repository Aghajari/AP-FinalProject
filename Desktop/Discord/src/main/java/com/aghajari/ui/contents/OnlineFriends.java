package com.aghajari.ui.contents;

import com.aghajari.shared.IDFinder;
import com.aghajari.shared.models.IDModel;
import com.aghajari.store.EasyApi;
import javafx.scene.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OnlineFriends extends MyFriends {

    ArrayList<IDModel> models = new ArrayList<>();

    @Override
    public Node create() throws IOException {
        Node n = super.create();
        EasyApi.getOnlineFriends(model -> {
            models.clear();
            models.addAll(model.get());
            load();
        });
        return n;
    }

    @Override
    public void init() {
    }

    @Override
    public List<? extends IDFinder> getList() {
        return models;
    }

    @Override
    public void callApi() {
    }

    @Override
    public String getSearchPrompt() {
        return "Search for online friends";
    }

    @Override
    public String getImage() {
        return "online.png";
    }

    @Override
    public String getEmptyText() {
        return "No one's around :(";
    }

    @Override
    public int getType() {
        return 0;
    }
}
