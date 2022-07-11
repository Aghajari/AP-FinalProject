package com.aghajari.ui.dialogs;

import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.ui.HomeController;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

public class OpenChatDialog extends AbstractDialog {

    private final UserModel model;
    private final ObjectProperty<Image> image;

    public OpenChatDialog(UserModel model, ObjectProperty<Image> image) {
        this.model = model;
        this.image = image;
    }

    @Override
    public String getLayoutFXML() {
        return "open_chat_dialog";
    }

    @Override
    public void load(AnchorPane base, Node dialog) {
        Label lbl = Utils.findViewById(dialog, "name");
        Label lbl2 = Utils.findViewById(dialog, "full_name");
        lbl.setText(model.username);
        lbl2.setText(model.nickname);

        ImageView img = Utils.findViewById(dialog, "img");
        img.setClip(new Circle(40, 40, 40));
        img.imageProperty().bind(image);

        ((MFXButton) Utils.findViewById(dialog, "btn")).setOnAction(actionEvent -> {
            HomeController.requestToOpenChat = true;
            EasyApi.openChat(model, c -> {
                hide(base);
            });
        });
    }

}
