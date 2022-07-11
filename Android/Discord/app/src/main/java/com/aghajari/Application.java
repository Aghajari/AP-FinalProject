package com.aghajari;

import android.content.Context;

import com.aghajari.rlottie.AXrLottie;
import com.aghajari.views.NotificationCenter;

public class Application extends android.app.Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        AXrLottie.init(this);
        context = this;
        NotificationCenter.createNotificationChannel();
    }

}
