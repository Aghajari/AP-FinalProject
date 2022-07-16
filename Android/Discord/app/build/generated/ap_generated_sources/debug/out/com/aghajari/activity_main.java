package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import com.aghajari.views.LoadingButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class activity_main extends FrameLayout {

	public AppCompatImageView background;
	public LinearLayout loading;
	public CircularProgressIndicator progress;
	public FrameLayout sign;
	public FrameLayout main_content;
	public TextView sign_in_text;
	public TextView sign_up_text;
	public View selector;
	public LinearLayout sign_in_content;
	public AppCompatEditText login_name;
	public AppCompatEditText login_pass;
	public LoadingButton login_btn;
	public AppCompatTextView login;
	public LinearLayout sign_up_content;
	public AppCompatEditText signup_email;
	public AppCompatEditText signup_name;
	public AppCompatEditText signup_pass;
	public LoadingButton signup_btn;
	public AppCompatTextView signup;
	public FrameLayout username_content;
	public AppCompatEditText username_edt;
	public AppCompatTextView skip_username;
	public LoadingButton next_username_btn;
	public AppCompatTextView next_username;
	public FrameLayout profile_content;
	public AppCompatImageView profile_img;
	public LoadingButton next_profile_btn;
	public AppCompatTextView next_profile;

	protected TextView view1;
	protected LinearLayout view2;
	protected TextView view3;
	protected TextView view4;
	protected LinearLayout view5;
	protected LoadingButton view6;
	protected TextView view7;
	protected TextView view8;

    public activity_main(Context context) {
        this(context, null);
    }

    public activity_main(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public activity_main(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initBackground();
		initLoading();
		initProgress();
		initView1();
		initSign();
		initMain_content();
		initView2();
		initSign_in_text();
		initSign_up_text();
		initSelector();
		initSign_in_content();
		initLogin_name();
		initLogin_pass();
		initLogin_btn();
		initLogin();
		initSign_up_content();
		initSignup_email();
		initSignup_name();
		initSignup_pass();
		initSignup_btn();
		initSignup();
		initUsername_content();
		initView3();
		initUsername_edt();
		initView4();
		initView5();
		initView6();
		initSkip_username();
		initNext_username_btn();
		initNext_username();
		initProfile_content();
		initView7();
		initProfile_img();
		initView8();
		initNext_profile_btn();
		initNext_profile();
	}

	protected void initThis() {
		this.setBackgroundColor(getResources().getColor(R.color.background_dark));
		this.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initBackground() {
		background = new AppCompatImageView(getContext());
		background.setId(R.id.background);
		this.addView(background, new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initLoading() {
		loading = new LinearLayout(getContext());
		loading.setBackgroundResource(R.drawable.black_alpha);
		loading.setId(R.id.loading);
		loading.setGravity(Gravity.CENTER);
		loading.setOrientation(1);
		FrameLayout.LayoutParams loading_lp = new FrameLayout.LayoutParams(dp(300f), dp(200f));
		loading_lp.gravity = Gravity.CENTER;
		this.addView(loading, loading_lp);
	}

	protected void initProgress() {
		progress = new CircularProgressIndicator(getContext());
		progress.setId(R.id.progress);
		progress.setIndeterminate(true);
		loading.addView(progress, new LinearLayout.LayoutParams(dp(56f), dp(56f)));
	}

	protected void initView1() {
		view1 = new TextView(getContext());
		view1.setTextSize(24);
		view1.setText("Discord");
		view1.setTextColor(getResources().getColor(R.color.white));
		view1.setTypeface(ResourcesCompat.getFont(getContext(), R.font.semi_bold));
		LinearLayout.LayoutParams view1_lp = new LinearLayout.LayoutParams(-2, -2);
		view1_lp.topMargin = dp(16f);
		loading.addView(view1, view1_lp);
	}

	protected void initSign() {
		sign = new FrameLayout(getContext());
		sign.setBackgroundResource(R.drawable.black_alpha);
		sign.setId(R.id.sign);
		sign.setVisibility(View.GONE);
		FrameLayout.LayoutParams sign_lp = new FrameLayout.LayoutParams(dp(350f), dp(450f));
		sign_lp.gravity = Gravity.CENTER;
		this.addView(sign, sign_lp);
	}

	protected void initMain_content() {
		main_content = new FrameLayout(getContext());
		main_content.setId(R.id.main_content);
		sign.addView(main_content, new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initView2() {
		view2 = new LinearLayout(getContext());
		view2.setOrientation(0);
		main_content.addView(view2, new FrameLayout.LayoutParams(-1, dp(56f)));
	}

	protected void initSign_in_text() {
		sign_in_text = new TextView(getContext());
		sign_in_text.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		sign_in_text.setId(R.id.sign_in_text);
		sign_in_text.setTextSize(18);
		sign_in_text.setText("Sign In");
		sign_in_text.setGravity(Gravity.CENTER);
		sign_in_text.setTextColor(getResources().getColor(R.color.color2));
		sign_in_text.setTypeface(ResourcesCompat.getFont(getContext(), R.font.semi_bold));
		LinearLayout.LayoutParams sign_in_text_lp = new LinearLayout.LayoutParams(dp(0f), -1);
		sign_in_text_lp.weight = (float) 0.5f;
		view2.addView(sign_in_text, sign_in_text_lp);
	}

	protected void initSign_up_text() {
		sign_up_text = new TextView(getContext());
		sign_up_text.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		sign_up_text.setId(R.id.sign_up_text);
		sign_up_text.setTextSize(18);
		sign_up_text.setText("Sign Up");
		sign_up_text.setGravity(Gravity.CENTER);
		sign_up_text.setTextColor(getResources().getColor(R.color.white));
		sign_up_text.setTypeface(ResourcesCompat.getFont(getContext(), R.font.semi_bold));
		LinearLayout.LayoutParams sign_up_text_lp = new LinearLayout.LayoutParams(dp(0f), -1);
		sign_up_text_lp.weight = (float) 0.5f;
		view2.addView(sign_up_text, sign_up_text_lp);
	}

	protected void initSelector() {
		selector = new View(getContext());
		selector.setBackgroundResource(R.drawable.gradient);
		selector.setId(R.id.selector);
		FrameLayout.LayoutParams selector_lp = new FrameLayout.LayoutParams(dp(175f), dp(2f));
		selector_lp.topMargin = dp(56f);
		main_content.addView(selector, selector_lp);
	}

	protected void initSign_in_content() {
		sign_in_content = new LinearLayout(getContext());
		sign_in_content.setId(R.id.sign_in_content);
		sign_in_content.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		sign_in_content.setOrientation(1);
		FrameLayout.LayoutParams sign_in_content_lp = new FrameLayout.LayoutParams(-1, -1);
		sign_in_content_lp.topMargin = dp(58f);
		main_content.addView(sign_in_content, sign_in_content_lp);
	}

	protected void initLogin_name() {
		login_name = new AppCompatEditText(getContext());
		login_name.setId(R.id.login_name);
		login_name.setHintTextColor(getResources().getColor(R.color.gray));
		login_name.setTextSize(16);
		login_name.setSingleLine(true);
		login_name.setHint("Email or Username");
		login_name.setTextColor(getResources().getColor(R.color.color2));
		login_name.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams login_name_lp = new LinearLayout.LayoutParams(-1, dp(56f));
		login_name_lp.leftMargin = dp(24f);
		login_name_lp.topMargin = dp(24f);
		login_name_lp.rightMargin = dp(24f);
		sign_in_content.addView(login_name, login_name_lp);
	}

	protected void initLogin_pass() {
		login_pass = new AppCompatEditText(getContext());
		login_pass.setImeOptions(0x00000006);
		login_pass.setId(R.id.login_pass);
		login_pass.setTextSize(16);
		login_pass.setSingleLine(true);
		login_pass.setHint("Password");
		login_pass.setTextColor(getResources().getColor(R.color.color2));
		login_pass.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		login_pass.setInputType(0x00000081);
		login_pass.setHintTextColor(getResources().getColor(R.color.gray));
		LinearLayout.LayoutParams login_pass_lp = new LinearLayout.LayoutParams(-1, dp(56f));
		login_pass_lp.leftMargin = dp(24f);
		login_pass_lp.topMargin = dp(16f);
		login_pass_lp.rightMargin = dp(24f);
		sign_in_content.addView(login_pass, login_pass_lp);
	}

	protected void initLogin_btn() {
		login_btn = new LoadingButton(getContext());
		login_btn.setBackgroundResource(R.drawable.gradient_rad);
		login_btn.setId(R.id.login_btn);
		LinearLayout.LayoutParams login_btn_lp = new LinearLayout.LayoutParams(dp(140f), dp(50f));
		login_btn_lp.topMargin = dp(150f);
		sign_in_content.addView(login_btn, login_btn_lp);
	}

	protected void initLogin() {
		login = new AppCompatTextView(getContext());
		login.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		login.setId(R.id.login);
		login.setTextSize(18);
		login.setText("Sign In");
		login.setGravity(Gravity.CENTER);
		login.setTextColor(getResources().getColor(R.color.white));
		login.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams login_lp = new FrameLayout.LayoutParams(-1, -1);
		login_lp.gravity = Gravity.CENTER;
		login_btn.addView(login, login_lp);
	}

	protected void initSign_up_content() {
		sign_up_content = new LinearLayout(getContext());
		sign_up_content.setId(R.id.sign_up_content);
		sign_up_content.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		sign_up_content.setOrientation(1);
		sign_up_content.setTranslationX(dp(350f));
		FrameLayout.LayoutParams sign_up_content_lp = new FrameLayout.LayoutParams(-1, -1);
		sign_up_content_lp.topMargin = dp(58f);
		main_content.addView(sign_up_content, sign_up_content_lp);
	}

	protected void initSignup_email() {
		signup_email = new AppCompatEditText(getContext());
		signup_email.setId(R.id.signup_email);
		signup_email.setTextSize(16);
		signup_email.setSingleLine(true);
		signup_email.setHint("Email");
		signup_email.setNextFocusDownId(R.id.signup_name);
		signup_email.setTextColor(getResources().getColor(R.color.color2));
		signup_email.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		signup_email.setHintTextColor(getResources().getColor(R.color.gray));
		LinearLayout.LayoutParams signup_email_lp = new LinearLayout.LayoutParams(-1, dp(56f));
		signup_email_lp.leftMargin = dp(24f);
		signup_email_lp.topMargin = dp(24f);
		signup_email_lp.rightMargin = dp(24f);
		sign_up_content.addView(signup_email, signup_email_lp);
	}

	protected void initSignup_name() {
		signup_name = new AppCompatEditText(getContext());
		signup_name.setId(R.id.signup_name);
		signup_name.setHintTextColor(getResources().getColor(R.color.gray));
		signup_name.setTextSize(16);
		signup_name.setSingleLine(true);
		signup_name.setHint("Full Name");
		signup_name.setTextColor(getResources().getColor(R.color.color2));
		signup_name.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams signup_name_lp = new LinearLayout.LayoutParams(-1, dp(56f));
		signup_name_lp.leftMargin = dp(24f);
		signup_name_lp.topMargin = dp(24f);
		signup_name_lp.rightMargin = dp(24f);
		sign_up_content.addView(signup_name, signup_name_lp);
	}

	protected void initSignup_pass() {
		signup_pass = new AppCompatEditText(getContext());
		signup_pass.setImeOptions(0x00000006);
		signup_pass.setId(R.id.signup_pass);
		signup_pass.setTextSize(16);
		signup_pass.setSingleLine(true);
		signup_pass.setHint("Password");
		signup_pass.setTextColor(getResources().getColor(R.color.color2));
		signup_pass.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		signup_pass.setInputType(0x00000081);
		signup_pass.setHintTextColor(getResources().getColor(R.color.gray));
		LinearLayout.LayoutParams signup_pass_lp = new LinearLayout.LayoutParams(-1, dp(56f));
		signup_pass_lp.leftMargin = dp(24f);
		signup_pass_lp.topMargin = dp(16f);
		signup_pass_lp.rightMargin = dp(24f);
		sign_up_content.addView(signup_pass, signup_pass_lp);
	}

	protected void initSignup_btn() {
		signup_btn = new LoadingButton(getContext());
		signup_btn.setBackgroundResource(R.drawable.gradient_rad);
		signup_btn.setId(R.id.signup_btn);
		LinearLayout.LayoutParams signup_btn_lp = new LinearLayout.LayoutParams(dp(140f), dp(50f));
		signup_btn_lp.topMargin = dp(70f);
		sign_up_content.addView(signup_btn, signup_btn_lp);
	}

	protected void initSignup() {
		signup = new AppCompatTextView(getContext());
		signup.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		signup.setId(R.id.signup);
		signup.setTextSize(18);
		signup.setText("Next");
		signup.setGravity(Gravity.CENTER);
		signup.setTextColor(getResources().getColor(R.color.white));
		signup.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams signup_lp = new FrameLayout.LayoutParams(-1, -1);
		signup_lp.gravity = Gravity.CENTER;
		signup_btn.addView(signup, signup_lp);
	}

	protected void initUsername_content() {
		username_content = new FrameLayout(getContext());
		username_content.setId(R.id.username_content);
		username_content.setTranslationX(dp(350f));
		sign.addView(username_content, new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initView3() {
		view3 = new TextView(getContext());
		view3.setTextSize(22);
		view3.setText("Welcome");
		view3.setTextColor(getResources().getColor(R.color.color2));
		view3.setTypeface(ResourcesCompat.getFont(getContext(), R.font.semi_bold));
		FrameLayout.LayoutParams view3_lp = new FrameLayout.LayoutParams(-2, -2);
		view3_lp.leftMargin = dp(24f);
		view3_lp.topMargin = dp(24f);
		username_content.addView(view3, view3_lp);
	}

	protected void initUsername_edt() {
		username_edt = new AppCompatEditText(getContext());
		username_edt.setImeOptions(0x00000006);
		username_edt.setId(R.id.username_edt);
		username_edt.setTextSize(16);
		username_edt.setSingleLine(true);
		username_edt.setHint("Username");
		username_edt.setTextColor(getResources().getColor(R.color.color2));
		username_edt.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		username_edt.setHintTextColor(getResources().getColor(R.color.gray));
		FrameLayout.LayoutParams username_edt_lp = new FrameLayout.LayoutParams(-1, dp(56f));
		username_edt_lp.leftMargin = dp(24f);
		username_edt_lp.topMargin = dp(80f);
		username_edt_lp.rightMargin = dp(24f);
		username_content.addView(username_edt, username_edt_lp);
	}

	protected void initView4() {
		view4 = new TextView(getContext());
		view4.setTextSize(16);
		view4.setText(getResources().getString(R.string.username_hint));
		view4.setTextColor(getResources().getColor(R.color.white));
		view4.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams view4_lp = new FrameLayout.LayoutParams(-1, -2);
		view4_lp.leftMargin = dp(24f);
		view4_lp.topMargin = dp(148f);
		view4_lp.rightMargin = dp(24f);
		username_content.addView(view4, view4_lp);
	}

	protected void initView5() {
		view5 = new LinearLayout(getContext());
		view5.setGravity(Gravity.CENTER);
		view5.setOrientation(0);
		FrameLayout.LayoutParams view5_lp = new FrameLayout.LayoutParams(-1, -2);
		view5_lp.bottomMargin = dp(48f);
		view5_lp.gravity = Gravity.BOTTOM;
		username_content.addView(view5, view5_lp);
	}

	protected void initView6() {
		view6 = new LoadingButton(getContext());
		view6.setBackgroundResource(R.drawable.gradient2_rad);
		LinearLayout.LayoutParams view6_lp = new LinearLayout.LayoutParams(dp(140f), dp(50f));
		view6_lp.rightMargin = dp(12f);
		view5.addView(view6, view6_lp);
	}

	protected void initSkip_username() {
		skip_username = new AppCompatTextView(getContext());
		skip_username.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		skip_username.setId(R.id.skip_username);
		skip_username.setTextSize(18);
		skip_username.setText("Skip");
		skip_username.setGravity(Gravity.CENTER);
		skip_username.setTextColor(getResources().getColor(R.color.white));
		skip_username.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams skip_username_lp = new FrameLayout.LayoutParams(-1, -1);
		skip_username_lp.gravity = Gravity.CENTER;
		view6.addView(skip_username, skip_username_lp);
	}

	protected void initNext_username_btn() {
		next_username_btn = new LoadingButton(getContext());
		next_username_btn.setBackgroundResource(R.drawable.gradient_rad);
		next_username_btn.setId(R.id.next_username_btn);
		LinearLayout.LayoutParams next_username_btn_lp = new LinearLayout.LayoutParams(dp(140f), dp(50f));
		next_username_btn_lp.leftMargin = dp(12f);
		view5.addView(next_username_btn, next_username_btn_lp);
	}

	protected void initNext_username() {
		next_username = new AppCompatTextView(getContext());
		next_username.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		next_username.setId(R.id.next_username);
		next_username.setTextSize(18);
		next_username.setText("Next");
		next_username.setGravity(Gravity.CENTER);
		next_username.setTextColor(getResources().getColor(R.color.white));
		next_username.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams next_username_lp = new FrameLayout.LayoutParams(-1, -1);
		next_username_lp.gravity = Gravity.CENTER;
		next_username_btn.addView(next_username, next_username_lp);
	}

	protected void initProfile_content() {
		profile_content = new FrameLayout(getContext());
		profile_content.setId(R.id.profile_content);
		profile_content.setTranslationX(dp(350f));
		sign.addView(profile_content, new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initView7() {
		view7 = new TextView(getContext());
		view7.setTextSize(22);
		view7.setText("Profile");
		view7.setTextColor(getResources().getColor(R.color.color2));
		view7.setTypeface(ResourcesCompat.getFont(getContext(), R.font.semi_bold));
		FrameLayout.LayoutParams view7_lp = new FrameLayout.LayoutParams(-2, -2);
		view7_lp.leftMargin = dp(24f);
		view7_lp.topMargin = dp(24f);
		profile_content.addView(view7, view7_lp);
	}

	protected void initProfile_img() {
		profile_img = new AppCompatImageView(getContext());
		profile_img.setId(R.id.profile_img);
		FrameLayout.LayoutParams profile_img_lp = new FrameLayout.LayoutParams(dp(100f), dp(100f));
		profile_img_lp.leftMargin = dp(24f);
		profile_img_lp.topMargin = dp(80f);
		profile_img_lp.rightMargin = dp(24f);
		profile_img_lp.gravity = Gravity.CENTER_HORIZONTAL;
		profile_content.addView(profile_img, profile_img_lp);
	}

	protected void initView8() {
		view8 = new TextView(getContext());
		view8.setTextSize(16);
		view8.setText(getResources().getString(R.string.profile_hint));
		view8.setTextColor(getResources().getColor(R.color.white));
		view8.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams view8_lp = new FrameLayout.LayoutParams(-1, -2);
		view8_lp.leftMargin = dp(24f);
		view8_lp.topMargin = dp(210f);
		view8_lp.rightMargin = dp(24f);
		profile_content.addView(view8, view8_lp);
	}

	protected void initNext_profile_btn() {
		next_profile_btn = new LoadingButton(getContext());
		next_profile_btn.setBackgroundResource(R.drawable.gradient2_rad);
		next_profile_btn.setId(R.id.next_profile_btn);
		FrameLayout.LayoutParams next_profile_btn_lp = new FrameLayout.LayoutParams(dp(140f), dp(50f));
		next_profile_btn_lp.bottomMargin = dp(48f);
		next_profile_btn_lp.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
		profile_content.addView(next_profile_btn, next_profile_btn_lp);
	}

	protected void initNext_profile() {
		next_profile = new AppCompatTextView(getContext());
		next_profile.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		next_profile.setId(R.id.next_profile);
		next_profile.setTextSize(18);
		next_profile.setText("Sign Up");
		next_profile.setGravity(Gravity.CENTER);
		next_profile.setTextColor(getResources().getColor(R.color.white));
		next_profile.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams next_profile_lp = new FrameLayout.LayoutParams(-1, -1);
		next_profile_lp.gravity = Gravity.CENTER;
		next_profile_btn.addView(next_profile, next_profile_lp);
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