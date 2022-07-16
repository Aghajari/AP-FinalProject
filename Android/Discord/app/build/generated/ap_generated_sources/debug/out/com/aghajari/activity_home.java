package com.aghajari;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class activity_home extends FrameLayout {

	public FrameLayout fragment_holder;

    public activity_home(Context context) {
        this(context, null);
    }

    public activity_home(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public activity_home(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

	protected void init() {
		initThis();
		initFragment_holder();
	}

	protected void initThis() {
		this.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
	}

	protected void initFragment_holder() {
		fragment_holder = new FrameLayout(getContext());
		fragment_holder.setId(R.id.fragment_holder);
		this.addView(fragment_holder, new FrameLayout.LayoutParams(-1, -1));
	}

}