package com.festival.dailypostermaker.MyUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {

    private boolean swipeEnabled;

    public CustomViewPager(Context context) {
        super(context);
        this.swipeEnabled = false;
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.swipeEnabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.swipeEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.swipeEnabled && super.onInterceptTouchEvent(event);
    }

    public void setSwipeEnabled(boolean enabled) {
        this.swipeEnabled = enabled;
    }
}