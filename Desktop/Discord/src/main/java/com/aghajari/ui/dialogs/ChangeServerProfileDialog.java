package com.aghajari.ui.dialogs;

import com.aghajari.api.ApiService;
import com.aghajari.models.BaseApiModel;
import com.aghajari.models.MyInfo;
import com.aghajari.models.UploadAvatarModel;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.store.EasyApi;
import com.aghajari.ui.HomeController;
import com.aghajari.ui.StaticListeners;
import com.aghajari.util.ImagePicker;
import com.aghajari.util.Toast;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

public class ChangeServerProfileDialog extends AbstractDialog {

    final ServerModel server;

    public ChangeServerProfileDialog(ServerModel server) {
        this.server = server;
    }

    @Override
    public String getLayoutFXML() {
        return "change_profile_dialog";
    }

    @Override
    public void load(AnchorPane base, Node dialog) {
        ImageView img = Utils.findViewById(dialog, "img");
        img.setClip(new Circle(50, 50, 50));
        Utils.loadAvatar(server, img);

        Node progress = Utils.findViewById(dialog, "progress");

        ImagePicker picker = new ImagePicker(img, dialog);

        ((MFXButton) Utils.findViewById(dialog, "btn")).setOnAction(actionEvent -> {
            if (picker.getFile() != null) {
                progress.setVisible(true);
                ApiService.uploadImage(picker.getFile(), new ApiService.Callback() {
                    @Override
                    public void onResponse(String body) {
                        System.out.println(body);
                        UploadAvatarModel model = UploadAvatarModel.parseOnlyUrl(body);
                        if (model == null) {
                            onError(false, 0);
                            return;
                        }
                        server.setImage(null);
                        server.setColor(null);
                        server.avatar = model.url;
                        EasyApi.updateServerAvatar(server);

                        HomeController.forceUpdate = true;
                        HomeController.keepOldIndex = true;
                        if (StaticListeners.updateOpenedChatsListener != null)
                            StaticListeners.updateOpenedChatsListener.update();

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
            } else {
                hide(base);
            }
        });
    }

}
