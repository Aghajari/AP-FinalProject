package com.aghajari.fragments;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aghajari.api.ApiService;
import com.aghajari.api.SocketApi;
import com.aghajari.change_password;
import com.aghajari.models.BaseApiModel;
import com.aghajari.models.ChangePasswordModel;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;
import com.aghajari.views.Utils;

public class ChangePasswordFragment extends BaseFragment<change_password> {

    @Override
    public change_password create(Context context) {
        return new change_password(context);
    }

    @Override
    public void bind() {
        ((ViewGroup.MarginLayoutParams) view.header.getLayoutParams())
                .topMargin = Utils.getStatusBarHeight();

        Utils.showKeyboard(view.current_pass);
        view.back.setOnClickListener(v -> back());

        view.save.setOnClickListener(v -> {
            String pass = Utils.textOf(view.current_pass);
            String pass2 = Utils.textOf(view.new_pass);
            String pass3 = Utils.textOf(view.confirm_pass);

            if (pass.isEmpty()) {
                Toast.makeText(v.getContext(), "Enter current password!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass2.isEmpty()) {
                Toast.makeText(v.getContext(), "Enter your new password!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass3.equals(pass2)) {
                Toast.makeText(v.getContext(), "Confirm your new password!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.equals(pass3)) {
                Toast.makeText(v.getContext(), "Current Password and New Password are same!", Toast.LENGTH_SHORT).show();
                return;
            }
            view.progress.setLoading(true);

            ApiService.changePass(pass, pass2, new ApiService.Callback() {
                @Override
                public void onResponse(String body) {
                    ChangePasswordModel model = ChangePasswordModel.parse(body);
                    if (model == null || !model.success) {
                        onError2(false, 200, body);
                        return;
                    }
                    SocketApi.getInstance().write(new SocketModel(SocketEvents.AUTH,
                            ApiService.getToken()));

                    if (getContext() == null)
                        return;

                    view.progress.setLoading(false);
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
