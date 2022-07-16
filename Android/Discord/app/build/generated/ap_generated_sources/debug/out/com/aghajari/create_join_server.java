package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.aghajari.views.LoadingButton;

public class create_join_server extends LinearLayout {

	public FrameLayout header;
	public TextView back;
	public TextView title;
	public AppCompatEditText invite_code;
	public LoadingButton progress_join;
	public TextView done_join;
	public AppCompatImageView upload;
	public AppCompatEditText server_name;
	public LoadingButton progress_create;
	public TextView done_create;

	protected CardView view1;
	protected ScrollView view2;
	protected LinearLayout view3;
	protected FrameLayout view4;
	protected View view5;
	protected TextView view6;
	protected FrameLayout view7;
	protected TextView view8;
	protected FrameLayout view9;
	protected View view10;
	protected TextView view11;
	protected TextView view12;
	protected FrameLayout view13;
	protected TextView view14;

    public create_join_server(Context context) {
        this(context, null);
    }

    public create_join_server(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public create_join_server(Context context, AttributeSet attrs, int defStyleAttr) {
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
		initView3();
		initView4();
		initView5();
		initView6();
		initView7();
		initInvite_code();
		initProgress_join();
		initDone_join();
		initView8();
		initView9();
		initView10();
		initView11();
		initView12();
		initUpload();
		initView13();
		initServer_name();
		initProgress_create();
		initDone_create();
		initView14();
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
		back.setId(R.id.back);
		back.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackgroundBorderless));
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
		title.setText("Servers");
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
		view2 = new ScrollView(getContext());
		this.addView(view2, new LinearLayout.LayoutParams(-1, -1));
	}

	protected void initView3() {
		view3 = new LinearLayout(getContext());
		view3.setOrientation(1);
		view2.addView(view3, new ScrollView.LayoutParams(-1, -2));
	}

	protected void initView4() {
		view4 = new FrameLayout(getContext());
		LinearLayout.LayoutParams view4_lp = new LinearLayout.LayoutParams(-1, -2);
		view4_lp.setMargins(dp(24f), dp(24f), dp(24f), dp(12f));
		view3.addView(view4, view4_lp);
	}

	protected void initView5() {
		view5 = new View(getContext());
		view5.setBackgroundColor(getResources().getColor(R.color.background_dark2));
		FrameLayout.LayoutParams view5_lp = new FrameLayout.LayoutParams(-1, dp(1f));
		view5_lp.gravity = Gravity.CENTER;
		view4.addView(view5, view5_lp);
	}

	protected void initView6() {
		view6 = new TextView(getContext());
		view6.setBackgroundColor(getResources().getColor(R.color.background));
		view6.setText("JOIN A SERVER");
		view6.setTextSize(12);
		view6.setGravity(Gravity.CENTER);
		view6.setPadding(dp(12f), 0, dp(12f), 0);
		view6.setTextColor(getResources().getColor(R.color.white));
		view6.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams view6_lp = new FrameLayout.LayoutParams(-2, -2);
		view6_lp.leftMargin = dp(12f);
		view6_lp.gravity = Gravity.LEFT;
		view4.addView(view6, view6_lp);
	}

	protected void initView7() {
		view7 = new FrameLayout(getContext());
		view7.setBackgroundResource(R.drawable.dark_rad);
		LinearLayout.LayoutParams view7_lp = new LinearLayout.LayoutParams(-1, dp(48f));
		view7_lp.setMargins(dp(24f), dp(6f), dp(24f), dp(6f));
		view3.addView(view7, view7_lp);
	}

	protected void initInvite_code() {
		invite_code = new AppCompatEditText(getContext());
		invite_code.setImeOptions(0x00000006);
		invite_code.setId(R.id.invite_code);
		invite_code.setTextSize(16);
		invite_code.setSingleLine(true);
		invite_code.setHint("Invite Code");
		invite_code.setPadding(dp(16f), 0, dp(16f), 0);
		invite_code.setTextColor(getResources().getColor(R.color.white));
		invite_code.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		invite_code.setBackground(null);
		invite_code.setHintTextColor(getResources().getColor(R.color.gray));
		invite_code.setGravity(Gravity.CENTER_VERTICAL);
		FrameLayout.LayoutParams invite_code_lp = new FrameLayout.LayoutParams(-1, -1);
		invite_code_lp.rightMargin = dp(56f);
		view7.addView(invite_code, invite_code_lp);
	}

	protected void initProgress_join() {
		progress_join = new LoadingButton(getContext());
		progress_join.setScaleX(0);
		progress_join.setId(R.id.progress_join);
		progress_join.setBackgroundResource(R.drawable.circle3);
		progress_join.setScaleY(0);
		progress_join.setElevation(dp(4f));
		FrameLayout.LayoutParams progress_join_lp = new FrameLayout.LayoutParams(dp(32f), dp(32f));
		progress_join_lp.rightMargin = dp(12f);
		progress_join_lp.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
		view7.addView(progress_join, progress_join_lp);
	}

	protected void initDone_join() {
		done_join = new TextView(getContext());
		done_join.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		done_join.setId(R.id.done_join);
		done_join.setTextSize(20);
		done_join.setText("\ue876");
		done_join.setGravity(Gravity.CENTER);
		done_join.setTextColor(getResources().getColor(R.color.white));
		done_join.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		FrameLayout.LayoutParams done_join_lp = new FrameLayout.LayoutParams(-1, -1);
		done_join_lp.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
		progress_join.addView(done_join, done_join_lp);
	}

	protected void initView8() {
		view8 = new TextView(getContext());
		view8.setTextSize(12);
		view8.setText("Enter an invite code to join an existing server.");
		view8.setGravity(Gravity.LEFT);
		view8.setTextColor(getResources().getColor(R.color.gray));
		view8.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams view8_lp = new LinearLayout.LayoutParams(-1, -1);
		view8_lp.leftMargin = dp(28f);
		view8_lp.topMargin = dp(2f);
		view3.addView(view8, view8_lp);
	}

	protected void initView9() {
		view9 = new FrameLayout(getContext());
		LinearLayout.LayoutParams view9_lp = new LinearLayout.LayoutParams(-1, -2);
		view9_lp.setMargins(dp(24f), dp(32f), dp(24f), dp(12f));
		view3.addView(view9, view9_lp);
	}

	protected void initView10() {
		view10 = new View(getContext());
		view10.setBackgroundColor(getResources().getColor(R.color.background_dark2));
		FrameLayout.LayoutParams view10_lp = new FrameLayout.LayoutParams(-1, dp(1f));
		view10_lp.gravity = Gravity.CENTER;
		view9.addView(view10, view10_lp);
	}

	protected void initView11() {
		view11 = new TextView(getContext());
		view11.setBackgroundColor(getResources().getColor(R.color.background));
		view11.setText("CREATE A SERVER");
		view11.setTextSize(12);
		view11.setGravity(Gravity.CENTER);
		view11.setPadding(dp(12f), 0, dp(12f), 0);
		view11.setTextColor(getResources().getColor(R.color.white));
		view11.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams view11_lp = new FrameLayout.LayoutParams(-2, -2);
		view11_lp.leftMargin = dp(12f);
		view11_lp.gravity = Gravity.LEFT;
		view9.addView(view11, view11_lp);
	}

	protected void initView12() {
		view12 = new TextView(getContext());
		view12.setTextSize(12);
		view12.setText("Create your own server and invite your friends to join it.");
		view12.setGravity(Gravity.LEFT);
		view12.setTextColor(getResources().getColor(R.color.gray));
		view12.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams view12_lp = new LinearLayout.LayoutParams(-1, -1);
		view12_lp.leftMargin = dp(28f);
		view12_lp.topMargin = dp(2f);
		view12_lp.bottomMargin = dp(12f);
		view3.addView(view12, view12_lp);
	}

	protected void initUpload() {
		upload = new AppCompatImageView(getContext());
		upload.setId(R.id.upload);
		upload.setImageResource(R.drawable.upload);
		LinearLayout.LayoutParams upload_lp = new LinearLayout.LayoutParams(dp(85f), dp(85f));
		upload_lp.setMargins(dp(24f), dp(24f), dp(24f), dp(24f));
		upload_lp.gravity = Gravity.CENTER;
		view3.addView(upload, upload_lp);
	}

	protected void initView13() {
		view13 = new FrameLayout(getContext());
		view13.setBackgroundResource(R.drawable.dark_rad);
		LinearLayout.LayoutParams view13_lp = new LinearLayout.LayoutParams(-1, dp(48f));
		view13_lp.setMargins(dp(24f), dp(6f), dp(24f), dp(6f));
		view3.addView(view13, view13_lp);
	}

	protected void initServer_name() {
		server_name = new AppCompatEditText(getContext());
		server_name.setImeOptions(0x00000006);
		server_name.setId(R.id.server_name);
		server_name.setTextSize(16);
		server_name.setSingleLine(true);
		server_name.setHint("Server Name");
		server_name.setPadding(dp(16f), 0, dp(16f), 0);
		server_name.setTextColor(getResources().getColor(R.color.white));
		server_name.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		server_name.setBackground(null);
		server_name.setHintTextColor(getResources().getColor(R.color.gray));
		server_name.setGravity(Gravity.CENTER_VERTICAL);
		FrameLayout.LayoutParams server_name_lp = new FrameLayout.LayoutParams(-1, -1);
		server_name_lp.rightMargin = dp(56f);
		view13.addView(server_name, server_name_lp);
	}

	protected void initProgress_create() {
		progress_create = new LoadingButton(getContext());
		progress_create.setScaleX(0);
		progress_create.setId(R.id.progress_create);
		progress_create.setBackgroundResource(R.drawable.circle3);
		progress_create.setScaleY(0);
		progress_create.setElevation(dp(4f));
		FrameLayout.LayoutParams progress_create_lp = new FrameLayout.LayoutParams(dp(32f), dp(32f));
		progress_create_lp.rightMargin = dp(12f);
		progress_create_lp.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
		view13.addView(progress_create, progress_create_lp);
	}

	protected void initDone_create() {
		done_create = new TextView(getContext());
		done_create.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		done_create.setId(R.id.done_create);
		done_create.setTextSize(20);
		done_create.setText("\ue876");
		done_create.setGravity(Gravity.CENTER);
		done_create.setTextColor(getResources().getColor(R.color.white));
		done_create.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		FrameLayout.LayoutParams done_create_lp = new FrameLayout.LayoutParams(-1, -1);
		done_create_lp.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
		progress_create.addView(done_create, done_create_lp);
	}

	protected void initView14() {
		view14 = new TextView(getContext());
		view14.setTextSize(10);
		view14.setText("By creating a server, you agree to Discord's Community Guidelines");
		view14.setGravity(Gravity.LEFT);
		view14.setTextColor(getResources().getColor(R.color.gray));
		view14.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		LinearLayout.LayoutParams view14_lp = new LinearLayout.LayoutParams(-1, -1);
		view14_lp.leftMargin = dp(28f);
		view14_lp.topMargin = dp(4f);
		view14_lp.bottomMargin = dp(80f);
		view3.addView(view14, view14_lp);
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