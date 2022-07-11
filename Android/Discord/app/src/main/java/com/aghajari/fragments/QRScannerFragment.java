package com.aghajari.fragments;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aghajari.Application;
import com.aghajari.qrscanner_fragment;
import com.aghajari.shared.models.DeviceInfo;
import com.aghajari.store.EasyApi;
import com.aghajari.views.Utils;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.BarcodeFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class QRScannerFragment extends BaseFragment<qrscanner_fragment> {

    private CodeScanner scanner;
    private final List<String> scanned = new ArrayList<>();
    private boolean isWaiting = false;

    @Override
    public qrscanner_fragment create(Context context) {
        qrscanner_fragment v = new qrscanner_fragment(context);
        v.scanner.setAutoFocusButtonVisible(false);
        v.scanner.setFlashButtonVisible(false);
        v.scanner.setFrameCornersSize(Utils.dp(24));
        v.scanner.setFrameCornersRadius(Utils.dp(12));
        v.scanner.setFrameCornersCapRounded(true);
        v.scanner.setFrameThickness(Utils.dp(4));

        scanner = new CodeScanner(context, v.scanner);
        v.scanner.setOnClickListener(v0 -> scanner.startPreview());

        scanner.setScanMode(ScanMode.CONTINUOUS);
        scanner.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));

        scanner.setDecodeCallback(result -> {
            if (isWaiting)
                return;

            String text = result.getText();
            if (scanned.contains(text))
                return;
            isWaiting = true;
            scanned.add(text);

            EasyApi.authDeviceByQRCode(text, model -> {
                DeviceInfo info = model.get();
                if (info != null) {
                    Toast.makeText(Application.context, info.name + " Logged in!",
                            Toast.LENGTH_SHORT).show();
                    back();
                } else {
                    isWaiting = false;
                }
            });
        });
        return v;
    }

    @Override
    public void bind() {
        ((ViewGroup.MarginLayoutParams) view.header.getLayoutParams())
                .topMargin = Utils.getStatusBarHeight();

        view.back.setOnClickListener(v -> back());
    }


    @Override
    public void onResume() {
        super.onResume();
        scanner.startPreview();
    }

    @Override
    public void onPause() {
        scanner.releaseResources();
        super.onPause();
    }

}
