package com.aghajari.ui.dialogs;

import com.aghajari.Main;
import com.aghajari.MainUI;
import com.aghajari.api.ApiService;
import com.aghajari.util.Toast;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LogOutDialog extends AbstractDialog {

    @Override
    public String getLayoutFXML() {
        return "logout_dialog";
    }

    @Override
    public void load(AnchorPane base, Node dialog) {
        ((MFXButton) Utils.findViewById(dialog, "btn")).setOnAction(actionEvent -> {
            ApiService.logOut();
            Toast.ownerStage.close();
            try {
                new MainUI().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
