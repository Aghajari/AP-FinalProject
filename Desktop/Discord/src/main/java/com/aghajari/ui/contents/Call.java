package com.aghajari.ui.contents;

import com.aghajari.call.CallUI;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.ui.StaticListeners;
import com.aghajari.ui.dialogs.ChangePasswordDialog;
import com.aghajari.ui.dialogs.ChangeProfileDialog;
import com.aghajari.ui.dialogs.ChangeUsernameDialog;
import com.aghajari.ui.dialogs.LogOutDialog;
import com.aghajari.util.Toast;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconNode;


public class Call extends AbstractContentCreator<Pane> {

    public static final String KEY = "call_page";
    private boolean isOnline = false;

    private final UserModel userModel;

    public Call(UserModel userModel) {
        this.userModel = userModel;
    }

    @Override
    String getFXMLName() {
        return "content_call";
    }

    @Override
    void create(Pane node) {
        node.setId(KEY);
        node.setUserData(this);

        Label lbl = Utils.findViewById(node, "btn");
        IconNode iconNode = new IconNode(GoogleMaterialDesignIcons.CALL);
        iconNode.setFill(Color.WHITE);
        iconNode.setIconSize(30);
        lbl.setGraphic(iconNode);

        Node r = Utils.findViewById(node, "ripple");
        r.setClip(new Circle(30, 30, 30));
        load();

        r.setOnMouseClicked(mouseEvent -> {
            if (CallUI.isInCall()) {
                Toast.makeText("You are already in a call!");
                return;
            }

            if (!isOnline) {
                Toast.makeText(MyInfo.getUsername(userModel, "")
                        + " isn't online!");
                //return;
            }
            CallUI.start(userModel, true);
        });
    }

    public void load() {
        Label lbl = Utils.findViewById(tmp, "btn");
        lbl.setStyle("-fx-background-color: " + (isOnline ? "#339966" : "#2F3136"));

        EasyApi.isOnline(userModel, m -> {
            isOnline = m.get();
            lbl.setStyle("-fx-background-color: " + (isOnline ? "#339966" : "#2F3136"));
        });
    }

}
