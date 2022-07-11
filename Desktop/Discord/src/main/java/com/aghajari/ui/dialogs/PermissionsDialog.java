package com.aghajari.ui.dialogs;

import com.aghajari.models.MyInfo;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.util.PermissionUtils;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class PermissionsDialog extends AbstractDialog {

    private final ServerModel main;
    private final ServerModel server;
    private final UserModel userModel;

    public PermissionsDialog(ServerModel server, UserModel userModel) {
        this.main = server;
        this.server = new ServerModel(server.serverId);
        this.server.permissions = server.permissions;
        this.userModel = userModel;
    }

    @Override
    public String getLayoutFXML() {
        return "permissions_dialog";
    }

    @Override
    public void load(AnchorPane base, Node dialog) {
        EasyApi.getMemberPermission(server.serverId, userModel.getId(), model -> {
            server.permissions2 = model.get();
            checkPermissionFor(Utils.findViewById(dialog, "p1"), PermissionUtils.SEND_MESSAGE, PermissionUtils.NOT_SEND_MESSAGE);
            checkPermissionFor(Utils.findViewById(dialog, "p2"), PermissionUtils.SEE_INVITE_CODE, PermissionUtils.NOT_SEE_INVITE_CODE);
            checkPermissionFor(Utils.findViewById(dialog, "p3"), PermissionUtils.CHANGE_SERVER_PROFILE, PermissionUtils.NOT_CHANGE_SERVER_PROFILE);
            checkPermissionFor(Utils.findViewById(dialog, "p4"), PermissionUtils.CHANGE_SERVER_NAME, PermissionUtils.NOT_CHANGE_SERVER_NAME);
            checkPermissionFor(Utils.findViewById(dialog, "p5"), PermissionUtils.SEND_MESSAGE_TO_CHANNELS, 0);
            checkPermissionFor(Utils.findViewById(dialog, "p6"), PermissionUtils.REVOKE_INVITE_CODE, 0);
            checkPermissionFor(Utils.findViewById(dialog, "p7"), PermissionUtils.CHANGE_PERMISSIONS, 0);
            checkPermissionFor(Utils.findViewById(dialog, "p8"), PermissionUtils.REMOVE_MEMBERS, 0);
        });
    }

    private void checkPermissionFor(MFXToggleButton node, int key, int notKey) {
        node.setSelected(PermissionUtils.hasPermission(server, key, notKey));

        if (notKey == 0) {
            if (!main.owner.equals(MyInfo.getInstance().getId())
                    && !PermissionUtils.hasPermission(main.permissions2, key))
                node.setDisable(true);
        } else {
            if (!PermissionUtils.hasPermission(main.permissions, key)
                    || (!main.owner.equals(MyInfo.getInstance().getId())
                    && PermissionUtils.hasPermission(main.permissions2, notKey)))
                node.setDisable(true);
        }

        node.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (node.isSelected()) {
                server.permissions2 |= key;
                if (notKey != 0)
                    server.permissions2 ^= notKey;
            } else {
                server.permissions2 ^= key;
                if (notKey != 0)
                    server.permissions2 |= notKey;
            }
            EasyApi.updateMemberPermission(server.serverId, userModel.getId(), server.permissions2);
        });
    }
}
