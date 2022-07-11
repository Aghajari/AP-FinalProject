package com.aghajari.ui.contents;

import com.aghajari.Main;
import com.aghajari.shared.models.UserModel;
import com.aghajari.ui.ContentCreator;
import com.aghajari.ui.HomeController;
import com.aghajari.ui.StaticListeners;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

public abstract class AbstractContentCreator<T extends Node> implements ContentCreator {

    T tmp = null;

    @Override
    public Node create() throws IOException {
        HomeController.isOnChatPage = isChatPage();
        HomeController.isOnEditPage = isEditablePage();
        StaticListeners.updateProfileContent = getProfileContentUpdater();

        if (tmp != null)
            return tmp;

        tmp = FXMLLoader.load(Main.class.getResource("fxml/" + getFXMLName() + ".fxml"));
        create(tmp);
        return tmp;
    }

    public StaticListeners.ResponseUpdater<UserModel, Boolean> getProfileContentUpdater(){
        return null;
    }

    public boolean isEditablePage(){
        return false;
    }

    public boolean isChatPage() {
        return false;
    }

    abstract String getFXMLName();

    abstract void create(T node);
}
