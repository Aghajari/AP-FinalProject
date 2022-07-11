package com.aghajari.ui.dialogs;

import com.aghajari.models.MyInfo;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.store.EasyApi;
import com.aghajari.ui.HomeController;
import com.aghajari.ui.StaticListeners;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class ChangeServerNameDialog extends AbstractDialog {

    final ServerModel server;

    public ChangeServerNameDialog(ServerModel server) {
        this.server = server;
    }

    @Override
    public String getLayoutFXML() {
        return "change_server_name_dialog";
    }

    @Override
    public void load(AnchorPane base, Node dialog) {
        MFXTextField username = Utils.findViewById(dialog, "name");
        username.setText(server.name);

        ((MFXButton) Utils.findViewById(dialog, "btn")).setOnAction(actionEvent -> {
            String text = username.getText().trim();
            if (text.trim().isEmpty() ||
                    server.getName().equals(text.trim())) {
                hide(base);
                return;
            }

            server.name = text;
            EasyApi.updateServerName(server);

            HomeController.forceUpdate = true;
            HomeController.keepOldIndex = true;
            if (StaticListeners.updateOpenedChatsListener != null)
                StaticListeners.updateOpenedChatsListener.update();

            hide(base);
        });
    }

}
