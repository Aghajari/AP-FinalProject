package com.aghajari.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aghajari.edit_username;
import com.aghajari.api.ApiService;
import com.aghajari.models.BaseApiModel;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.Profile;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.store.EasyApi;
import com.aghajari.views.Utils;

import java.io.Serializable;


public class EditUsernameFragment extends BaseFragment<edit_username> {

    public static EditUsernameFragment newInstance(Profile profile) {
        EditUsernameFragment fragment = new EditUsernameFragment();
        Bundle args = new Bundle();
        args.putSerializable("profile", (Serializable) profile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public edit_username create(Context context) {
        return new edit_username(context);
    }

    @Override
    public void bind() {
        if (getArguments() == null)
            return;

        Profile profile = (Profile) getArguments().getSerializable("profile");


        ((ViewGroup.MarginLayoutParams) view.header.getLayoutParams())
                .topMargin = Utils.getStatusBarHeight();

        String def = profile == MyInfo.getInstance() ?
                MyInfo.getUsername("") : profile.getName();
        view.username_edt.setText(def);
        Utils.showKeyboard(view.username_edt);

        view.hint.setVisibility(profile == MyInfo.getInstance() ? View.VISIBLE : View.GONE);
        view.title.setText(profile == MyInfo.getInstance() ? "Username" : "Server Name");

        view.back.setOnClickListener(v -> back());

        view.save.setOnClickListener(v -> {
            String text = Utils.textOf(view.username_edt);
            if (def.equals(text)) {
                back();
                return;
            }
            if (profile == MyInfo.getInstance() && !Utils.isValidUsername(text)) {
                Toast.makeText(v.getContext(), "Username isn't valid!", Toast.LENGTH_SHORT).show();
                return;
            } else if (text.isEmpty()) {
                Toast.makeText(v.getContext(), "Enter a name!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (profile instanceof ServerModel) {
                ServerModel server = (ServerModel) profile;
                server.name = text;
                EasyApi.updateServerName(server);
                back();
                return;
            }

            view.progress.setLoading(true);

            ApiService.changeUsername(text, new ApiService.Callback() {
                @Override
                public void onResponse(String body) {
                    MyInfo.getInstance().username = text;
                    ApiService.saveUser(MyInfo.getInstance());
                    EasyApi.updateProfile();
                    if (getContext() == null)
                        return;

                    view.progress.setLoading(false);
                    System.out.println(body);
                    back();
                }

                @Override
                public void onError(boolean network, int code) {
                    if (getContext() == null)
                        return;
                    view.progress.setLoading(false);
                    Toast.makeText(v.getContext(), "Oops, something went wrong :(", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError2(boolean network, int code, String res) {
                    if (getContext() == null)
                        return;
                    view.progress.setLoading(false);
                    BaseApiModel.toastError(res);
                }
            });
        });
    }

}
