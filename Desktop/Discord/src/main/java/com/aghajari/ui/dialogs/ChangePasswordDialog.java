package com.aghajari.ui.dialogs;

import com.aghajari.api.ApiService;
import com.aghajari.api.SocketApi;
import com.aghajari.models.BaseApiModel;
import com.aghajari.models.ChangePasswordModel;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;
import com.aghajari.util.Toast;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class ChangePasswordDialog extends AbstractDialog {

    @Override
    public String getLayoutFXML() {
        return "change_password_dialog";
    }

    @Override
    public void load(AnchorPane base, Node dialog) {
        MFXTextField pass = Utils.findViewById(dialog, "pass");
        MFXTextField npass = Utils.findViewById(dialog, "npass");
        MFXTextField cnpass = Utils.findViewById(dialog, "cnpass");

        Node progress = Utils.findViewById(dialog, "progress");

        ((MFXButton) Utils.findViewById(dialog, "btn")).setOnAction(actionEvent -> {
            if (pass.getText().trim().isEmpty()) {
                Toast.makeText("Enter current password!");
                return;
            }
            if (npass.getText().trim().isEmpty()) {
                Toast.makeText("Enter your new password!");
                return;
            }
            if (!cnpass.getText().trim().equals(npass.getText().trim())) {
                Toast.makeText("Confirm your new password!");
                return;
            }
            if (pass.getText().trim().equals(npass.getText().trim())) {
                Toast.makeText("Current Password and New Password are same!");
                return;
            }
            progress.setVisible(true);
            ApiService.changePass(pass.getText().trim(), npass.getText().trim(), new ApiService.Callback() {
                @Override
                public void onResponse(String body) {
                    ChangePasswordModel model = ChangePasswordModel.parse(body);
                    if (!model.success) {
                        onError2(false, 200, body);
                        return;
                    }
                    SocketApi.getInstance().write(new SocketModel(SocketEvents.AUTH,
                            ApiService.getToken()));

                    progress.setVisible(false);
                    System.out.println(body);
                    hide(base);
                }

                @Override
                public void onError(boolean network, int code) {
                    progress.setVisible(false);
                    Toast.makeText("Oops, something went wrong :(");
                }

                @Override
                public void onError2(boolean network, int code, String res) {
                    progress.setVisible(false);
                    BaseApiModel.toastError(res);
                }
            });
        });
    }

}
