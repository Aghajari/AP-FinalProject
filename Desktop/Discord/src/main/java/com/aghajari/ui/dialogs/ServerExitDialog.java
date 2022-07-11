package com.aghajari.ui.dialogs;

import com.aghajari.api.SocketApi;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.store.EasyApi;
import com.aghajari.store.StaticStorage;
import com.aghajari.ui.StaticListeners;
import com.aghajari.util.Toast;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ServerExitDialog extends AbstractDialog {

    final boolean delete;
    final ServerModel server;

    public ServerExitDialog(ServerModel server, boolean delete) {
        this.delete = delete;
        this.server = server;
    }

    @Override
    public String getLayoutFXML() {
        return "logout_dialog";
    }

    @Override
    public void load(AnchorPane base, Node dialog) {
        Label lbl = Utils.findViewById(dialog, "text");
        lbl.setText("Are you sure you want to " + (delete ? "delete" : "leave") + " this server?");

        MFXButton btn = Utils.findViewById(dialog, "btn");
        btn.setText(delete ? "Delete Server" : "Leave Server");

        SocketApi.Listener listener = model -> {
            if (model.get()) {
                StaticStorage.chats.servers.remove(server);
                if (StaticListeners.updateOpenedChatsListener != null)
                    StaticListeners.updateOpenedChatsListener.update();
                hide(base);
            } else
                Toast.makeText("Oops, Something went wrong!");
        };

        btn.setOnAction(actionEvent -> {
            if (delete)
                EasyApi.deleteServer(server.serverId, listener);
            else
                EasyApi.leaveServer(server.serverId, listener);
        });
    }

}
