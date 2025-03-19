package com.festival.dailypostermaker.MyUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class CustomRecyclerView extends RecyclerView {

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        // Determine if the touch event should be intercepted
        // You can check if the touch event is within a specific area of the RecyclerView

        // For example, if you want to allow scrolling on the inner RecyclerView
        // when it's touched directly and not by swiping on the parent RecyclerView
        View childView = findChildViewUnder(e.getX(), e.getY());
        if (childView instanceof RecyclerView) {
            return false;
        } else {
            return super.onInterceptTouchEvent(e);
        }
    }
}