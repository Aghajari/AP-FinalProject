package com.aghajari.views;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.aghajari.Application;
import com.aghajari.R;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.models.MessageModel;
import com.aghajari.shared.models.UserModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.HashMap;

public class NotificationCenter {

    private static final String CHANNEL_MESSAGE = "message";
    private static final String GROUP_MESSAGE = "com.aghajari.MESSAGE";

    private static int id = 0;
    private static final HashMap<String, Integer> notificationIds = new HashMap<>();

    public static void cancel() {

    }

    public static void cancel(UserModel model) {
        Integer old = notificationIds.get(model.getId());

        if (old != null)
            NotificationManagerCompat.from(Application.context)
                    .cancel(old);
    }

    public static void notify(MessageModel model) {
        if (model.fromId.equals(MyInfo.getInstance().getId()))
            return;
        if (!model.toId.equals(MyInfo.getInstance().getId()))
            return;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                Application.context, CHANNEL_MESSAGE)
                .setSmallIcon(R.drawable.send)
                .setContentText(model.text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup(GROUP_MESSAGE);

        if (model.getUser() == null) {
            builder.setContentTitle("Message");
        } else {
            builder.setContentTitle(model.getUser().getName());

            if (model.getUser().getAvatar() != null && !model.getUser().getAvatar().isEmpty()
                    && !model.getUser().getAvatar().endsWith("avatar.png")) {

                Glide.with(Application.context)
                        .asBitmap()
                        .load(model.getUser().getAvatar())
                        .addListener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object m, Target<Bitmap> target, boolean isFirstResource) {
                                NotificationCenter.notify(builder, model.getUser());
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object m, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                if (resource != null)
                                    builder.setLargeIcon(resource);

                                NotificationCenter.notify(builder, model.getUser());
                                return false;
                            }
                        }).preload();
                return;
            } else {
                builder.setLargeIcon(Utils.createBitmap(model.getUser().nickname));
            }
        }
        notify(builder, model.getUser());
    }

    private static void notify(NotificationCompat.Builder builder, UserModel model) {
        String key = model == null ? "UNKNOWN" : model.getId();
        Integer old = notificationIds.get(key);
        if (old == null) {
            old = id++;
            notificationIds.put(key, old);
        }
        System.out.println("Hi2");
        NotificationManagerCompat.from(Application.context)
                .notify(old, builder.build());
    }

    public static void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Messages";
            String description = "Messages";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_MESSAGE, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = Application.context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
