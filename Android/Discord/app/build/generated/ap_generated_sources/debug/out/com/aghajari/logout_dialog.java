package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.aghajari.views.ClipFrameLayout;

public class logout_dialog extends CardView {

	public AppCompatTextView title;
	public AppCompatTextView subtitle;
	public AppCompatTextView cancel;
	public AppCompatTextView accept;

	protected LinearLayout view1;
	protected LinearLayout view2;
	protected ClipFrameLayout view3;
	protected ClipFrameLayout view4;

    public logout_dialog(Context context) {
        this(context, null);
    }

    public logout_dialog(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public logout_dialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initView1();
		initTitle();
		initSubtitle();
		initView2();
		initView3();
		initCancel();
		initView4();
		initAccept();
	}

	protected void initThis() {
		this.setCardElevation(dp(0f));
		this.setCardBackgroundColor(getResources().getColor(R.color.background_dark2));
		this.setRadius(dp(16f));
		android.widget.FrameLayout.LayoutParams this_lp = new android.widget.FrameLayout.LayoutParams(-1, dp(220f));
		this_lp.gravity = Gravity.CENTER;
		this.setLayoutParams(this_lp);
	}

	protected void initView1() {
		view1 = new LinearLayout(getContext());
		view1.setOrientation(1);
		android.widget.FrameLayout.LayoutParams view1_lp = new android.widget.FrameLayout.LayoutParams(-1, -2);
		view1_lp.gravity = Gravity.CENTER;
		this.addView(view1, view1_lp);
	}

	protected void initTitle() {
		title = new AppCompatTextView(getContext());
		title.setId(R.id.title);
		title.setText("Log Out");
		title.setTextSize(20);
		title.setTextColor(getResources().getColor(R.color.white));
		title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.semi_bold));
		LinearLayout.LayoutParams title_lp = new LinearLayout.LayoutParams(-2, -2);
		title_lp.leftMargin = dp(24f);
		title_lp.topMargin = dp(24f);
		title_lp.rightMargin = dp(24f);
		title_lp.gravity = Gravity.CENTER_HORIZONTAL;
		view1.addView(title, title_lp);
	}

	protected void initSubtitle() {
		subtitle = new AppCompatTextView(getContext());
		subtitle.setId(R.id.subtitle);
		subtitle.setText("Are you sure you want to logout?");
		subtitle.setTextSize(17);
		subtitle.setTextColor(getResources().getColor(R.color.gray));
		subtitle.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams subtitle_lp = new LinearLayout.LayoutParams(-2, -2);
		subtitle_lp.setMargins(dp(18f), dp(20f), dp(18f), dp(20f));
		subtitle_lp.gravity = Gravity.CENTER_HORIZONTAL;
		view1.addView(subtitle, subtitle_lp);
	}

	protected void initView2() {
		view2 = new LinearLayout(getContext());
		LinearLayout.LayoutParams view2_lp = new LinearLayout.LayoutParams(dp(268f), dp(46f));
		view2_lp.topMargin = dp(12f);
		view2_lp.bottomMargin = dp(30f);
		view2_lp.gravity = Gravity.CENTER;
		view1.addView(view2, view2_lp);
	}

	protected void initView3() {
		view3 = new ClipFrameLayout(getContext());
		view3.setBackgroundResource(R.drawable.bg_trans);
		LinearLayout.LayoutParams view3_lp = new LinearLayout.LayoutParams(dp(0f), -1);
		view3_lp.weight = (float) 0.5f;
		view2.addView(view3, view3_lp);
	}

	protected void initCancel() {
		cancel = new AppCompatTextView(getContext());
		cancel.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		cancel.setId(R.id.cancel);
		cancel.setTextSize(18);
		cancel.setText("Cancel");
		cancel.setGravity(Gravity.CENTER);
		cancel.setTextColor(getResources().getColor(R.color.gray));
		cancel.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		android.widget.FrameLayout.LayoutParams cancel_lp = new android.widget.FrameLayout.LayoutParams(-1, -1);
		cancel_lp.gravity = Gravity.CENTER;
		view3.addView(cancel, cancel_lp);
	}

	protected void initView4() {
		view4 = new ClipFrameLayout(getContext());
		view4.setBackgroundResource(R.drawable.bg);
		LinearLayout.LayoutParams view4_lp = new LinearLayout.LayoutParams(dp(0f), -1);
		view4_lp.weight = (float) 0.5f;
		view2.addView(view4, view4_lp);
	}

	protected void initAccept() {
		accept = new AppCompatTextView(getContext());
		accept.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		accept.setId(R.id.accept);
		accept.setTextSize(18);
		accept.setText("Log Out");
		accept.setGravity(Gravity.CENTER);
		accept.setTextColor(getResources().getColor(R.color.white));
		accept.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		android.widget.FrameLayout.LayoutParams accept_lp = new android.widget.FrameLayout.LayoutParams(-1, -1);
		accept_lp.gravity = Gravity.CENTER;
		view4.addView(accept, accept_lp);
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