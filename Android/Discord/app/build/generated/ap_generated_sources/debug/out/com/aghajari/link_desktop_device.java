package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.aghajari.views.ClipFrameLayout;

public class link_desktop_device extends ClipFrameLayout {

	public View click;

	protected LinearLayout view1;
	protected TextView view2;
	protected TextView view3;

    public link_desktop_device(Context context) {
        this(context, null);
    }

    public link_desktop_device(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public link_desktop_device(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initView1();
		initView2();
		initView3();
		initClick();
	}

	protected void initThis() {
		this.setBackgroundResource(R.drawable.dark_rad);
		this.setElevation(dp(4f));
		ClipFrameLayout.LayoutParams this_lp = new ClipFrameLayout.LayoutParams(-1, dp(48f));
		this_lp.setMargins(dp(24f), dp(24f), dp(24f), dp(12f));
		this.setLayoutParams(this_lp);
	}

	protected void initView1() {
		view1 = new LinearLayout(getContext());
		view1.setGravity(Gravity.CENTER);
		view1.setOrientation(0);
		this.addView(view1, new ClipFrameLayout.LayoutParams(-1, -1));
	}

	protected void initView2() {
		view2 = new TextView(getContext());
		view2.setTextSize(20);
		view2.setText("\uef6b");
		view2.setGravity(Gravity.CENTER);
		view2.setTextColor(getResources().getColor(R.color.white));
		view2.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		LinearLayout.LayoutParams view2_lp = new LinearLayout.LayoutParams(-2, -1);
		view2_lp.rightMargin = dp(6f);
		view1.addView(view2, view2_lp);
	}

	protected void initView3() {
		view3 = new TextView(getContext());
		view3.setText("Link Desktop Device");
		view3.setTextSize(15);
		view3.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		view3.setTextColor(getResources().getColor(R.color.white));
		view3.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams view3_lp = new LinearLayout.LayoutParams(-2, -1);
		view3_lp.leftMargin = dp(6f);
		view1.addView(view3, view3_lp);
	}

	protected void initClick() {
		click = new View(getContext());
		click.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		click.setId(R.id.click);
		this.addView(click, new ClipFrameLayout.LayoutParams(-1, -1));
	}

    private int dp(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, getResources().getDisplayMetrics());
    }

    private int resolveAttribute(int attrId){
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(attrId, outValue, true);
        return outValue.resourceId;
    }

}