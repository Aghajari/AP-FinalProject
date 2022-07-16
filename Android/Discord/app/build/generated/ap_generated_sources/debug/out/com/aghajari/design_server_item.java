package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

public class design_server_item extends FrameLayout {

	public AppCompatEditText edt;
	public CardView group;
	public CardView channel;

	protected TextView view1;
	protected TextView view2;
	protected TextView view3;

    public design_server_item(Context context) {
        this(context, null);
    }

    public design_server_item(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public design_server_item(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initEdt();
		initView1();
		initGroup();
		initView2();
		initChannel();
		initView3();
	}

	protected void initThis() {
		this.setBackgroundResource(R.drawable.dark_rad);
		FrameLayout.LayoutParams this_lp = new FrameLayout.LayoutParams(-1, dp(56f));
		this_lp.setMargins(dp(24f), dp(6f), dp(24f), dp(6f));
		this.setLayoutParams(this_lp);
	}

	protected void initEdt() {
		edt = new AppCompatEditText(getContext());
		edt.setImeOptions(0x00000006);
		edt.setId(R.id.edt);
		edt.setTextSize(18);
		edt.setSingleLine(true);
		edt.setPadding(dp(16f), 0, dp(16f), 0);
		edt.setTextColor(getResources().getColor(R.color.white));
		edt.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		edt.setBackground(null);
		edt.setHintTextColor(getResources().getColor(R.color.gray));
		edt.setGravity(Gravity.CENTER_VERTICAL);
		FrameLayout.LayoutParams edt_lp = new FrameLayout.LayoutParams(-1, -1);
		edt_lp.rightMargin = dp(80f);
		this.addView(edt, edt_lp);
	}

	protected void initView1() {
		view1 = new TextView(getContext());
		view1.setTextSize(20);
		view1.setText("\ue25d");
		view1.setGravity(Gravity.CENTER);
		view1.setTextColor(getResources().getColor(R.color.white));
		view1.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		FrameLayout.LayoutParams view1_lp = new FrameLayout.LayoutParams(-2, -1);
		view1_lp.rightMargin = dp(8f);
		view1_lp.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
		this.addView(view1, view1_lp);
	}

	protected void initGroup() {
		group = new CardView(getContext());
		group.setId(R.id.group);
		group.setVisibility(View.GONE);
		group.setCardElevation(dp(4f));
		group.setCardBackgroundColor(0xFF5C64EA);
		group.setRadius(dp(56f));
		FrameLayout.LayoutParams group_lp = new FrameLayout.LayoutParams(dp(40f), dp(40f));
		group_lp.rightMargin = dp(38f);
		group_lp.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
		this.addView(group, group_lp);
	}

	protected void initView2() {
		view2 = new TextView(getContext());
		view2.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		view2.setTextSize(20);
		view2.setText("\ue7ef");
		view2.setGravity(Gravity.CENTER);
		view2.setTextColor(getResources().getColor(R.color.white));
		view2.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		FrameLayout.LayoutParams view2_lp = new FrameLayout.LayoutParams(-1, -1);
		view2_lp.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
		group.addView(view2, view2_lp);
	}

	protected void initChannel() {
		channel = new CardView(getContext());
		channel.setId(R.id.channel);
		channel.setVisibility(View.GONE);
		channel.setCardElevation(dp(4f));
		channel.setCardBackgroundColor(0xFF5C64EA);
		channel.setRadius(dp(56f));
		FrameLayout.LayoutParams channel_lp = new FrameLayout.LayoutParams(dp(40f), dp(40f));
		channel_lp.rightMargin = dp(38f);
		channel_lp.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
		this.addView(channel, channel_lp);
	}

	protected void initView3() {
		view3 = new TextView(getContext());
		view3.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		view3.setTextSize(20);
		view3.setText("\uef49");
		view3.setGravity(Gravity.CENTER);
		view3.setTextColor(getResources().getColor(R.color.white));
		view3.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		FrameLayout.LayoutParams view3_lp = new FrameLayout.LayoutParams(-1, -1);
		view3_lp.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
		channel.addView(view3, view3_lp);
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