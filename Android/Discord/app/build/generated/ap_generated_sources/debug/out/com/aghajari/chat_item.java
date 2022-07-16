package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;

public class chat_item extends FrameLayout {

	public AppCompatImageView profile;
	public TextView username;
	public TextView nickname;
	public View divider;

	protected LinearLayout view1;

    public chat_item(Context context) {
        this(context, null);
    }

    public chat_item(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public chat_item(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initProfile();
		initView1();
		initUsername();
		initNickname();
		initDivider();
	}

	protected void initThis() {
		this.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		this.setPadding(dp(24f), 0, dp(24f), 0);
		this.setLayoutParams(new FrameLayout.LayoutParams(-1, dp(80f)));
	}

	protected void initProfile() {
		profile = new AppCompatImageView(getContext());
		profile.setId(R.id.profile);
		FrameLayout.LayoutParams profile_lp = new FrameLayout.LayoutParams(dp(50f), dp(50f));
		profile_lp.gravity = Gravity.CENTER_VERTICAL;
		this.addView(profile, profile_lp);
	}

	protected void initView1() {
		view1 = new LinearLayout(getContext());
		view1.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		view1.setOrientation(1);
		FrameLayout.LayoutParams view1_lp = new FrameLayout.LayoutParams(-1, -1);
		view1_lp.leftMargin = dp(62f);
		this.addView(view1, view1_lp);
	}

	protected void initUsername() {
		username = new TextView(getContext());
		username.setId(R.id.username);
		username.setTextSize(17);
		username.setTextColor(getResources().getColor(R.color.white));
		username.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		view1.addView(username, new LinearLayout.LayoutParams(-2, -2));
	}

	protected void initNickname() {
		nickname = new TextView(getContext());
		nickname.setId(R.id.nickname);
		nickname.setTextSize(15);
		nickname.setTextColor(getResources().getColor(R.color.gray));
		nickname.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		view1.addView(nickname, new LinearLayout.LayoutParams(-2, -2));
	}

	protected void initDivider() {
		divider = new View(getContext());
		divider.setBackgroundColor(getResources().getColor(R.color.background_light));
		divider.setId(R.id.divider);
		FrameLayout.LayoutParams divider_lp = new FrameLayout.LayoutParams(-1, dp(1f));
		divider_lp.leftMargin = dp(60f);
		divider_lp.gravity = Gravity.BOTTOM;
		this.addView(divider, divider_lp);
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