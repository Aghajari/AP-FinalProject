package com.aghajari.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.aghajari.adapters.MemberPermissionsAdapter;
import com.aghajari.adapters.PermissionsAdapter;
import com.aghajari.permissions;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.views.Utils;

public class PermissionsFragment extends BaseFragment<permissions> {

    public static PermissionsFragment newInstance(ServerModel profile) {
        return newInstance(profile, null);
    }

    public static PermissionsFragment newInstance(ServerModel profile, UserModel userModel) {
        PermissionsFragment fragment = new PermissionsFragment();
        Bundle args = new Bundle();
        args.putSerializable("profile", profile);
        if (userModel != null)
            args.putSerializable("user", userModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public permissions create(Context context) {
        return new permissions(context);
    }

    @Override
    public void bind() {
        ((ViewGroup.MarginLayoutParams) view.header.getLayoutParams())
                .topMargin = Utils.getStatusBarHeight();

        view.back.setOnClickListener(v -> back());

        Utils.addSpace(view.rv, 12, 80);
        view.rv.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() == null)
            return;

        ServerModel serverModel = (ServerModel) getArguments().getSerializable("profile");
        if (getArguments().containsKey("user"))
            view.rv.setAdapter(new MemberPermissionsAdapter(serverModel,
                    (UserModel) getArguments().getSerializable("user"), this));
        else
            view.rv.setAdapter(new PermissionsAdapter(serverModel, this));
    }

}
