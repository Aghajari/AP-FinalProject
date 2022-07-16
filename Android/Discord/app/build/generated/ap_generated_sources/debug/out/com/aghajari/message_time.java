package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

public class message_time extends FrameLayout {

	public TextView date;

	protected View view1;

    public message_time(Context context) {
        this(context, null);
    }

    public message_time(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public message_time(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initView1();
		initDate();
	}

	protected void initThis() {
		FrameLayout.LayoutParams this_lp = new FrameLayout.LayoutParams(-1, -2);
		this_lp.setMargins(dp(24f), dp(12f), dp(24f), dp(12f));
		this.setLayoutParams(this_lp);
	}

	protected void initView1() {
		view1 = new View(getContext());
		view1.setBackgroundColor(getResources().getColor(R.color.background_dark2));
		FrameLayout.LayoutParams view1_lp = new FrameLayout.LayoutParams(-1, dp(1f));
		view1_lp.gravity = Gravity.CENTER;
		this.addView(view1, view1_lp);
	}

	protected void initDate() {
		date = new TextView(getContext());
		date.setBackgroundColor(getResources().getColor(R.color.background));
		date.setId(R.id.date);
		date.setTextSize(13);
		date.setGravity(Gravity.CENTER);
		date.setPadding(dp(12f), 0, dp(12f), 0);
		date.setTextColor(getResources().getColor(R.color.white));
		date.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams date_lp = new FrameLayout.LayoutParams(-2, -2);
		date_lp.gravity = Gravity.CENTER;
		this.addView(date, date_lp);
	}

    private int dp(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, getResources().getDisplayMetrics());
    }

}