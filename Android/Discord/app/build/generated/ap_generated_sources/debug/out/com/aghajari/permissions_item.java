package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.res.ResourcesCompat;

public class permissions_item extends FrameLayout {

	public TextView title;
	public SwitchCompat switchView;
	public View divider;
	public View click;

    public permissions_item(Context context) {
        this(context, null);
    }

    public permissions_item(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public permissions_item(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initTitle();
		initSwitchView();
		initDivider();
		initClick();
	}

	protected void initThis() {
		this.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		this.setLayoutParams(new FrameLayout.LayoutParams(-1, dp(50f)));
	}

	protected void initTitle() {
		title = new TextView(getContext());
		title.setId(R.id.title);
		title.setTextSize(17);
		title.setTextColor(getResources().getColor(R.color.white));
		title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams title_lp = new FrameLayout.LayoutParams(-2, -2);
		title_lp.leftMargin = dp(24f);
		title_lp.rightMargin = dp(24f);
		title_lp.gravity = Gravity.CENTER_VERTICAL;
		this.addView(title, title_lp);
	}

	protected void initSwitchView() {
		switchView = new SwitchCompat(getContext());
		switchView.setId(R.id.switchView);
		FrameLayout.LayoutParams switchView_lp = new FrameLayout.LayoutParams(dp(50f), dp(50f));
		switchView_lp.leftMargin = dp(24f);
		switchView_lp.rightMargin = dp(24f);
		switchView_lp.gravity = Gravity.RIGHT;
		this.addView(switchView, switchView_lp);
	}

	protected void initDivider() {
		divider = new View(getContext());
		divider.setBackgroundColor(getResources().getColor(R.color.background_light));
		divider.setId(R.id.divider);
		FrameLayout.LayoutParams divider_lp = new FrameLayout.LayoutParams(-1, dp(1f));
		divider_lp.leftMargin = dp(24f);
		divider_lp.rightMargin = dp(24f);
		divider_lp.gravity = Gravity.BOTTOM;
		this.addView(divider, divider_lp);
	}

	protected void initClick() {
		click = new View(getContext());
		click.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		click.setId(R.id.click);
		this.addView(click, new FrameLayout.LayoutParams(-1, -1));
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