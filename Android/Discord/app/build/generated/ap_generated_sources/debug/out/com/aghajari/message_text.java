package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

public class message_text extends LinearLayout {

	public TextView text;
	public LinearLayout reactions;

    public message_text(Context context) {
        this(context, null);
    }

    public message_text(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public message_text(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initText();
		initReactions();
	}

	protected void initThis() {
		this.setOrientation(1);
		this.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
	}

	protected void initText() {
		text = new TextView(getContext());
		text.setId(R.id.text);
		text.setTextSize(15);
		text.setMaxWidth(dp(250f));
		text.setTextColor(getResources().getColor(R.color.white));
		text.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams text_lp = new LinearLayout.LayoutParams(-2, -2);
		text_lp.setMargins(dp(12f), dp(12f), dp(12f), dp(12f));
		text_lp.gravity = Gravity.CENTER;
		this.addView(text, text_lp);
	}

	protected void initReactions() {
		reactions = new LinearLayout(getContext());
		reactions.setId(R.id.reactions);
		reactions.setVisibility(View.GONE);
		reactions.setOrientation(0);
		LinearLayout.LayoutParams reactions_lp = new LinearLayout.LayoutParams(-2, -2);
		reactions_lp.setMargins(dp(8f), dp(-8f), dp(8f), dp(12f));
		this.addView(reactions, reactions_lp);
	}

    private int dp(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, getResources().getDisplayMetrics());
    }

}