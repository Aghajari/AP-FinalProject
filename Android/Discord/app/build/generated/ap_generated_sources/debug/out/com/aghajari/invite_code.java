package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

public class invite_code extends LinearLayout {

	public FrameLayout header;
	public TextView back;
	public TextView title;
	public TextView code;
	public LinearLayout copy;
	public LinearLayout revoke;

	protected CardView view1;
	protected CardView view2;
	protected LinearLayout view3;
	protected TextView view4;
	protected TextView view5;
	protected TextView view6;
	protected TextView view7;

    public invite_code(Context context) {
        this(context, null);
    }

    public invite_code(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public invite_code(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initView1();
		initHeader();
		initBack();
		initTitle();
		initView2();
		initCode();
		initView3();
		initCopy();
		initView4();
		initView5();
		initRevoke();
		initView6();
		initView7();
	}

	protected void initThis() {
		this.setOrientation(1);
		this.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
	}

	protected void initView1() {
		view1 = new CardView(getContext());
		view1.setCardElevation(dp(4f));
		view1.setCardBackgroundColor(getResources().getColor(R.color.background_dark2));
		this.addView(view1, new LinearLayout.LayoutParams(-1, -2));
	}

	protected void initHeader() {
		header = new FrameLayout(getContext());
		header.setId(R.id.header);
		FrameLayout.LayoutParams header_lp = new FrameLayout.LayoutParams(-1, dp(56f));
		header_lp.gravity = Gravity.BOTTOM;
		view1.addView(header, header_lp);
	}

	protected void initBack() {
		back = new TextView(getContext());
		back.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackgroundBorderless));
		back.setId(R.id.back);
		back.setText("\ue5c4");
		back.setTextSize(25);
		back.setGravity(Gravity.CENTER);
		back.setTextColor(getResources().getColor(R.color.white));
		back.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		FrameLayout.LayoutParams back_lp = new FrameLayout.LayoutParams(dp(30f), dp(30f));
		back_lp.setMargins(dp(13f), dp(13f), dp(13f), dp(13f));
		header.addView(back, back_lp);
	}

	protected void initTitle() {
		title = new TextView(getContext());
		title.setId(R.id.title);
		title.setText("Invite Code");
		title.setTextSize(18);
		title.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		title.setTextColor(getResources().getColor(R.color.white));
		title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.semi_bold));
		FrameLayout.LayoutParams title_lp = new FrameLayout.LayoutParams(-2, -1);
		title_lp.leftMargin = dp(72f);
		title_lp.rightMargin = dp(72f);
		header.addView(title, title_lp);
	}

	protected void initView2() {
		view2 = new CardView(getContext());
		view2.setCardElevation(dp(4f));
		view2.setCardBackgroundColor(getResources().getColor(R.color.background_dark2));
		view2.setRadius(dp(8f));
		LinearLayout.LayoutParams view2_lp = new LinearLayout.LayoutParams(-1, dp(56f));
		view2_lp.setMargins(dp(24f), dp(24f), dp(24f), dp(24f));
		this.addView(view2, view2_lp);
	}

	protected void initCode() {
		code = new TextView(getContext());
		code.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		code.setId(R.id.code);
		code.setTextSize(20);
		code.setText("CODE");
		code.setGravity(Gravity.CENTER_VERTICAL);
		code.setPadding(dp(24f), 0, dp(24f), 0);
		code.setTextColor(getResources().getColor(R.color.white));
		code.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		view2.addView(code, new CardView.LayoutParams(-1, -1));
	}

	protected void initView3() {
		view3 = new LinearLayout(getContext());
		view3.setGravity(Gravity.CENTER);
		view3.setOrientation(0);
		LinearLayout.LayoutParams view3_lp = new LinearLayout.LayoutParams(-1, dp(40f));
		view3_lp.leftMargin = dp(18f);
		view3_lp.rightMargin = dp(18f);
		this.addView(view3, view3_lp);
	}

	protected void initCopy() {
		copy = new LinearLayout(getContext());
		copy.setBackgroundResource(R.drawable.gradient2_rad2);
		copy.setId(R.id.copy);
		copy.setGravity(Gravity.CENTER);
		copy.setOrientation(0);
		LinearLayout.LayoutParams copy_lp = new LinearLayout.LayoutParams(dp(0f), -1);
		copy_lp.leftMargin = dp(6f);
		copy_lp.rightMargin = dp(6f);
		copy_lp.weight = (float) 0.5f;
		view3.addView(copy, copy_lp);
	}

	protected void initView4() {
		view4 = new TextView(getContext());
		view4.setTextSize(20);
		view4.setText("\ue14d");
		view4.setGravity(Gravity.CENTER);
		view4.setTextColor(getResources().getColor(R.color.white));
		view4.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		LinearLayout.LayoutParams view4_lp = new LinearLayout.LayoutParams(-2, -1);
		view4_lp.rightMargin = dp(6f);
		copy.addView(view4, view4_lp);
	}

	protected void initView5() {
		view5 = new TextView(getContext());
		view5.setText("Copy");
		view5.setTextSize(15);
		view5.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		view5.setTextColor(getResources().getColor(R.color.white));
		view5.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams view5_lp = new LinearLayout.LayoutParams(-2, -1);
		view5_lp.leftMargin = dp(6f);
		copy.addView(view5, view5_lp);
	}

	protected void initRevoke() {
		revoke = new LinearLayout(getContext());
		revoke.setBackgroundResource(R.drawable.gradient_rad2);
		revoke.setId(R.id.revoke);
		revoke.setGravity(Gravity.CENTER);
		revoke.setOrientation(0);
		LinearLayout.LayoutParams revoke_lp = new LinearLayout.LayoutParams(dp(0f), -1);
		revoke_lp.leftMargin = dp(6f);
		revoke_lp.rightMargin = dp(6f);
		revoke_lp.weight = (float) 0.5f;
		view3.addView(revoke, revoke_lp);
	}

	protected void initView6() {
		view6 = new TextView(getContext());
		view6.setTextSize(20);
		view6.setText("\ue872");
		view6.setGravity(Gravity.CENTER);
		view6.setTextColor(getResources().getColor(R.color.white));
		view6.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		LinearLayout.LayoutParams view6_lp = new LinearLayout.LayoutParams(-2, -1);
		view6_lp.rightMargin = dp(6f);
		revoke.addView(view6, view6_lp);
	}

	protected void initView7() {
		view7 = new TextView(getContext());
		view7.setText("Revoke");
		view7.setTextSize(15);
		view7.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		view7.setTextColor(getResources().getColor(R.color.white));
		view7.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams view7_lp = new LinearLayout.LayoutParams(-2, -1);
		view7_lp.leftMargin = dp(6f);
		revoke.addView(view7, view7_lp);
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