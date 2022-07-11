package com.aghajari.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import com.aghajari.R;
import com.aghajari.adapters.DevicesAdapter;
import com.aghajari.logout_dialog;
import com.aghajari.shared.models.DeviceInfo;
import com.aghajari.store.EasyApi;
import com.aghajari.views.Utils;

public class TerminateDeviceDialog extends Dialog {

    private final logout_dialog view;
    private final int index;
    private final DeviceInfo info;
    private final DevicesAdapter adapter;

    public TerminateDeviceDialog(Context context, int index, DevicesAdapter adapter, DeviceInfo info) {
        super(context, R.style.AghajariDialog);
        this.index = index;
        this.info = info;
        this.adapter = adapter;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view = new logout_dialog(context));

        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(Utils.perX(86), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view.title.setText("Terminate Device");
        view.subtitle.setText("Are you sure you want to terminate " + info.name + "?");
        view.accept.setText("Terminate");

        view.cancel.setOnClickListener(v -> cancel());
        view.accept.setOnClickListener(v -> {
            EasyApi.terminate(info);
            adapter.remove(index);
            cancel();
        });
    }

}
