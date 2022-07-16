package com.aghajari;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
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
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.axanimation.layouts.AXAFrameLayout;
import com.aghajari.views.ClipFrameLayout;
import com.aghajari.views.LoadingButton;

public class profile_fragment extends FrameLayout {

	public AXAFrameLayout parallax_header;
	public AppCompatImageView header;
	public AppCompatImageView profile;
	public ClipFrameLayout profile_parent;
	public TextView profile_click;
	public ClipFrameLayout chat_parent;
	public TextView chat_click;
	public ClipFrameLayout online;
	public TextView name;
	public TextView subtitle;
	public FrameLayout profile_picker;
	public TextView cancel;
	public TextView edit;
	public LoadingButton progress;
	public TextView apply;
	public TextView profile_hint;
	public FrameLayout request_friend;
	public TextView request_friend_hint;
	public TextView send_friend_request;
	public FrameLayout cancel_request_friend;
	public TextView cancel_request_friend_hint;
	public TextView cancel_friend_request;
	public FrameLayout is_friend;
	public TextView is_friend_hint;
	public TextView remove_friend;
	public FrameLayout ask_request_friend;
	public TextView ask_request_friend_hint;
	public TextView reject_friend_request;
	public TextView accept_friend_request;
	public View rv_bg;
	public RecyclerView rv;
	public RecyclerView chat_rv;
	public FrameLayout chat_footer;
	public LoadingButton send_parent;
	public View send;
	public AppCompatEditText message_edt;
	public ClipFrameLayout access_panel;
	public AppCompatTextView access;

	protected FrameLayout view1;
	protected AppCompatImageView view2;
	protected View view3;
	protected LinearLayout view4;
	protected ClipFrameLayout view5;
	protected ClipFrameLayout view6;
	protected ClipFrameLayout view7;
	protected ClipFrameLayout view8;
	protected ClipFrameLayout view9;
	protected LinearLayout view10;
	protected ClipFrameLayout view11;
	protected ClipFrameLayout view12;
	protected View view13;
	protected AppCompatImageView view14;
	protected FrameLayout view15;

    public profile_fragment(Context context) {
        this(context, null);
    }

    public profile_fragment(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public profile_fragment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initParallax_header();
		initHeader();
		initView1();
		initView2();
		initProfile();
		initProfile_parent();
		initProfile_click();
		initChat_parent();
		initChat_click();
		initOnline();
		initView3();
		initName();
		initSubtitle();
		initProfile_picker();
		initView4();
		initView5();
		initCancel();
		initView6();
		initEdit();
		initProgress();
		initApply();
		initProfile_hint();
		initRequest_friend();
		initRequest_friend_hint();
		initView7();
		initSend_friend_request();
		initCancel_request_friend();
		initCancel_request_friend_hint();
		initView8();
		initCancel_friend_request();
		initIs_friend();
		initIs_friend_hint();
		initView9();
		initRemove_friend();
		initAsk_request_friend();
		initAsk_request_friend_hint();
		initView10();
		initView11();
		initReject_friend_request();
		initView12();
		initAccept_friend_request();
		initRv_bg();
		initRv();
		initChat_rv();
		initChat_footer();
		initView13();
		initSend_parent();
		initView14();
		initSend();
		initView15();
		initMessage_edt();
		initAccess_panel();
		initAccess();
	}

	protected void initThis() {
		this.setBackgroundColor(getResources().getColor(R.color.background_dark2));
		this.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initParallax_header() {
		parallax_header = new AXAFrameLayout(getContext());
		parallax_header.setId(R.id.parallax_header);
		parallax_header.setElevation(dp(10f));
		this.addView(parallax_header, new FrameLayout.LayoutParams(-1, dp(324f)));
	}

	protected void initHeader() {
		header = new AppCompatImageView(getContext());
		header.setId(R.id.header);
		parallax_header.addView(header, new AXAFrameLayout.LayoutParams(-1, dp(200f)));
	}

	protected void initView1() {
		view1 = new FrameLayout(getContext());
		AXAFrameLayout.LayoutParams view1_lp = new AXAFrameLayout.LayoutParams(dp(90f), dp(90f));
		view1_lp.leftMargin = dp(20f);
		view1_lp.topMargin = dp(155f);
		parallax_header.addView(view1, view1_lp);
	}

	protected void initView2() {
		view2 = new AppCompatImageView(getContext());
		view2.setImageResource(R.drawable.circle);
		view1.addView(view2, new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initProfile() {
		profile = new AppCompatImageView(getContext());
		profile.setId(R.id.profile);
		FrameLayout.LayoutParams profile_lp = new FrameLayout.LayoutParams(dp(82f), dp(82f));
		profile_lp.gravity = Gravity.CENTER;
		view1.addView(profile, profile_lp);
	}

	protected void initProfile_parent() {
		profile_parent = new ClipFrameLayout(getContext());
		profile_parent.setBackgroundResource(R.drawable.circle);
		profile_parent.setId(R.id.profile_parent);
		FrameLayout.LayoutParams profile_parent_lp = new FrameLayout.LayoutParams(dp(42f), dp(42f));
		profile_parent_lp.topMargin = dp(179f);
		profile_parent_lp.rightMargin = dp(24f);
		profile_parent_lp.gravity = Gravity.RIGHT;
		parallax_header.addView(profile_parent, profile_parent_lp);
	}

	protected void initProfile_click() {
		profile_click = new TextView(getContext());
		profile_click.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		profile_click.setId(R.id.profile_click);
		profile_click.setText("\ue439");
		profile_click.setTextSize(22);
		profile_click.setGravity(Gravity.CENTER);
		profile_click.setTag("profile");
		profile_click.setTextColor(getResources().getColor(R.color.white));
		profile_click.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		profile_parent.addView(profile_click, new ClipFrameLayout.LayoutParams(-1, -1));
	}

	protected void initChat_parent() {
		chat_parent = new ClipFrameLayout(getContext());
		chat_parent.setBackgroundResource(R.drawable.circle);
		chat_parent.setId(R.id.chat_parent);
		FrameLayout.LayoutParams chat_parent_lp = new FrameLayout.LayoutParams(dp(42f), dp(42f));
		chat_parent_lp.topMargin = dp(179f);
		chat_parent_lp.rightMargin = dp(90f);
		chat_parent_lp.gravity = Gravity.RIGHT;
		parallax_header.addView(chat_parent, chat_parent_lp);
	}

	protected void initChat_click() {
		chat_click = new TextView(getContext());
		chat_click.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		chat_click.setId(R.id.chat_click);
		chat_click.setText("\ue0ca");
		chat_click.setTextSize(22);
		chat_click.setGravity(Gravity.CENTER);
		chat_click.setTag("chat");
		chat_click.setTextColor(getResources().getColor(R.color.white));
		chat_click.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		chat_parent.addView(chat_click, new ClipFrameLayout.LayoutParams(-1, -1));
	}

	protected void initOnline() {
		online = new ClipFrameLayout(getContext());
		online.setBackgroundResource(R.drawable.circle);
		online.setId(R.id.online);
		online.setVisibility(View.GONE);
		AXAFrameLayout.LayoutParams online_lp = new AXAFrameLayout.LayoutParams(dp(25f), dp(25f));
		online_lp.leftMargin = dp(80f);
		online_lp.topMargin = dp(216f);
		parallax_header.addView(online, online_lp);
	}

	protected void initView3() {
		view3 = new View(getContext());
		view3.setBackgroundResource(R.drawable.green_circle);
		FrameLayout.LayoutParams view3_lp = new FrameLayout.LayoutParams(-1, -1);
		view3_lp.setMargins(dp(5f), dp(5f), dp(5f), dp(5f));
		view3_lp.gravity = Gravity.CENTER;
		online.addView(view3, view3_lp);
	}

	protected void initName() {
		name = new TextView(getContext());
		name.setId(R.id.name);
		name.setTextSize(24);
		name.setEllipsize(TextUtils.TruncateAt.END);
		name.setSingleLine(true);
		name.setGravity(Gravity.LEFT);
		name.setTextColor(getResources().getColor(R.color.white));
		name.setTypeface(ResourcesCompat.getFont(getContext(), R.font.semi_bold));
		AXAFrameLayout.LayoutParams name_lp = new AXAFrameLayout.LayoutParams(-1, dp(36f));
		name_lp.leftMargin = dp(24f);
		name_lp.topMargin = dp(250f);
		name_lp.rightMargin = dp(24f);
		parallax_header.addView(name, name_lp);
	}

	protected void initSubtitle() {
		subtitle = new TextView(getContext());
		subtitle.setId(R.id.subtitle);
		subtitle.setTextSize(20);
		subtitle.setSingleLine(true);
		subtitle.setAlpha(0);
		subtitle.setTextColor(getResources().getColor(R.color.white));
		subtitle.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		subtitle.setEllipsize(TextUtils.TruncateAt.END);
		subtitle.setGravity(Gravity.LEFT);
		AXAFrameLayout.LayoutParams subtitle_lp = new AXAFrameLayout.LayoutParams(-1, dp(36f));
		subtitle_lp.leftMargin = dp(24f);
		subtitle_lp.topMargin = dp(286f);
		subtitle_lp.rightMargin = dp(24f);
		parallax_header.addView(subtitle, subtitle_lp);
	}

	protected void initProfile_picker() {
		profile_picker = new FrameLayout(getContext());
		profile_picker.setId(R.id.profile_picker);
		profile_picker.setVisibility(View.GONE);
		this.addView(profile_picker, new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initView4() {
		view4 = new LinearLayout(getContext());
		view4.setOrientation(0);
		FrameLayout.LayoutParams view4_lp = new FrameLayout.LayoutParams(-2, -2);
		view4_lp.topMargin = dp(362f);
		view4_lp.gravity = Gravity.CENTER_HORIZONTAL;
		profile_picker.addView(view4, view4_lp);
	}

	protected void initView5() {
		view5 = new ClipFrameLayout(getContext());
		view5.setScaleX(0);
		view5.setBackgroundResource(R.drawable.circle2);
		view5.setScaleY(0);
		view5.setAlpha(0);
		LinearLayout.LayoutParams view5_lp = new LinearLayout.LayoutParams(dp(56f), dp(56f));
		view5_lp.rightMargin = dp(12f);
		view4.addView(view5, view5_lp);
	}

	protected void initCancel() {
		cancel = new TextView(getContext());
		cancel.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		cancel.setId(R.id.cancel);
		cancel.setText("\ue5cd");
		cancel.setTextSize(25);
		cancel.setGravity(Gravity.CENTER);
		cancel.setTextColor(getResources().getColor(R.color.white));
		cancel.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		FrameLayout.LayoutParams cancel_lp = new FrameLayout.LayoutParams(-1, -1);
		cancel_lp.gravity = Gravity.CENTER;
		view5.addView(cancel, cancel_lp);
	}

	protected void initView6() {
		view6 = new ClipFrameLayout(getContext());
		view6.setScaleX(0);
		view6.setBackgroundResource(R.drawable.circle2);
		view6.setScaleY(0);
		view6.setAlpha(0);
		LinearLayout.LayoutParams view6_lp = new LinearLayout.LayoutParams(dp(56f), dp(56f));
		view6_lp.leftMargin = dp(12f);
		view6_lp.rightMargin = dp(12f);
		view4.addView(view6, view6_lp);
	}

	protected void initEdit() {
		edit = new TextView(getContext());
		edit.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		edit.setId(R.id.edit);
		edit.setText("\ue439");
		edit.setTextSize(25);
		edit.setGravity(Gravity.CENTER);
		edit.setTextColor(getResources().getColor(R.color.white));
		edit.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		FrameLayout.LayoutParams edit_lp = new FrameLayout.LayoutParams(-1, -1);
		edit_lp.gravity = Gravity.CENTER;
		view6.addView(edit, edit_lp);
	}

	protected void initProgress() {
		progress = new LoadingButton(getContext());
		progress.setScaleX(0);
		progress.setBackgroundResource(R.drawable.circle2);
		progress.setId(R.id.progress);
		progress.setScaleY(0);
		progress.setAlpha(0);
		LinearLayout.LayoutParams progress_lp = new LinearLayout.LayoutParams(dp(56f), dp(56f));
		progress_lp.leftMargin = dp(12f);
		view4.addView(progress, progress_lp);
	}

	protected void initApply() {
		apply = new TextView(getContext());
		apply.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		apply.setId(R.id.apply);
		apply.setText("\ue876");
		apply.setTextSize(25);
		apply.setGravity(Gravity.CENTER);
		apply.setTextColor(getResources().getColor(R.color.white));
		apply.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		FrameLayout.LayoutParams apply_lp = new FrameLayout.LayoutParams(-1, -1);
		apply_lp.gravity = Gravity.CENTER;
		progress.addView(apply, apply_lp);
	}

	protected void initProfile_hint() {
		profile_hint = new TextView(getContext());
		profile_hint.setId(R.id.profile_hint);
		profile_hint.setTextSize(16);
		profile_hint.setText(getResources().getString(R.string.profile_hint2));
		profile_hint.setGravity(Gravity.CENTER);
		profile_hint.setAlpha(0);
		profile_hint.setTextColor(getResources().getColor(R.color.white));
		profile_hint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams profile_hint_lp = new FrameLayout.LayoutParams(-1, -2);
		profile_hint_lp.leftMargin = dp(24f);
		profile_hint_lp.topMargin = dp(438f);
		profile_hint_lp.rightMargin = dp(24f);
		profile_picker.addView(profile_hint, profile_hint_lp);
	}

	protected void initRequest_friend() {
		request_friend = new FrameLayout(getContext());
		request_friend.setId(R.id.request_friend);
		request_friend.setVisibility(View.GONE);
		this.addView(request_friend, new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initRequest_friend_hint() {
		request_friend_hint = new TextView(getContext());
		request_friend_hint.setId(R.id.request_friend_hint);
		request_friend_hint.setTextSize(16);
		request_friend_hint.setText("Do you want to send a friend request to X ?");
		request_friend_hint.setGravity(Gravity.CENTER);
		request_friend_hint.setAlpha(0);
		request_friend_hint.setTextColor(getResources().getColor(R.color.white));
		request_friend_hint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams request_friend_hint_lp = new FrameLayout.LayoutParams(-1, -2);
		request_friend_hint_lp.leftMargin = dp(24f);
		request_friend_hint_lp.topMargin = dp(362f);
		request_friend_hint_lp.rightMargin = dp(24f);
		request_friend.addView(request_friend_hint, request_friend_hint_lp);
	}

	protected void initView7() {
		view7 = new ClipFrameLayout(getContext());
		view7.setScaleX(0);
		view7.setBackgroundResource(R.drawable.circle2);
		view7.setScaleY(0);
		view7.setAlpha(0);
		FrameLayout.LayoutParams view7_lp = new FrameLayout.LayoutParams(dp(250f), dp(48f));
		view7_lp.topMargin = dp(438f);
		view7_lp.gravity = Gravity.CENTER_HORIZONTAL;
		request_friend.addView(view7, view7_lp);
	}

	protected void initSend_friend_request() {
		send_friend_request = new TextView(getContext());
		send_friend_request.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		send_friend_request.setId(R.id.send_friend_request);
		send_friend_request.setText("Send Friend Request");
		send_friend_request.setTextSize(16);
		send_friend_request.setGravity(Gravity.CENTER);
		send_friend_request.setTextColor(getResources().getColor(R.color.white));
		send_friend_request.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams send_friend_request_lp = new FrameLayout.LayoutParams(-1, -1);
		send_friend_request_lp.gravity = Gravity.CENTER;
		view7.addView(send_friend_request, send_friend_request_lp);
	}

	protected void initCancel_request_friend() {
		cancel_request_friend = new FrameLayout(getContext());
		cancel_request_friend.setId(R.id.cancel_request_friend);
		cancel_request_friend.setVisibility(View.GONE);
		this.addView(cancel_request_friend, new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initCancel_request_friend_hint() {
		cancel_request_friend_hint = new TextView(getContext());
		cancel_request_friend_hint.setId(R.id.cancel_request_friend_hint);
		cancel_request_friend_hint.setTextSize(16);
		cancel_request_friend_hint.setText("You have sent a friend request to X, Do you want to cancel it?");
		cancel_request_friend_hint.setGravity(Gravity.CENTER);
		cancel_request_friend_hint.setAlpha(0);
		cancel_request_friend_hint.setTextColor(getResources().getColor(R.color.white));
		cancel_request_friend_hint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams cancel_request_friend_hint_lp = new FrameLayout.LayoutParams(-1, -2);
		cancel_request_friend_hint_lp.leftMargin = dp(24f);
		cancel_request_friend_hint_lp.topMargin = dp(362f);
		cancel_request_friend_hint_lp.rightMargin = dp(24f);
		cancel_request_friend.addView(cancel_request_friend_hint, cancel_request_friend_hint_lp);
	}

	protected void initView8() {
		view8 = new ClipFrameLayout(getContext());
		view8.setScaleX(0);
		view8.setBackgroundResource(R.drawable.circle2);
		view8.setScaleY(0);
		view8.setAlpha(0);
		FrameLayout.LayoutParams view8_lp = new FrameLayout.LayoutParams(dp(250f), dp(48f));
		view8_lp.topMargin = dp(438f);
		view8_lp.gravity = Gravity.CENTER_HORIZONTAL;
		cancel_request_friend.addView(view8, view8_lp);
	}

	protected void initCancel_friend_request() {
		cancel_friend_request = new TextView(getContext());
		cancel_friend_request.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		cancel_friend_request.setId(R.id.cancel_friend_request);
		cancel_friend_request.setText("Cancel Friend Request");
		cancel_friend_request.setTextSize(16);
		cancel_friend_request.setGravity(Gravity.CENTER);
		cancel_friend_request.setTextColor(getResources().getColor(R.color.white));
		cancel_friend_request.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams cancel_friend_request_lp = new FrameLayout.LayoutParams(-1, -1);
		cancel_friend_request_lp.gravity = Gravity.CENTER;
		view8.addView(cancel_friend_request, cancel_friend_request_lp);
	}

	protected void initIs_friend() {
		is_friend = new FrameLayout(getContext());
		is_friend.setId(R.id.is_friend);
		is_friend.setVisibility(View.GONE);
		this.addView(is_friend, new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initIs_friend_hint() {
		is_friend_hint = new TextView(getContext());
		is_friend_hint.setId(R.id.is_friend_hint);
		is_friend_hint.setTextSize(16);
		is_friend_hint.setText("X is your friend, Do you want to delete X from you friends list?");
		is_friend_hint.setGravity(Gravity.CENTER);
		is_friend_hint.setAlpha(0);
		is_friend_hint.setTextColor(getResources().getColor(R.color.white));
		is_friend_hint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams is_friend_hint_lp = new FrameLayout.LayoutParams(-1, -2);
		is_friend_hint_lp.leftMargin = dp(24f);
		is_friend_hint_lp.topMargin = dp(362f);
		is_friend_hint_lp.rightMargin = dp(24f);
		is_friend.addView(is_friend_hint, is_friend_hint_lp);
	}

	protected void initView9() {
		view9 = new ClipFrameLayout(getContext());
		view9.setScaleX(0);
		view9.setBackgroundResource(R.drawable.circle2);
		view9.setScaleY(0);
		view9.setAlpha(0);
		FrameLayout.LayoutParams view9_lp = new FrameLayout.LayoutParams(dp(250f), dp(48f));
		view9_lp.topMargin = dp(438f);
		view9_lp.gravity = Gravity.CENTER_HORIZONTAL;
		is_friend.addView(view9, view9_lp);
	}

	protected void initRemove_friend() {
		remove_friend = new TextView(getContext());
		remove_friend.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		remove_friend.setId(R.id.remove_friend);
		remove_friend.setText("Delete Friend");
		remove_friend.setTextSize(16);
		remove_friend.setGravity(Gravity.CENTER);
		remove_friend.setTextColor(getResources().getColor(R.color.white));
		remove_friend.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams remove_friend_lp = new FrameLayout.LayoutParams(-1, -1);
		remove_friend_lp.gravity = Gravity.CENTER;
		view9.addView(remove_friend, remove_friend_lp);
	}

	protected void initAsk_request_friend() {
		ask_request_friend = new FrameLayout(getContext());
		ask_request_friend.setId(R.id.ask_request_friend);
		ask_request_friend.setVisibility(View.GONE);
		this.addView(ask_request_friend, new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initAsk_request_friend_hint() {
		ask_request_friend_hint = new TextView(getContext());
		ask_request_friend_hint.setId(R.id.ask_request_friend_hint);
		ask_request_friend_hint.setTextSize(16);
		ask_request_friend_hint.setText("X wants to be your friend, Do you accept X's friend request?");
		ask_request_friend_hint.setGravity(Gravity.CENTER);
		ask_request_friend_hint.setAlpha(0);
		ask_request_friend_hint.setTextColor(getResources().getColor(R.color.white));
		ask_request_friend_hint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams ask_request_friend_hint_lp = new FrameLayout.LayoutParams(-1, -2);
		ask_request_friend_hint_lp.leftMargin = dp(24f);
		ask_request_friend_hint_lp.topMargin = dp(362f);
		ask_request_friend_hint_lp.rightMargin = dp(24f);
		ask_request_friend.addView(ask_request_friend_hint, ask_request_friend_hint_lp);
	}

	protected void initView10() {
		view10 = new LinearLayout(getContext());
		view10.setOrientation(0);
		FrameLayout.LayoutParams view10_lp = new FrameLayout.LayoutParams(-2, -2);
		view10_lp.topMargin = dp(438f);
		view10_lp.gravity = Gravity.CENTER_HORIZONTAL;
		ask_request_friend.addView(view10, view10_lp);
	}

	protected void initView11() {
		view11 = new ClipFrameLayout(getContext());
		view11.setScaleX(0);
		view11.setBackgroundResource(R.drawable.circle2);
		view11.setScaleY(0);
		view11.setAlpha(0);
		LinearLayout.LayoutParams view11_lp = new LinearLayout.LayoutParams(dp(56f), dp(56f));
		view11_lp.rightMargin = dp(12f);
		view10.addView(view11, view11_lp);
	}

	protected void initReject_friend_request() {
		reject_friend_request = new TextView(getContext());
		reject_friend_request.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		reject_friend_request.setId(R.id.reject_friend_request);
		reject_friend_request.setText("\ue5cd");
		reject_friend_request.setTextSize(25);
		reject_friend_request.setGravity(Gravity.CENTER);
		reject_friend_request.setTextColor(getResources().getColor(R.color.white));
		reject_friend_request.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		FrameLayout.LayoutParams reject_friend_request_lp = new FrameLayout.LayoutParams(-1, -1);
		reject_friend_request_lp.gravity = Gravity.CENTER;
		view11.addView(reject_friend_request, reject_friend_request_lp);
	}

	protected void initView12() {
		view12 = new ClipFrameLayout(getContext());
		view12.setScaleX(0);
		view12.setBackgroundResource(R.drawable.circle2);
		view12.setScaleY(0);
		view12.setAlpha(0);
		LinearLayout.LayoutParams view12_lp = new LinearLayout.LayoutParams(dp(56f), dp(56f));
		view12_lp.leftMargin = dp(12f);
		view10.addView(view12, view12_lp);
	}

	protected void initAccept_friend_request() {
		accept_friend_request = new TextView(getContext());
		accept_friend_request.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		accept_friend_request.setId(R.id.accept_friend_request);
		accept_friend_request.setText("\ue876");
		accept_friend_request.setTextSize(25);
		accept_friend_request.setGravity(Gravity.CENTER);
		accept_friend_request.setTextColor(getResources().getColor(R.color.white));
		accept_friend_request.setTypeface(ResourcesCompat.getFont(getContext(), R.font.materialicons));
		FrameLayout.LayoutParams accept_friend_request_lp = new FrameLayout.LayoutParams(-1, -1);
		accept_friend_request_lp.gravity = Gravity.CENTER;
		view12.addView(accept_friend_request, accept_friend_request_lp);
	}

	protected void initRv_bg() {
		rv_bg = new View(getContext());
		rv_bg.setBackgroundColor(getResources().getColor(R.color.background));
		rv_bg.setId(R.id.rv_bg);
		rv_bg.setElevation(dp(5f));
		rv_bg.setRotation(180);
		FrameLayout.LayoutParams rv_bg_lp = new FrameLayout.LayoutParams(-1, dp(2000f));
		rv_bg_lp.topMargin = dp(304f);
		this.addView(rv_bg, rv_bg_lp);
	}

	protected void initRv() {
		rv = new RecyclerView(getContext());
		rv.setId(R.id.rv);
		rv.setElevation(dp(6f));
		rv.setOverScrollMode(2);
		this.addView(rv, new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initChat_rv() {
		chat_rv = new RecyclerView(getContext());
		chat_rv.setId(R.id.chat_rv);
		chat_rv.setVisibility(View.GONE);
		chat_rv.setElevation(dp(10f));
		this.addView(chat_rv, new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initChat_footer() {
		chat_footer = new FrameLayout(getContext());
		chat_footer.setId(R.id.chat_footer);
		chat_footer.setElevation(dp(10f));
		chat_footer.setVisibility(View.GONE);
		FrameLayout.LayoutParams chat_footer_lp = new FrameLayout.LayoutParams(-1, dp(104f));
		chat_footer_lp.gravity = Gravity.BOTTOM;
		this.addView(chat_footer, chat_footer_lp);
	}

	protected void initView13() {
		view13 = new View(getContext());
		view13.setBackgroundResource(R.drawable.shadow);
		chat_footer.addView(view13, new FrameLayout.LayoutParams(-1, dp(24f)));
	}

	protected void initSend_parent() {
		send_parent = new LoadingButton(getContext());
		send_parent.setId(R.id.send_parent);
		FrameLayout.LayoutParams send_parent_lp = new FrameLayout.LayoutParams(dp(56f), dp(56f));
		send_parent_lp.rightMargin = dp(24f);
		send_parent_lp.bottomMargin = dp(24f);
		send_parent_lp.gravity = Gravity.BOTTOM|Gravity.RIGHT;
		chat_footer.addView(send_parent, send_parent_lp);
	}

	protected void initView14() {
		view14 = new AppCompatImageView(getContext());
		view14.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
		view14.setImageResource(R.drawable.send);
		LoadingButton.LayoutParams view14_lp = new LoadingButton.LayoutParams(-1, -1);
		view14_lp.setMargins(dp(12f), dp(12f), dp(12f), dp(12f));
		send_parent.addView(view14, view14_lp);
	}

	protected void initSend() {
		send = new View(getContext());
		send.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		send.setId(R.id.send);
		send_parent.addView(send, new LoadingButton.LayoutParams(-1, -1));
	}

	protected void initView15() {
		view15 = new FrameLayout(getContext());
		view15.setBackgroundResource(R.drawable.edt);
		FrameLayout.LayoutParams view15_lp = new FrameLayout.LayoutParams(-1, dp(56f));
		view15_lp.leftMargin = dp(24f);
		view15_lp.rightMargin = dp(92f);
		view15_lp.gravity = Gravity.CENTER_VERTICAL;
		chat_footer.addView(view15, view15_lp);
	}

	protected void initMessage_edt() {
		message_edt = new AppCompatEditText(getContext());
		message_edt.setId(R.id.message_edt);
		message_edt.setBackground(null);
		message_edt.setHintTextColor(getResources().getColor(R.color.gray));
		message_edt.setImeOptions(0x00000004);
		message_edt.setTextSize(16);
		message_edt.setHint("Write a message...");
		message_edt.setTextColor(getResources().getColor(R.color.white));
		message_edt.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		FrameLayout.LayoutParams message_edt_lp = new FrameLayout.LayoutParams(-1, -1);
		message_edt_lp.leftMargin = dp(24f);
		message_edt_lp.rightMargin = dp(24f);
		view15.addView(message_edt, message_edt_lp);
	}

	protected void initAccess_panel() {
		access_panel = new ClipFrameLayout(getContext());
		access_panel.setBackgroundResource(R.drawable.edt);
		access_panel.setId(R.id.access_panel);
		access_panel.setVisibility(View.GONE);
		FrameLayout.LayoutParams access_panel_lp = new FrameLayout.LayoutParams(-1, dp(56f));
		access_panel_lp.leftMargin = dp(24f);
		access_panel_lp.rightMargin = dp(24f);
		access_panel_lp.bottomMargin = dp(24f);
		access_panel_lp.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
		chat_footer.addView(access_panel, access_panel_lp);
	}

	protected void initAccess() {
		access = new AppCompatTextView(getContext());
		access.setId(R.id.access);
		access.setBackgroundResource(resolveAttribute(R.attr.selectableItemBackground));
		access.setTextSize(16);
		access.setGravity(Gravity.CENTER);
		access.setTextColor(getResources().getColor(R.color.white));
		access.setTypeface(ResourcesCompat.getFont(getContext(), R.font.regular));
		access_panel.addView(access, new ClipFrameLayout.LayoutParams(-1, -1));
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