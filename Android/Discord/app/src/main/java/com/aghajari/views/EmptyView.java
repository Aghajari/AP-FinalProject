package com.aghajari.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Space;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import com.aghajari.R;
import com.aghajari.rlottie.AXrLottieDrawable;
import com.bumptech.glide.Glide;

public class EmptyView extends LinearLayout {

    public enum Type {
        NO_CHATS(R.drawable.online, "No chat or server exists!\nSearch for friends..."),
        NO_RESULTS(R.drawable.online, "No results!"),
        NO_ONLINE(R.drawable.online, "No one's around :("),
        NO_FRIEND(R.drawable.friends, "You have no friends\nNobody likes you :D"),
        NO_PENDING(R.drawable.pending, "There are no pending friend request."),
        NO_BlOCK(R.drawable.block, "You haven't blocked anyone."),
        NO_MUTUAL_FRIENDS(R.drawable.no_friends, "NO FRIENDS IN COMMON"),
        NO_CHAT(0, "Send Hello");

        private final String text;
        private final int image;

        Type(int image, String text) {
            this.text = text;
            this.image = image;
        }

        public String getText() {
            return text;
        }

        public int getImage() {
            return image;
        }
    }

    public EmptyView(Context context, Type type) {
        super(context);

        setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        setOrientation(VERTICAL);

        if (type != Type.NO_CHAT)
            addView(new Space(getContext()), new LayoutParams(1, Utils.perY(10)));
        else
            setGravity(Gravity.CENTER);

        AppCompatImageView imageView = new AppCompatImageView(context);
        LayoutParams lp = new LayoutParams(Utils.dp(300), Utils.dp(200));
        addView(imageView, lp);

        if (type == Type.NO_CHAT) {
            imageView.setImageDrawable(
                    AXrLottieDrawable.fromAssets(context, "hi.json")
                            .setAutoRepeat(true)
                            .setAutoStart(true)
                            .build()
            );

            ClipFrameLayout btn = new ClipFrameLayout(context);
            btn.setId(R.id.chat_rv);

            lp = new LayoutParams(Utils.dp(150), Utils.dp(40));
            int margin = Utils.dp(24);
            lp.setMargins(margin, margin / 2, margin, margin);
            addView(btn, lp);

            AppCompatTextView tv = new AppCompatTextView(context);
            tv.setTypeface(ResourcesCompat.getFont(context, R.font.regular));
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(16);
            tv.setGravity(Gravity.CENTER);
            tv.setText(type.getText());

            btn.addView(tv);
        } else {
            Glide.with(context)
                    .load(type.getImage())
                    .fitCenter()
                    .into(imageView);

            AppCompatTextView tv = new AppCompatTextView(context);
            tv.setTypeface(ResourcesCompat.getFont(context, R.font.regular));
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(16);
            tv.setGravity(Gravity.CENTER);
            tv.setText(type.getText());
            lp = new LayoutParams(-2, -2);
            int margin = Utils.dp(24);
            lp.setMargins(margin, margin / 2, margin, margin);
            addView(tv, lp);
        }

        setLayoutParams(new LayoutParams(-1, -1));
    }


}
