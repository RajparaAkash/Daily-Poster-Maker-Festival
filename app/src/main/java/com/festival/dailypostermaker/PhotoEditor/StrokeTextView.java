package com.festival.dailypostermaker.PhotoEditor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.festival.dailypostermaker.R;

import java.lang.reflect.Field;
import java.util.Arrays;

public class StrokeTextView extends AppCompatTextView {

    private boolean gradientChanged;
    private LinearGradient mGradient;
    private int[] mGradientColor;
    private int mGradientOrientation;
    private TextPaint mPaint;
    private int mStrokeColor;
    private int mStrokeWidth;
    private int mTextColor;

    public StrokeTextView(Context context) {
        super(context);
        this.mStrokeColor = -16777216;
        init(context, null);
    }

    public StrokeTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mStrokeColor = -16777216;
        init(context, attributeSet);
    }

    public StrokeTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mStrokeColor = -16777216;
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        this.mPaint = getPaint();
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{R.attr.gradientOrientation, R.attr.strokeColor, R.attr.strokeWidth});
            this.mStrokeColor = obtainStyledAttributes.getColor(1, -16777216);
            this.mStrokeWidth = obtainStyledAttributes.getDimensionPixelSize(2, 0);
            this.mGradientOrientation = obtainStyledAttributes.getInt(0, 0);
            setStrokeColor(this.mStrokeColor);
            setStrokeWidth(this.mStrokeWidth);
            setGradientOrientation(this.mGradientOrientation);
            obtainStyledAttributes.recycle();
        }
    }

    public void setGradientOrientation(int i) {
        if (this.mGradientOrientation != i) {
            this.mGradientOrientation = i;
            this.gradientChanged = true;
            invalidate();
        }
    }

    public void setGradientColor(int[] iArr) {
        if (Arrays.equals(iArr, this.mGradientColor)) {
            return;
        }
        this.mGradientColor = iArr;
        this.gradientChanged = true;
        invalidate();
    }

    public void setStrokeColor(int i) {
        if (this.mStrokeColor != i) {
            this.mStrokeColor = i;
            invalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (this.mStrokeWidth > 0) {
            this.mTextColor = getCurrentTextColor();
            this.mPaint.setStrokeWidth(this.mStrokeWidth);
            this.mPaint.setFakeBoldText(true);
            this.mPaint.setShadowLayer(this.mStrokeWidth, 0.0f, 0.0f, 0);
            this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            setColor(this.mStrokeColor);
            this.mPaint.setShader(null);
            super.onDraw(canvas);
            if (this.gradientChanged) {
                if (this.mGradientColor != null) {
                    this.mGradient = getGradient();
                }
                this.gradientChanged = false;
            }
            LinearGradient linearGradient = this.mGradient;
            if (linearGradient != null) {
                this.mPaint.setShader(linearGradient);
                this.mPaint.setColor(-1);
            } else {
                setColor(this.mTextColor);
            }
            this.mPaint.setStrokeWidth(0.0f);
            this.mPaint.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
            super.onDraw(canvas);
            return;
        }
        super.onDraw(canvas);
    }

    public void setStrokeWidth(int i) {
        this.mStrokeWidth = i;
        invalidate();
    }

    private LinearGradient getGradient() {
        if (this.mGradientOrientation == 0) {
            return new LinearGradient(0.0f, 0.0f, getWidth(), 0.0f, this.mGradientColor, (float[]) null, Shader.TileMode.CLAMP);
        }
        return new LinearGradient(0.0f, 0.0f, 0.0f, getHeight(), this.mGradientColor, (float[]) null, Shader.TileMode.CLAMP);
    }

    private void setColor(int i) {
        try {
            Field declaredField = TextView.class.getDeclaredField("mCurTextColor");
            declaredField.setAccessible(true);
            declaredField.set(this, Integer.valueOf(i));
            declaredField.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mPaint.setColor(i);
    }
}
