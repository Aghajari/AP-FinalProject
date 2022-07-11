package com.aghajari.ui.contents;

import com.aghajari.api.SocketApi;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.SocketModel;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.ui.HomeController;
import com.aghajari.ui.StaticListeners;
import com.aghajari.ui.dialogs.*;
import com.aghajari.util.PermissionUtils;
import com.aghajari.util.Toast;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconNode;

import java.util.Map;

public class ServerProfile extends AbstractContentCreator<Pane> {

    public static final String KEY = "server_profile_page";

    private final boolean editable;
    private final ServerModel serverModel;

    public ServerProfile(ServerModel serverModel) {
        this.serverModel = serverModel;
        this.editable = serverModel.owner.equals(MyInfo.getInstance().getId());
    }

    @Override
    String getFXMLName() {
        return "content_server_profile";
    }

    @Override
    void create(Pane node) {
        node.setId(KEY);
        node.setUserData(this);

        load();

        if (editable || PermissionUtils.canChangeServerName(serverModel)) {
            ((MFXButton) Utils.findViewById(node, "edit"))
                    .setOnAction(actionEvent -> new ChangeServerNameDialog(serverModel).create());
        } else {
            Utils.findViewById(node, "edit").setVisible(false);
        }

        if (editable || PermissionUtils.canChangeServerProfile(serverModel)) {
            Node editLabel = Utils.findViewById(node, "avatar_edit");
            editLabel.setDisable(true);
            EventHandler<MouseEvent> handler = mouseEvent ->
                    editLabel.setVisible(mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED);
            ImageView avatar = Utils.findViewById(node, "avatar");
            avatar.setOnMouseEntered(handler);
            avatar.setOnMouseExited(handler);
            avatar.setOnMouseClicked(mouseEvent ->
                    new ChangeServerProfileDialog(serverModel).create());
        }

        if (editable || PermissionUtils.canRevokeInviteCode(serverModel)) {
            Utils.findViewById(node, "revoke")
                    .setOnMouseClicked(mouseEvent ->
                            EasyApi.revokeServerInviteCode(serverModel.serverId, model -> {
                                String code = model.get();
                                if (code == null || code.isEmpty())
                                    return;

                                serverModel.inviteCode = model.get();
                                ((Label) Utils.findViewById(node, "code"))
                                        .setText(serverModel.inviteCode);
                            }));
        } else {
            Utils.findViewById(node, "revoke").setVisible(false);
        }

        if (editable || PermissionUtils.canSeeInviteCode(serverModel)) {
            Utils.findViewById(node, "copy")
                    .setOnMouseClicked(mouseEvent -> {
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        if (clipboard.setContent(Map.of(DataFormat.PLAIN_TEXT,
                                serverModel.inviteCode)))
                            Toast.makeText("Copied!");
                    });
        } else {
            Utils.findViewById(node, "copy").setVisible(false);
        }

        Utils.findViewById(node, "owner_img").setClip(new Circle(10, 10, 10));

        Label lbl = Utils.findViewById(node, "close");
        lbl.setAlignment(Pos.CENTER);
        IconNode iconNode = new IconNode(serverModel.owner.equals(MyInfo.getInstance().getId()) ?
                GoogleMaterialDesignIcons.DELETE : GoogleMaterialDesignIcons.CLOSE);
        iconNode.setStyle("-fx-fill: #5ed3f7ff");
        iconNode.setIconSize(24);
        Utils.loadFill(serverModel, iconNode, false);
        lbl.setGraphic(iconNode);

        Node r = Utils.findViewById(node, "close_ripple");
        r.setClip(new Circle(20, 20, 20));
        r.setOnMouseClicked(event -> {
            new ServerExitDialog(serverModel,
                    serverModel.owner.equals(MyInfo.getInstance().getId())).create();
        });

        if (editable || PermissionUtils.canChangePermissions(serverModel)) {
            Utils.findViewById(node, "permissions").setVisible(true);
            checkPermissionFor(Utils.findViewById(node, "p1"), PermissionUtils.SEND_MESSAGE);
            checkPermissionFor(Utils.findViewById(node, "p2"), PermissionUtils.SEE_INVITE_CODE);
            checkPermissionFor(Utils.findViewById(node, "p3"), PermissionUtils.CHANGE_SERVER_PROFILE);
            checkPermissionFor(Utils.findViewById(node, "p4"), PermissionUtils.CHANGE_SERVER_NAME);
        }
    }

    private void checkPermissionFor(MFXToggleButton node, int key) {
        node.setSelected(PermissionUtils.hasPermission(serverModel.permissions, key));
        node.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (node.isSelected()) {
                serverModel.permissions |= key;
            } else {
                serverModel.permissions ^= key;
            }
            EasyApi.updateServerPermissions(serverModel);
        });
    }

    public void load() {
        Node node = tmp;

        ImageView avatar = Utils.findViewById(node, "avatar");
        avatar.setClip(new Circle(40, 40, 40));
        Utils.loadAvatar(serverModel, avatar);
        Utils.loadBg(serverModel, Utils.findViewById(node, "bg"), true);

        ((Label) Utils.findViewById(node, "code"))
                .setText(editable || PermissionUtils.canSeeInviteCode(serverModel) ?
                        serverModel.inviteCode : "No access!");

        Label lbl = Utils.findViewById(node, "name");
        lbl.setText(serverModel.getName());

        ((Label) Utils.findViewById(node, "server_name"))
                .setText(serverModel.getName());

        EasyApi.getUserInfo(serverModel.owner, model -> {
            UserModel ownerModel = model.get();
            Utils.loadAvatar(ownerModel, Utils.findViewById(node, "owner_img"));
            ((Label) Utils.findViewById(node, "owner"))
                    .setText(ownerModel.getName());
        });

    }

}
