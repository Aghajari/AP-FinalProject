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

public class bot_fragment extends LinearLayout {

	public FrameLayout header;
	public TextView back;
	public LoadingButton progress;
	public TextView save;
	public TextView title;
	public AppCompatEditText api;

	protected CardView view1;
	protected LinearLayout view2;
	protected FrameLayout view3;
	protected TextView view4;

    public bot_fragment(Context context) {
        this(context, null);
    }

    public bot_fragment(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public bot_fragment(Context context, AttributeSet attrs, int defStyleAttr) {
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
		initView2();
		initView3();
		initApi();
		initView4();
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
		title.setText("Bot Api");
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
		view2 = new LinearLayout(getContext());
		view2.setOrientation(1);
		LinearLayout.LayoutParams view2_lp = new LinearLayout.LayoutParams(-1, -1);
		view2_lp.topMargin = dp(24f);
		this.addView(view2, view2_lp);
	}

	protected void initView3() {
		view3 = new FrameLayout(getContext());
		view3.setBackgroundResource(R.drawable.dark_rad);
		LinearLayout.LayoutParams view3_lp = new LinearLayout.LayoutParams(-1, dp(48f));
		view3_lp.leftMargin = dp(24f);
		view3_lp.rightMargin = dp(24f);
		view3_lp.bottomMargin = dp(12f);
		view2.addView(view3, view3_lp);
	}

	protected void initApi() {
		api = new AppCompatEditText(getContext());
		api.setImeOptions(0x00000006);
		api.setId(R.id.api);
		api.setTextSize(16);
		api.setSingleLine(true);
		api.setHint("Bot HTTP Api");
		api.setPadding(dp(16f), 0, dp(16f), 0);
		api.setTextColor(getResources().getColor(R.color.white));
		api.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		api.setBackground(null);
		api.setHintTextColor(getResources().getColor(R.color.gray));
		api.setGravity(Gravity.CENTER_VERTICAL);
		view3.addView(api, new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initView4() {
		view4 = new TextView(getContext());
		view4.setTextSize(12);
		view4.setText("Connect your account to a bot...");
		view4.setGravity(Gravity.LEFT);
		view4.setTextColor(getResources().getColor(R.color.gray));
		view4.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams view4_lp = new LinearLayout.LayoutParams(-1, -1);
		view4_lp.leftMargin = dp(28f);
		view4_lp.topMargin = dp(2f);
		view2.addView(view4, view4_lp);
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