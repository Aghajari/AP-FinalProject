package com.aghajari.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;

import com.aghajari.MainActivity;
import com.aghajari.R;
import com.aghajari.api.ApiService;
import com.aghajari.logout_dialog;
import com.aghajari.views.Utils;

public class LogoutDialog extends Dialog {

    private final Activity activity;
    private final logout_dialog view;

    public LogoutDialog(@NonNull Activity context) {
        super(context, R.style.AghajariDialog);
        this.activity = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view = new logout_dialog(context));

        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(Utils.perX(86), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        view.cancel.setOnClickListener(v -> cancel());
        view.accept.setOnClickListener(v -> {
            if (activity == null || !isShowing())
                return;

            cancel();
            ApiService.logOut();
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        });
    }

}
