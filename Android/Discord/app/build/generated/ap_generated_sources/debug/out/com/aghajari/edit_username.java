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

public class edit_username extends LinearLayout {

	public FrameLayout header;
	public TextView back;
	public LoadingButton progress;
	public TextView save;
	public TextView title;
	public AppCompatEditText username_edt;
	public TextView hint;

	protected CardView view1;

    public edit_username(Context context) {
        this(context, null);
    }

    public edit_username(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public edit_username(Context context, AttributeSet attrs, int defStyleAttr) {
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
		initUsername_edt();
		initHint();
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
		title.setText("Username");
		title.setTextSize(18);
		title.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		title.setTextColor(getResources().getColor(R.color.white));
		title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.semi_bold));
		FrameLayout.LayoutParams title_lp = new FrameLayout.LayoutParams(-2, -1);
		title_lp.leftMargin = dp(72f);
		title_lp.rightMargin = dp(72f);
		header.addView(title, title_lp);
	}

	protected void initUsername_edt() {
		username_edt = new AppCompatEditText(getContext());
		username_edt.setId(R.id.username_edt);
		username_edt.setImeOptions(0x00000006);
		username_edt.setHintTextColor(getResources().getColor(R.color.gray));
		username_edt.setTextSize(16);
		username_edt.setSingleLine(true);
		username_edt.setHint("Username");
		username_edt.setTextColor(getResources().getColor(R.color.white));
		username_edt.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams username_edt_lp = new LinearLayout.LayoutParams(-1, dp(56f));
		username_edt_lp.setMargins(dp(24f), dp(24f), dp(24f), dp(24f));
		this.addView(username_edt, username_edt_lp);
	}

	protected void initHint() {
		hint = new TextView(getContext());
		hint.setId(R.id.hint);
		hint.setTextSize(16);
		hint.setText(getResources().getString(R.string.username_hint));
		hint.setTextColor(getResources().getColor(R.color.gray));
		hint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams hint_lp = new LinearLayout.LayoutParams(-1, -2);
		hint_lp.leftMargin = dp(24f);
		hint_lp.rightMargin = dp(24f);
		this.addView(hint, hint_lp);
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