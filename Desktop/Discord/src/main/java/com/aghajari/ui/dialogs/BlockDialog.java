package com.aghajari.ui.dialogs;

import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class BlockDialog extends AbstractDialog {

    final UserModel userModel;

    public BlockDialog(UserModel userModel) {
        this.userModel = userModel;
    }

    @Override
    public String getLayoutFXML() {
        return "logout_dialog";
    }

    @Override
    public void load(AnchorPane base, Node dialog) {
        Label lbl = Utils.findViewById(dialog, "text");
        lbl.setText("Are you sure you want to block " + userModel.getName());

        MFXButton btn = Utils.findViewById(dialog, "btn");
        btn.setText("Block");

        EasyApi.hasBlocked(userModel.getId(), model -> {
            boolean blocked = model.get();
            if (blocked) {
                lbl.setText("Are you sure you want to unblock " + userModel.getName());
                btn.setText("Unblock");
            }
            btn.setOnAction(actionEvent -> {
                EasyApi.block(userModel.getId(), !blocked);
                hide(base);
            });
        });
    }

}
