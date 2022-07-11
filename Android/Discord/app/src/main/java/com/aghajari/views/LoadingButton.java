package com.aghajari.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class LoadingButton extends FrameLayout {

    public CircularProgressIndicator indicator;

    public LoadingButton(@NonNull Context context) {
        this(context, null);
    }

    public LoadingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipToOutline(true);

        indicator = new CircularProgressIndicator(context);
        indicator.setIndeterminate(true);
        indicator.setVisibility(GONE);
        indicator.setIndicatorColor(Color.WHITE);
        indicator.setIndicatorSize(Utils.dp(30));
        indicator.setTrackThickness(Utils.dp(3));

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Utils.dp(30), Utils.dp(30));
        lp.gravity = Gravity.CENTER;
        addView(indicator, lp);
    }

    public void setLoading(boolean enabled) {
        View btn = getChildAt(1);
        indicator.setVisibility(enabled ? VISIBLE : GONE);
        btn.setVisibility(!enabled ? VISIBLE : GONE);
    }

    public boolean isLoading(){
        return indicator.getVisibility() == VISIBLE;
    }

}
