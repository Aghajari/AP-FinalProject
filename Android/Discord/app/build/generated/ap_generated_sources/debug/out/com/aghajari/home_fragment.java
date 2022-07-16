package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.axanimation.layouts.AXAFrameLayout;

public class home_fragment extends AXAFrameLayout {

	public RecyclerView rv;
	public AppCompatEditText search;
	public AppCompatImageView profile;

	protected CardView view1;
	protected TextView view2;

    public home_fragment(Context context) {
        this(context, null);
    }

    public home_fragment(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public home_fragment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initRv();
		initView1();
		initView2();
		initSearch();
		initProfile();
	}

	protected void initThis() {
		this.setLayoutParams(new AXAFrameLayout.LayoutParams(-1, -1));
	}

	protected void initRv() {
		rv = new RecyclerView(getContext());
		rv.setId(R.id.rv);
		rv.setFocusableInTouchMode(true);
		AXAFrameLayout.LayoutParams rv_lp = new AXAFrameLayout.LayoutParams(-1, -1);
		rv_lp.topMargin = dp(104f);
		this.addView(rv, rv_lp);
	}

	protected void initView1() {
		view1 = new CardView(getContext());
		view1.setCardElevation(dp(4f));
		view1.setCardBackgroundColor(getResources().getColor(R.color.background_dark));
		view1.setRadius(dp(8f));
		AXAFrameLayout.LayoutParams view1_lp = new AXAFrameLayout.LayoutParams(-1, dp(56f));
		view1_lp.leftMargin = dp(24f);
		view1_lp.topMargin = dp(48f);
		view1_lp.rightMargin = dp(24f);
		this.addView(view1, view1_lp);
	}

	protected void initView2() {
		view2 = new TextView(getContext());
		view2.setTextSize(25);
		view2.setText("\ue8b6");
		view2.setGravity(Gravity.CENTER);
		view2.setTextColor(getResources().getColor(R.color.white));
		view2.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		view1.addView(view2, new CardView.LayoutParams(dp(56f), dp(56f)));
	}

	protected void initSearch() {
		search = new AppCompatEditText(getContext());
		search.setImeOptions(0x00000006);
		search.setId(R.id.search);
		search.setTextSize(18);
		search.setSingleLine(true);
		search.setHint("Search");
		search.setTextColor(getResources().getColor(R.color.white));
		search.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		search.setBackground(null);
		search.setHintTextColor(getResources().getColor(R.color.gray));
		CardView.LayoutParams search_lp = new CardView.LayoutParams(-1, -1);
		search_lp.leftMargin = dp(56f);
		search_lp.rightMargin = dp(56f);
		view1.addView(search, search_lp);
	}

	protected void initProfile() {
		profile = new AppCompatImageView(getContext());
		profile.setId(R.id.profile);
		android.widget.FrameLayout.LayoutParams profile_lp = new android.widget.FrameLayout.LayoutParams(dp(40f), dp(40f));
		profile_lp.setMargins(dp(8f), dp(8f), dp(8f), dp(8f));
		profile_lp.gravity = Gravity.RIGHT;
		view1.addView(profile, profile_lp);
	}

    private int dp(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, getResources().getDisplayMetrics());
    }

}