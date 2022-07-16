package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

public class message_mine extends LinearLayout {

	public TextView time;
	public message_text message;

	protected FrameLayout view1;

    public message_mine(Context context) {
        this(context, null);
    }

    public message_mine(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public message_mine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initTime();
		initView1();
		initMessage();
	}

	protected void initThis() {
		this.setGravity(Gravity.RIGHT);
		this.setOrientation(0);
		LinearLayout.LayoutParams this_lp = new LinearLayout.LayoutParams(-1, -2);
		this_lp.topMargin = dp(1f);
		this_lp.bottomMargin = dp(1f);
		this.setLayoutParams(this_lp);
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
		this.addView(time, time_lp);
	}

	protected void initView1() {
		view1 = new FrameLayout(getContext());
		view1.setBackgroundResource(R.drawable.message_mine);
		LinearLayout.LayoutParams view1_lp = new LinearLayout.LayoutParams(-2, -2);
		view1_lp.rightMargin = dp(24f);
		view1_lp.gravity = Gravity.RIGHT;
		this.addView(view1, view1_lp);
	}

	protected void initMessage() {
		message = new message_text(getContext());
		message.setId(R.id.message);
		view1.addView(message);
	}

    private int dp(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, getResources().getDisplayMetrics());
    }

}