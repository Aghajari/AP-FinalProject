package com.aghajari.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aghajari.adapters.DevicesAdapter;
import com.aghajari.devices_fragment;
import com.aghajari.views.Utils;


public class DevicesFragment extends BaseFragment<devices_fragment> {

    Utils.Consumer<Boolean> permissionListener;

    ActivityResultLauncher<String> permission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (permissionListener != null)
                    permissionListener.accept(result);
            });

    @Override
    public devices_fragment create(Context context) {
        return new devices_fragment(context);
    }

    @Override
    public void bind() {
        ((ViewGroup.MarginLayoutParams) view.header.getLayoutParams())
                .topMargin = Utils.getStatusBarHeight();

        view.back.setOnClickListener(v -> back());
        view.rv.setLayoutManager(new LinearLayoutManager(getContext()));

        view.rv.setAdapter(new DevicesAdapter(view.rv, this));
        Utils.addSpace(view.rv, 0, 80);
    }


    public void requestCameraPermission(Utils.Consumer<Boolean> listener) {
        if (getContext() == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                listener.accept(true);
            } else {
                permissionListener = listener;
                permission.launch(Manifest.permission.CAMERA);
            }
        } else {
            listener.accept(true);
        }
    }

}
