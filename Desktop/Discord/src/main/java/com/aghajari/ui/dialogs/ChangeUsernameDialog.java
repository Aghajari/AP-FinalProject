package com.aghajari.ui.dialogs;

import com.aghajari.api.ApiService;
import com.aghajari.models.BaseApiModel;
import com.aghajari.models.MyInfo;
import com.aghajari.store.EasyApi;
import com.aghajari.ui.StaticListeners;
import com.aghajari.util.Toast;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class ChangeUsernameDialog extends AbstractDialog {

    @Override
    public String getLayoutFXML() {
        return "change_username_dialog";
    }

    @Override
    public void load(AnchorPane base, Node dialog) {
        MFXTextField username = Utils.findViewById(dialog, "username");
        username.setText(MyInfo.getUsername(""));

        Node progress = Utils.findViewById(dialog, "progress");

        ((MFXButton) Utils.findViewById(dialog, "btn")).setOnAction(actionEvent -> {
            String text = username.getText().trim();
            if (MyInfo.getUsername("").equals(text)) {
                hide(base);
                return;
            }
            if (!Utils.isValidUsername(text)) {
                Toast.makeText("Username isn't valid!");
                return;
            }
            progress.setVisible(true);
            ApiService.changeUsername(text, new ApiService.Callback() {
                @Override
                public void onResponse(String body) {
                    MyInfo.getInstance().username = text;
                    ApiService.saveUser(MyInfo.getInstance());
                    EasyApi.updateProfile();
                    if (StaticListeners.updateMyProfile != null)
                        StaticListeners.updateMyProfile.update();
                    if (StaticListeners.updateOpenedChatsListener != null)
                        StaticListeners.updateOpenedChatsListener.update();

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
