package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

public class profile_item extends LinearLayout {

	public TextView icon;
	public TextView text;
	public LinearLayout next;
	public TextView next_tv;

	protected TextView view1;

    public profile_item(Context context) {
        this(context, null);
    }

    public profile_item(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public profile_item(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initIcon();
		initText();
		initNext();
		initNext_tv();
		initView1();
	}

	protected void initThis() {
		this.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		this.setId(R.id.username_click);
		this.setTag("username");
		this.setPadding(dp(24f), 0, dp(24f), 0);
		this.setOrientation(0);
		this.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(56f)));
	}

	protected void initIcon() {
		icon = new TextView(getContext());
		icon.setId(R.id.icon);
		icon.setTextSize(25);
		icon.setGravity(Gravity.CENTER);
		icon.setTextColor(getResources().getColor(R.color.white));
		icon.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		this.addView(icon, new LinearLayout.LayoutParams(-2, -1));
	}

	protected void initText() {
		text = new TextView(getContext());
		text.setId(R.id.text);
		text.setTextSize(15);
		text.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		text.setTextColor(getResources().getColor(R.color.white));
		text.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams text_lp = new LinearLayout.LayoutParams(dp(0f), -1);
		text_lp.leftMargin = dp(24f);
		text_lp.weight = (float) 1f;
		this.addView(text, text_lp);
	}

	protected void initNext() {
		next = new LinearLayout(getContext());
		next.setId(R.id.next);
		next.setOrientation(0);
		this.addView(next, new LinearLayout.LayoutParams(-2, -1));
	}

	protected void initNext_tv() {
		next_tv = new TextView(getContext());
		next_tv.setId(R.id.next_tv);
		next_tv.setTextSize(14);
		next_tv.setGravity(Gravity.CENTER);
		next_tv.setTextColor(getResources().getColor(R.color.gray));
		next_tv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams next_tv_lp = new LinearLayout.LayoutParams(-2, -1);
		next_tv_lp.rightMargin = dp(12f);
		next.addView(next_tv, next_tv_lp);
	}

	protected void initView1() {
		view1 = new TextView(getContext());
		view1.setText("\ue5e1");
		view1.setTextSize(18);
		view1.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		view1.setTextColor(getResources().getColor(R.color.gray));
		view1.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		next.addView(view1, new LinearLayout.LayoutParams(-2, -1));
	}

    private int resolveAttribute(int attrId){
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(attrId, outValue, true);
        return outValue.resourceId;
    }

    private int dp(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, getResources().getDisplayMetrics());
    }

}