package com.aghajari.ui.contents;

import com.aghajari.shared.IDFinder;
import com.aghajari.shared.models.OpenedChatModel;
import com.aghajari.store.EasyApi;
import com.aghajari.util.Utils;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockList extends MyFriends {

    ArrayList<OpenedChatModel> models = new ArrayList<>();

    public BlockList() {
    }

    @Override
    public Node create() throws IOException {
        Node n = super.create();
        EasyApi.getBlockList(model -> {
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
        return "Search";
    }

    @Override
    public String getImage() {
        return "block.png";
    }

    @Override
    public String getEmptyText() {
        return "You haven't blocked anyone.";
    }

    @Override
    public int getType() {
        return 3;
    }


    @Override
    public boolean isEditablePage() {
        return true;
    }
}
