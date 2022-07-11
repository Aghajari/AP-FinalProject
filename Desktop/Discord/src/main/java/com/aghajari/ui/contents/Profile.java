package com.aghajari.ui.contents;

import com.aghajari.models.MyInfo;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.ui.HomeController;
import com.aghajari.ui.StaticListeners;
import com.aghajari.ui.dialogs.*;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconNode;

public class Profile extends AbstractContentCreator<Pane> {

    public static final String KEY = "profile_page";

    private final boolean editable;
    private final UserModel userModel;

    public Profile(UserModel userModel) {
        this.userModel = userModel;
        this.editable = userModel == MyInfo.getInstance();
    }

    @Override
    String getFXMLName() {
        return "content_profile" + (editable ? "" : "2");
    }

    @Override
    void create(Pane node) {
        if (editable)
            StaticListeners.updateMyProfile = this::load;

        node.setId(KEY);
        node.setUserData(this);

        load();
        if (editable) {
            ((MFXButton) Utils.findViewById(node, "edit_username"))
                    .setOnAction(actionEvent -> new ChangeUsernameDialog().create());

            Node editLabel = Utils.findViewById(node, "avatar_edit");
            editLabel.setDisable(true);
            EventHandler<MouseEvent> handler = mouseEvent ->
                    editLabel.setVisible(mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED);
            ImageView avatar = Utils.findViewById(node, "avatar");
            avatar.setOnMouseEntered(handler);
            avatar.setOnMouseExited(handler);
            avatar.setOnMouseClicked(mouseEvent ->
                    new ChangeProfileDialog().create());

            Utils.findViewById(node, "changePassword")
                    .setOnMouseClicked(mouseEvent ->
                            new ChangePasswordDialog().create());

            Utils.findViewById(node, "logOut")
                    .setOnMouseClicked(mouseEvent -> new LogOutDialog().create());
        } else {
            Label lbl = Utils.findViewById(node, "close");
            lbl.setAlignment(Pos.CENTER);
            IconNode iconNode = new IconNode(GoogleMaterialDesignIcons.CLOSE);
            iconNode.setStyle("-fx-fill: #5ed3f7ff");
            iconNode.setIconSize(24);
            Utils.loadFill(userModel, iconNode, false);
            lbl.setGraphic(iconNode);

            Node r = Utils.findViewById(node, "close_ripple");
            r.setClip(new Circle(20, 20, 20));
            r.setOnMouseClicked(event -> {
                HomeController.requestToOpenChat = false;
                EasyApi.closeChat(userModel);
            });

            lbl = Utils.findViewById(node, "block");
            lbl.setAlignment(Pos.CENTER);
            iconNode = new IconNode(GoogleMaterialDesignIcons.BLOCK);
            iconNode.setStyle("-fx-fill: #5ed3f7ff");
            iconNode.setIconSize(24);
            Utils.loadFill(userModel, iconNode, false);
            lbl.setGraphic(iconNode);

            r = Utils.findViewById(node, "block_ripple");
            r.setClip(new Circle(20, 20, 20));
            r.setOnMouseClicked(event -> new BlockDialog(userModel).create());
        }
    }

    public void load() {
        Node node = tmp;

        ImageView avatar = Utils.findViewById(node, "avatar");
        avatar.setClip(new Circle(40, 40, 40));
        Utils.loadAvatar(userModel, avatar);
        Utils.loadBg(userModel, Utils.findViewById(node, "bg"), true);

        Label lbl = Utils.findViewById(node, "name");
        lbl.setText(userModel.getName());

        ((Label) Utils.findViewById(node, "full_name"))
                .setText(userModel.nickname);

        ((Label) Utils.findViewById(node, "username"))
                .setText(MyInfo.getUsername(userModel, "You haven't set a username yet."));

        ((Label) Utils.findViewById(node, "email"))
                .setText(userModel.email);

        Node online = Utils.findViewById(node, "online");
        EasyApi.isOnline(userModel, m -> online.setVisible(m.get()));
    }

    @Override
    public StaticListeners.ResponseUpdater<UserModel, Boolean> getProfileContentUpdater() {
        return model -> {
            if (model.getId().equals(userModel.getId())) {
                HomeController.keepOldIndex = true;
                HomeController.forceUpdate = true;
                if (StaticListeners.updateOpenedChatsListener != null)
                    StaticListeners.updateOpenedChatsListener.update();

                return true;
            }
            return false;
        };
    }
}
