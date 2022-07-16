package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;

public class message_first_others_server extends FrameLayout {

	public AppCompatImageView avatar;
	public TextView title;
	public message_text message;
	public TextView time;

	protected LinearLayout view1;
	protected FrameLayout view2;

    public message_first_others_server(Context context) {
        this(context, null);
    }

    public message_first_others_server(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public message_first_others_server(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initAvatar();
		initTitle();
		initView1();
		initView2();
		initMessage();
		initTime();
	}

	protected void initThis() {
		FrameLayout.LayoutParams this_lp = new FrameLayout.LayoutParams(-1, -2);
		this_lp.topMargin = dp(8f);
		this_lp.bottomMargin = dp(1f);
		this.setLayoutParams(this_lp);
	}

	protected void initAvatar() {
		avatar = new AppCompatImageView(getContext());
		avatar.setId(R.id.avatar);
		FrameLayout.LayoutParams avatar_lp = new FrameLayout.LayoutParams(dp(40f), dp(40f));
		avatar_lp.leftMargin = dp(12f);
		this.addView(avatar, avatar_lp);
	}

	protected void initTitle() {
		title = new TextView(getContext());
		title.setId(R.id.title);
		title.setTextSize(10);
		title.setTextColor(getResources().getColor(R.color.time_text));
		title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams title_lp = new FrameLayout.LayoutParams(-2, -2);
		title_lp.leftMargin = dp(60f);
		this.addView(title, title_lp);
	}

	protected void initView1() {
		view1 = new LinearLayout(getContext());
		view1.setGravity(Gravity.LEFT);
		view1.setOrientation(0);
		FrameLayout.LayoutParams view1_lp = new FrameLayout.LayoutParams(-1, -2);
		view1_lp.leftMargin = dp(40f);
		view1_lp.topMargin = dp(16f);
		this.addView(view1, view1_lp);
	}

	protected void initView2() {
		view2 = new FrameLayout(getContext());
		view2.setBackgroundResource(R.drawable.message_first_others);
		LinearLayout.LayoutParams view2_lp = new LinearLayout.LayoutParams(-2, -2);
		view2_lp.leftMargin = dp(18f);
		view2_lp.gravity = Gravity.LEFT;
		view1.addView(view2, view2_lp);
	}

	protected void initMessage() {
		message = new message_text(getContext());
		message.setId(R.id.message);
		view2.addView(message);
	}

	protected void initTime() {
		time = new TextView(getContext());
		time.setId(R.id.time);
		time.setText("00:00");
		time.setTextSize(10);
		time.setTextColor(getResources().getColor(R.color.time_text));
		time.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams time_lp = new LinearLayout.LayoutParams(-2, -2);
		time_lp.leftMargin = dp(8f);
		time_lp.rightMargin = dp(8f);
		time_lp.gravity = Gravity.CENTER;
		view1.addView(time, time_lp);
	}

    private int dp(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, getResources().getDisplayMetrics());
    }

}