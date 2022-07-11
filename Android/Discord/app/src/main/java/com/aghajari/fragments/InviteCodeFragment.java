package com.aghajari.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aghajari.invite_code;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.store.EasyApi;
import com.aghajari.views.PermissionUtils;
import com.aghajari.views.Utils;

public class InviteCodeFragment extends BaseFragment<invite_code> {

    public static InviteCodeFragment newInstance(ServerModel profile) {
        InviteCodeFragment fragment = new InviteCodeFragment();
        Bundle args = new Bundle();
        args.putSerializable("profile", profile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public invite_code create(Context context) {
        return new invite_code(context);
    }

    @Override
    public void bind() {
        if (getArguments() == null)
            return;

        ServerModel serverModel = (ServerModel) getArguments().getSerializable("profile");

        ((ViewGroup.MarginLayoutParams) view.header.getLayoutParams())
                .topMargin = Utils.getStatusBarHeight();

        view.back.setOnClickListener(v -> back());
        view.code.setText(serverModel.inviteCode);

        View.OnClickListener clickListener = v -> {
            if (getContext() == null)
                return;

            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(serverModel.name + " Invite Code",
                    serverModel.inviteCode);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Copied!", Toast.LENGTH_SHORT).show();
        };

        view.code.setOnClickListener(clickListener);
        view.copy.setOnClickListener(clickListener);

        view.revoke.setVisibility(PermissionUtils.canRevokeInviteCode(serverModel)
                ? View.VISIBLE : View.GONE);
        view.revoke.setOnClickListener(v ->
                EasyApi.revokeServerInviteCode(serverModel.serverId, model -> {
                    String code = model.get();
                    if (code == null || code.isEmpty())
                        return;

                    serverModel.inviteCode = model.get();
                    view.code.setText(serverModel.inviteCode);
                })
        );
    }

}
