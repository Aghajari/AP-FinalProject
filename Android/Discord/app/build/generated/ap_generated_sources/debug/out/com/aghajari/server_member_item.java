package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.aghajari.views.ClipFrameLayout;

public class server_member_item extends FrameLayout {

	public chat_item chat;
	public TextView remove;
	public TextView permission;

	protected ClipFrameLayout view1;
	protected ClipFrameLayout view2;

    public server_member_item(Context context) {
        this(context, null);
    }

    public server_member_item(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public server_member_item(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initChat();
		initView1();
		initRemove();
		initView2();
		initPermission();
	}

	protected void initThis() {
		this.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
	}

	protected void initChat() {
		chat = new chat_item(getContext());
		chat.setId(R.id.chat);
		this.addView(chat);
	}

	protected void initView1() {
		view1 = new ClipFrameLayout(getContext());
		view1.setBackgroundResource(R.drawable.circle);
		FrameLayout.LayoutParams view1_lp = new FrameLayout.LayoutParams(dp(40f), dp(40f));
		view1_lp.rightMargin = dp(24f);
		view1_lp.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
		this.addView(view1, view1_lp);
	}

	protected void initRemove() {
		remove = new TextView(getContext());
		remove.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		remove.setId(R.id.remove);
		remove.setText("\ue15b");
		remove.setTextSize(18);
		remove.setGravity(Gravity.CENTER);
		remove.setTextColor(getResources().getColor(R.color.white));
		remove.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		FrameLayout.LayoutParams remove_lp = new FrameLayout.LayoutParams(-1, -1);
		remove_lp.gravity = Gravity.CENTER;
		view1.addView(remove, remove_lp);
	}

	protected void initView2() {
		view2 = new ClipFrameLayout(getContext());
		view2.setBackgroundResource(R.drawable.circle);
		FrameLayout.LayoutParams view2_lp = new FrameLayout.LayoutParams(dp(40f), dp(40f));
		view2_lp.rightMargin = dp(76f);
		view2_lp.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
		this.addView(view2, view2_lp);
	}

	protected void initPermission() {
		permission = new TextView(getContext());
		permission.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		permission.setId(R.id.permission);
		permission.setText("\ue73c");
		permission.setTextSize(18);
		permission.setGravity(Gravity.CENTER);
		permission.setTextColor(getResources().getColor(R.color.white));
		permission.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		FrameLayout.LayoutParams permission_lp = new FrameLayout.LayoutParams(-1, -1);
		permission_lp.gravity = Gravity.CENTER;
		view2.addView(permission, permission_lp);
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