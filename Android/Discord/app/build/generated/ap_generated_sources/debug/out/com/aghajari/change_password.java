package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.aghajari.views.LoadingButton;

public class change_password extends LinearLayout {

	public FrameLayout header;
	public TextView back;
	public LoadingButton progress;
	public TextView save;
	public TextView title;
	public AppCompatEditText current_pass;
	public AppCompatEditText new_pass;
	public AppCompatEditText confirm_pass;

	protected CardView view1;

    public change_password(Context context) {
        this(context, null);
    }

    public change_password(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public change_password(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initView1();
		initHeader();
		initBack();
		initProgress();
		initSave();
		initTitle();
		initCurrent_pass();
		initNew_pass();
		initConfirm_pass();
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

	protected void initProgress() {
		progress = new LoadingButton(getContext());
		progress.setId(R.id.progress);
		FrameLayout.LayoutParams progress_lp = new FrameLayout.LayoutParams(dp(56f), dp(56f));
		progress_lp.gravity = Gravity.RIGHT;
		header.addView(progress, progress_lp);
	}

	protected void initSave() {
		save = new TextView(getContext());
		save.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackgroundBorderless));
		save.setId(R.id.save);
		save.setText("\ue876");
		save.setTextSize(25);
		save.setGravity(Gravity.CENTER);
		save.setTextColor(getResources().getColor(R.color.white));
		save.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		FrameLayout.LayoutParams save_lp = new FrameLayout.LayoutParams(dp(30f), dp(30f));
		save_lp.gravity = Gravity.CENTER;
		progress.addView(save, save_lp);
	}

	protected void initTitle() {
		title = new TextView(getContext());
		title.setId(R.id.title);
		title.setText("Change Password");
		title.setTextSize(18);
		title.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		title.setTextColor(getResources().getColor(R.color.white));
		title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.semi_bold));
		FrameLayout.LayoutParams title_lp = new FrameLayout.LayoutParams(-2, -1);
		title_lp.leftMargin = dp(72f);
		title_lp.rightMargin = dp(72f);
		header.addView(title, title_lp);
	}

	protected void initCurrent_pass() {
		current_pass = new AppCompatEditText(getContext());
		current_pass.setImeOptions(0x00000006);
		current_pass.setId(R.id.current_pass);
		current_pass.setTextSize(16);
		current_pass.setSingleLine(true);
		current_pass.setHint("Current Password");
		current_pass.setTextColor(getResources().getColor(R.color.white));
		current_pass.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		current_pass.setInputType(0x00000081);
		current_pass.setHintTextColor(getResources().getColor(R.color.gray));
		LinearLayout.LayoutParams current_pass_lp = new LinearLayout.LayoutParams(-1, dp(56f));
		current_pass_lp.setMargins(dp(24f), dp(24f), dp(24f), dp(12f));
		this.addView(current_pass, current_pass_lp);
	}

	protected void initNew_pass() {
		new_pass = new AppCompatEditText(getContext());
		new_pass.setImeOptions(0x00000006);
		new_pass.setId(R.id.new_pass);
		new_pass.setTextSize(16);
		new_pass.setSingleLine(true);
		new_pass.setHint("New Password");
		new_pass.setTextColor(getResources().getColor(R.color.white));
		new_pass.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		new_pass.setInputType(0x00000081);
		new_pass.setHintTextColor(getResources().getColor(R.color.gray));
		LinearLayout.LayoutParams new_pass_lp = new LinearLayout.LayoutParams(-1, dp(56f));
		new_pass_lp.leftMargin = dp(24f);
		new_pass_lp.rightMargin = dp(24f);
		new_pass_lp.bottomMargin = dp(12f);
		this.addView(new_pass, new_pass_lp);
	}

	protected void initConfirm_pass() {
		confirm_pass = new AppCompatEditText(getContext());
		confirm_pass.setImeOptions(0x00000006);
		confirm_pass.setId(R.id.confirm_pass);
		confirm_pass.setTextSize(16);
		confirm_pass.setSingleLine(true);
		confirm_pass.setHint("Confirm Password");
		confirm_pass.setTextColor(getResources().getColor(R.color.white));
		confirm_pass.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		confirm_pass.setInputType(0x00000081);
		confirm_pass.setHintTextColor(getResources().getColor(R.color.gray));
		LinearLayout.LayoutParams confirm_pass_lp = new LinearLayout.LayoutParams(-1, dp(56f));
		confirm_pass_lp.leftMargin = dp(24f);
		confirm_pass_lp.rightMargin = dp(24f);
		this.addView(confirm_pass, confirm_pass_lp);
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