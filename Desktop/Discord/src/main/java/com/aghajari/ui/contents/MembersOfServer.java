package com.aghajari.ui.contents;

import com.aghajari.shared.IDFinder;
import com.aghajari.shared.models.IDModel;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.store.EasyApi;
import com.aghajari.util.Utils;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MembersOfServer extends MyFriends {

    private final ServerModel serverModel;
    ArrayList<IDModel> models = new ArrayList<>();

    public MembersOfServer(ServerModel serverModel) {
        this.serverModel = serverModel;
    }

    @Override
    public Node create() throws IOException {
        Node n = super.create();
        EasyApi.getMembersOfServer(serverModel.serverId, model -> {
            models.clear();
            models.addAll(model.get());
            load();
        });

        ((Label) Utils.findViewById(n, "noUserLabel"))
                .setText("No results :(");
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
        return "Search for members";
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
        return 3;
    }

    @Override
    public ServerModel getServer() {
        return serverModel;
    }

    @Override
    public boolean isEditablePage() {
        return false;
    }
}
