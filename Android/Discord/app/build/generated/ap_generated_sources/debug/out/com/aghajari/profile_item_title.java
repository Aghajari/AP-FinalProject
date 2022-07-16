package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

public class profile_item_title extends FrameLayout {

	public TextView text;

    public profile_item_title(Context context) {
        this(context, null);
    }

    public profile_item_title(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public profile_item_title(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initText();
	}

	protected void initThis() {
		FrameLayout.LayoutParams this_lp = new FrameLayout.LayoutParams(-2, -2);
		this_lp.setMargins(dp(24f), dp(24f), dp(24f), dp(12f));
		this.setLayoutParams(this_lp);
	}

	protected void initText() {
		text = new TextView(getContext());
		text.setId(R.id.text);
		text.setTextSize(16);
		text.setTextColor(getResources().getColor(R.color.gray));
		text.setTypeface(ResourcesCompat.getFont(getContext(), R.font.semi_bold));
		this.addView(text, new FrameLayout.LayoutParams(-2, -2));
	}

    private int dp(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, getResources().getDisplayMetrics());
    }

}