package com.festival.dailypostermaker.MyUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquareLinerLayout extends LinearLayout {

    public SquareLinerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
