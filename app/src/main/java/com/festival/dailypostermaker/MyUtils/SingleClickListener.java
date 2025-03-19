package com.festival.dailypostermaker.MyUtils;

import android.view.View;

public abstract class SingleClickListener implements View.OnClickListener {

    private static final long DEFAULT_DELAY = 1000L; // Default debounce interval (1 second)
    private final long delayMillis;
    private boolean isClickable = true;

    public SingleClickListener() {
        this(DEFAULT_DELAY);
    }

    public SingleClickListener(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    @Override
    public final void onClick(View v) {
        if (isClickable) {
            isClickable = false;
            v.postDelayed(() -> isClickable = true, delayMillis);
            onSingleClick(v);
        }
    }

    public abstract void onSingleClick(View view);
}
