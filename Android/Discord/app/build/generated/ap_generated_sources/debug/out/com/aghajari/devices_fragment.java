package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

public class devices_fragment extends LinearLayout {

	public FrameLayout header;
	public TextView back;
	public TextView title;
	public RecyclerView rv;

	protected CardView view1;

    public devices_fragment(Context context) {
        this(context, null);
    }

    public devices_fragment(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public devices_fragment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initView1();
		initHeader();
		initBack();
		initTitle();
		initRv();
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

	protected void initTitle() {
		title = new TextView(getContext());
		title.setId(R.id.title);
		title.setText("Devices");
		title.setTextSize(18);
		title.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		title.setTextColor(getResources().getColor(R.color.white));
		title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.semi_bold));
		FrameLayout.LayoutParams title_lp = new FrameLayout.LayoutParams(-2, -1);
		title_lp.leftMargin = dp(72f);
		title_lp.rightMargin = dp(72f);
		header.addView(title, title_lp);
	}

	protected void initRv() {
		rv = new RecyclerView(getContext());
		rv.setId(R.id.rv);
		this.addView(rv, new LinearLayout.LayoutParams(-1, -1));
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