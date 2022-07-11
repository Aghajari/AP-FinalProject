package com.aghajari.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import com.aghajari.R;
import com.aghajari.fragments.BaseFragment;
import com.aghajari.logout_dialog;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.store.EasyApi;
import com.aghajari.views.PermissionUtils;
import com.aghajari.views.Utils;

public class DeleteOrLeaveDialog extends Dialog {

    private final BaseFragment<?> base;
    private final logout_dialog view;
    private final ServerModel serverModel;

    public DeleteOrLeaveDialog(ServerModel serverModel, BaseFragment<?> base) {
        super(base.getContext(), R.style.AghajariDialog);
        this.base = base;
        this.serverModel = serverModel;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view = new logout_dialog(base.getContext()));

        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(Utils.perX(86), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PermissionUtils.isOwner(serverModel)) {
            view.title.setText("Delete Server");
            view.subtitle.setText("Are you sure you want to delete this server?");
            view.accept.setText("Delete");
        } else {
            view.title.setText("Leave Server");
            view.subtitle.setText("Are you sure you want to leave this server?");
            view.accept.setText("Leave");
        }

        view.cancel.setOnClickListener(v -> cancel());
        view.accept.setOnClickListener(v -> {
            if (base.getContext() == null || !isShowing())
                return;

            cancel();
            if (PermissionUtils.isOwner(serverModel))
                EasyApi.deleteServer(serverModel.serverId);
            else
                EasyApi.leaveServer(serverModel.serverId);
            base.back();
        });
    }

}
