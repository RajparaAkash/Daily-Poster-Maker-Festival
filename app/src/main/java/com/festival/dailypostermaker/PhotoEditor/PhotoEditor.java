package com.festival.dailypostermaker.PhotoEditor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.festival.dailypostermaker.Activity.ActivityCreateAddText;
import com.festival.dailypostermaker.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class PhotoEditor implements BrushViewChangeListener {

    private static final String TAG = "PhotoEditor";
    private final List<View> addedViews;
    private final View alignView;
    private final BrushDrawingView brushDrawingView;
    private final Context context;
    private final View deleteView;

    Handler handler;
    private final ImageView imageView;
    private final boolean isTextPinchZoomable;
    private final Typeface mDefaultEmojiTypeface;
    private final Typeface mDefaultTextTypeface;
    private final LayoutInflater mLayoutInflater;
    public OnPhotoEditorListener mOnPhotoEditorListener;
    public PointF midPoint;
    public PhotoEditorView parentView;
    private final List<View> redoViews;
    private final PointF startPoint;
    private final View zoomView;


    public interface OnSaveListener {
        void onFailure(Exception exc);

        void onSuccess(String str);
    }

    private PhotoEditor(Builder builder) {
        this.handler = new Handler();
        this.midPoint = new PointF();
        this.startPoint = new PointF();
        Context context = builder.context;
        this.context = context;
        this.parentView = builder.parentView;
        this.imageView = builder.imageView;
        this.deleteView = builder.deleteView;
        this.alignView = builder.alignView;
        this.zoomView = builder.zoomView;
        BrushDrawingView brushDrawingView = builder.brushDrawingView;
        this.brushDrawingView = brushDrawingView;
        this.isTextPinchZoomable = builder.isTextPinchZoomable;
        this.mDefaultTextTypeface = builder.textTypeface;
        this.mDefaultEmojiTypeface = builder.emojiTypeface;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        brushDrawingView.setBrushViewChangeListener(this);
        this.addedViews = new ArrayList();
        this.redoViews = new ArrayList();
    }

    public void addText(String str, int i) {
        addText(null, str, i);
    }

    public void addText(Typeface typeface, String str, int i) {
        this.brushDrawingView.setBrushDrawingMode(false);
        final View layout = getLayout(ViewType.TEXT);
        final StrokeTextView strokeTextView = (StrokeTextView) layout.findViewById(R.id.tvPhotoEditorText);

        final View findViewById = layout.findViewById(R.id.imgTextStickerEdit);
        final View findViewById2 = layout.findViewById(R.id.imgTextStickerResize);

        final RoundFrameLayout roundFrameLayout = (RoundFrameLayout) layout.findViewById(R.id.frmBorder_highlight);
        final FrameLayout frameLayout = (FrameLayout) layout.findViewById(R.id.frmBorder);

        strokeTextView.setText(str);
        strokeTextView.setTextColor(i);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                findViewById.setVisibility(View.GONE);
                findViewById2.setVisibility(View.GONE);
                frameLayout.setBackgroundResource(0);
            }
        };
        findViewById.setVisibility(View.VISIBLE);
        findViewById2.setVisibility(View.VISIBLE);

        frameLayout.setBackgroundResource(R.drawable.rounded_border_tv);
        this.handler.removeCallbacks(runnable);
        this.handler.postDelayed(runnable, 2500L);
        if (typeface != null) {
            strokeTextView.setTypeface(typeface);
        }
        MultiTouchListener multiTouchListener = getMultiTouchListener();
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onLongClick() {
            }

            @Override
            public void onClick() {
                findViewById.setVisibility(View.VISIBLE);
                findViewById2.setVisibility(View.VISIBLE);
                frameLayout.setBackgroundResource(R.drawable.rounded_border_tv);
                PhotoEditor.this.handler.removeCallbacks(runnable);
                PhotoEditor.this.handler.postDelayed(runnable, 2500L);
            }

            @Override
            public void onDoubleClick() {
                String charSequence = strokeTextView.getText().toString();
                int currentTextColor = strokeTextView.getCurrentTextColor();
                if (PhotoEditor.this.mOnPhotoEditorListener != null) {
                    PhotoEditor.this.mOnPhotoEditorListener.onEditTextChangeListener(layout, charSequence, currentTextColor);
                }
            }

            @Override
            public void onSingleTap() {
                findViewById.setVisibility(View.VISIBLE);
                findViewById2.setVisibility(View.VISIBLE);
                frameLayout.setBackgroundResource(R.drawable.rounded_border_tv);
                PhotoEditor.this.handler.removeCallbacks(runnable);
                PhotoEditor.this.handler.postDelayed(runnable, 2500L);
                if (PhotoEditor.this.mOnPhotoEditorListener != null) {
                    PhotoEditor.this.mOnPhotoEditorListener.onClickGetEditTextChangeListener(strokeTextView, roundFrameLayout);
                }
            }
        });
        layout.setOnTouchListener(multiTouchListener);
        addViewToParent(layout, ViewType.TEXT);
        this.mOnPhotoEditorListener.onAdded(strokeTextView, roundFrameLayout);
        String charSequence = strokeTextView.getText().toString();
        int currentTextColor = strokeTextView.getCurrentTextColor();
        OnPhotoEditorListener onPhotoEditorListener = this.mOnPhotoEditorListener;
        /*if (onPhotoEditorListener != null) {
            onPhotoEditorListener.onEditTextChangeListener(layout, charSequence, currentTextColor);
        }*/
    }

    public void editText(View view, Typeface typeface, String str, int i) {
        TextView textView = (TextView) view.findViewById(R.id.tvPhotoEditorText);
        if (textView == null || !this.addedViews.contains(view) || TextUtils.isEmpty(str)) {
            return;
        }
        textView.setText(str);
        if (typeface != null) {
            textView.setTypeface(typeface);
        }
        textView.setTextColor(i);
        this.parentView.updateViewLayout(view, view.getLayoutParams());
        int indexOf = this.addedViews.indexOf(view);
        if (indexOf > -1) {
            this.addedViews.set(indexOf, view);
        }
    }

    private void addViewToParent(View view, ViewType viewType) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.addRule(13, -1);
        this.parentView.addView(view, layoutParams);
        this.addedViews.add(view);
        OnPhotoEditorListener onPhotoEditorListener = this.mOnPhotoEditorListener;
        if (onPhotoEditorListener != null) {
            onPhotoEditorListener.onAddViewListener(viewType, this.addedViews.size());
        }
    }

    private MultiTouchListener getMultiTouchListener() {
        return new MultiTouchListener(this.context, this.parentView, this.imageView, this.isTextPinchZoomable, this.mOnPhotoEditorListener);
    }


    private View getLayout(final ViewType viewType) {

        View view = this.mLayoutInflater.inflate(R.layout.layout_text_sticker, (ViewGroup) null);
        TextView textView = (TextView) view.findViewById(R.id.tvPhotoEditorText);
        if (textView != null && this.mDefaultTextTypeface != null) {
            textView.setGravity(17);
            if (this.mDefaultEmojiTypeface != null) {
                textView.setTypeface(this.mDefaultTextTypeface);
            }
        }

        if (view != null) {
            view.setTag(viewType);
            final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frmBorder);

            final View findViewById = view.findViewById(R.id.imgTextStickerEdit);
            if (findViewById != null) {
                View finalView = view;
                findViewById.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        context.startActivity(new Intent(context, ActivityCreateAddText.class));
                        /*PhotoEditor.this.viewUndo(finalView, viewType);*/
                    }
                });
            }

            final View findViewById2 = view.findViewById(R.id.imgTextStickerResize);
            if (findViewById2 != null) {
                findViewById2.setOnTouchListener(new View.OnTouchListener() {
                    float scaleX = 1.0f;
                    float rotation = 0.0f;

                    @Override
                    public boolean onTouch(View view2, MotionEvent motionEvent) {

                        int action = motionEvent.getAction();
                        if (action != 0) {
                            if (action == 1 || action == 2) {
                                PhotoEditor.this.zoomAndRotateSticker((View) view2.getParent(), motionEvent, frameLayout, findViewById, findViewById2, this.scaleX, this.rotation);
                                return false;
                            }
                            return false;
                        }
                        this.scaleX = ((View) view2.getParent()).getScaleX();
                        this.rotation = ((View) view2.getParent()).getRotation();
                        PhotoEditor.this.getPointF(view2, motionEvent);
                        return true;
                    }
                });
            }
        }
        return view;
    }

    public PointF getPointF(View view, MotionEvent motionEvent) {
        View view2 = (View) view.getParent();
        this.startPoint.set(motionEvent.getRawX(), motionEvent.getRawY());
        this.midPoint.set(view2.getX() + (view2.getWidth() / 2), view2.getY() + (view2.getHeight() / 2));
        return this.midPoint;
    }

    public void zoomAndRotateSticker(View view, MotionEvent motionEvent, FrameLayout frameLayout, View view2, View view3, float f, float f2) {
        if (view != null) {
            float floatA = (getFloatA(this.midPoint.x, this.midPoint.y, motionEvent.getRawX(), motionEvent.getRawY()) / getFloatA(this.startPoint.x, this.startPoint.y, this.midPoint.x, this.midPoint.y)) * f;
            view.setPivotX(view.getWidth() / 2);
            view.setPivotY(view.getHeight() / 2);
            view.setScaleX(floatA);
            view.setScaleY(floatA);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
            int dimension = (int) (this.context.getResources().getDimension(R.dimen.frame_margin) / floatA);
            layoutParams.setMargins(dimension, dimension, dimension, dimension);
            frameLayout.setLayoutParams(layoutParams);
            view2.setPivotX(0.0f);
            view2.setPivotY(0.0f);
            view3.setPivotX(view3.getWidth());
            view3.setPivotY(view3.getHeight());
            float f3 = 1.0f / floatA;
            view2.setScaleX(f3);
            view2.setScaleY(f3);
            view3.setScaleX(f3);
            view3.setScaleY(f3);
            float degrees = f2 + ((float) Math.toDegrees(Math.atan2(motionEvent.getRawY() - this.midPoint.y, motionEvent.getRawX() - this.midPoint.x) - Math.atan2(this.startPoint.y - this.midPoint.y, this.startPoint.x - this.midPoint.x)));
            view.setRotation(degrees);
            view.requestLayout();
        }
    }

    public float getFloatA(float f, float f2, float f3, float f4) {
        double d = f - f3;
        double d2 = f2 - f4;
        return (float) Math.sqrt((d * d) + (d2 * d2));
    }

    public void viewUndo(View view, ViewType viewType) {
        if (this.addedViews.size() <= 0 || !this.addedViews.contains(view)) {
            return;
        }
        this.parentView.removeView(view);
        this.addedViews.remove(view);
        this.redoViews.add(view);
        OnPhotoEditorListener onPhotoEditorListener = this.mOnPhotoEditorListener;
        if (onPhotoEditorListener != null) {
            onPhotoEditorListener.onRemoveViewListener(this.addedViews.size());
            this.mOnPhotoEditorListener.onRemoveViewListener(viewType, this.addedViews.size());
        }
    }

    public boolean undo() {
        if (this.addedViews.size() > 0) {
            List<View> list = this.addedViews;
            View view = list.get(list.size() - 1);
            if (view instanceof BrushDrawingView) {
                BrushDrawingView brushDrawingView = this.brushDrawingView;
                return brushDrawingView != null && brushDrawingView.undo();
            }
            List<View> list2 = this.addedViews;
            list2.remove(list2.size() - 1);
            this.parentView.removeView(view);
            this.redoViews.add(view);
            OnPhotoEditorListener onPhotoEditorListener = this.mOnPhotoEditorListener;
            if (onPhotoEditorListener != null) {
                onPhotoEditorListener.onRemoveViewListener(this.addedViews.size());
                Object tag = view.getTag();
                if (tag != null && (tag instanceof ViewType)) {
                    this.mOnPhotoEditorListener.onRemoveViewListener((ViewType) tag, this.addedViews.size());
                }
            }
        }
        return this.addedViews.size() != 0;
    }

    public boolean redo() {
        if (this.redoViews.size() > 0) {
            List<View> list = this.redoViews;
            View view = list.get(list.size() - 1);
            if (view instanceof BrushDrawingView) {
                BrushDrawingView brushDrawingView = this.brushDrawingView;
                return brushDrawingView != null && brushDrawingView.redo();
            }
            List<View> list2 = this.redoViews;
            list2.remove(list2.size() - 1);
            this.parentView.addView(view);
            this.addedViews.add(view);
            Object tag = view.getTag();
            OnPhotoEditorListener onPhotoEditorListener = this.mOnPhotoEditorListener;
            if (onPhotoEditorListener != null && tag != null && (tag instanceof ViewType)) {
                onPhotoEditorListener.onAddViewListener((ViewType) tag, this.addedViews.size());
            }
        }
        return this.redoViews.size() != 0;
    }

    private void clearBrushAllViews() {
        BrushDrawingView brushDrawingView = this.brushDrawingView;
        if (brushDrawingView != null) {
            brushDrawingView.clearAll();
        }
    }

    public void clearAllViews() {
        for (int i = 0; i < this.addedViews.size(); i++) {
            this.parentView.removeView(this.addedViews.get(i));
        }
        if (this.addedViews.contains(this.brushDrawingView)) {
            this.parentView.addView(this.brushDrawingView);
        }
        this.addedViews.clear();
        this.redoViews.clear();
        clearBrushAllViews();
    }

    public void clearHelperBox() {
        for (int i = 0; i < this.parentView.getChildCount(); i++) {
            View childAt = this.parentView.getChildAt(i);
            FrameLayout frameLayout = (FrameLayout) childAt.findViewById(R.id.frmBorder);
            if (frameLayout != null) {
                frameLayout.setBackgroundResource(0);
                View findViewById = childAt.findViewById(R.id.imgTextStickerEdit);
                if (findViewById != null) {
                    findViewById.setVisibility(View.GONE);
                }
                View findViewById2 = childAt.findViewById(R.id.imgTextStickerResize);
                if (findViewById2 != null) {
                    findViewById2.setVisibility(View.GONE);
                }
            }
        }
    }

    public void saveAsFile(final Executor backgroundExecutor, final Handler uiHandler, String str, final SaveSettings saveSettings, final OnSaveListener onSaveListener) {
        Log.d(TAG, "Image Path: " + str);
        this.parentView.saveFilter(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {
                clearHelperBox();
                parentView.setDrawingCacheEnabled(false);

//                new AsyncTask<String, String, Exception>() {
//                    @Override
//                    public void onPreExecute() {
//                        super.onPreExecute();
//                        PhotoEditor.this.clearHelperBox();
//                        PhotoEditor.this.parentView.setDrawingCacheEnabled(false);
//                    }
//
//                    @Override
//                    public Exception doInBackground(String... strArr) {
//                        Bitmap drawingCache;
//                        try {
//                            FileOutputStream fileOutputStream = new FileOutputStream(new File(str), false);
//                            if (PhotoEditor.this.parentView != null) {
//                                PhotoEditor.this.parentView.setDrawingCacheEnabled(true);
//                                if (saveSettings.isTransparencyEnabled()) {
//                                    drawingCache = BitmapUtil.removeTransparency(PhotoEditor.this.parentView.getDrawingCache());
//                                } else {
//                                    drawingCache = PhotoEditor.this.parentView.getDrawingCache();
//                                }
//                                drawingCache.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
//                            }
//                            fileOutputStream.flush();
//                            fileOutputStream.close();
//                            Log.d(PhotoEditor.TAG, "Filed Saved Successfully");
//                            return null;
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Log.d(PhotoEditor.TAG, "Failed to save File");
//                            return e;
//                        }
//                    }
//
//                    @Override
//                    public void onPostExecute(Exception exc) {
//                        super.onPostExecute(exc);
//                        if (exc == null) {
//                            if (saveSettings.isClearViewsEnabled()) {
//                                PhotoEditor.this.clearAllViews();
//                            }
//                            onSaveListener.onSuccess(str);
//                            return;
//                        }
//                        onSaveListener.onFailure(exc);
//                    }
//                }.execute(new String[0]);

                backgroundExecutor.execute(() -> {
                    Bitmap drawingCache = null;
                    Exception exception = null;
                    FileOutputStream fileOutputStream = null;
                    try {
                        fileOutputStream = new FileOutputStream(new File(str), false);
                        if (parentView != null) {
                            parentView.setDrawingCacheEnabled(true);
                            if (saveSettings.isTransparencyEnabled()) {
                                drawingCache = BitmapUtil.removeTransparency(parentView.getDrawingCache());
                            } else {
                                drawingCache = parentView.getDrawingCache();
                            }
                            drawingCache.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        Log.d(PhotoEditor.TAG, "Filed Saved Successfully");
                    } catch (Exception e) {
                        exception = e;
                        e.printStackTrace();
                        Log.d(PhotoEditor.TAG, "Failed to save File");
                    } finally {
                        // Ensure resources are closed even on exception
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // Update UI on the main thread
                    Exception finalException = exception;
                    uiHandler.post(() -> {
                        if (finalException == null) {
                            if (saveSettings.isClearViewsEnabled()) {
                                clearAllViews();
                            }
                            onSaveListener.onSuccess(str);
                        } else {
                            onSaveListener.onFailure(finalException);
                        }
                    });
                });
            }

            @Override
            public void onFailure(Exception exc) {
                onSaveListener.onFailure(exc);
            }
        });
    }

    public void setOnPhotoEditorListener(OnPhotoEditorListener onPhotoEditorListener) {
        this.mOnPhotoEditorListener = onPhotoEditorListener;
    }

    @Override
    public void onViewAdd(BrushDrawingView brushDrawingView) {
        if (this.redoViews.size() > 0) {
            List<View> list = this.redoViews;
            list.remove(list.size() - 1);
        }
        this.addedViews.add(brushDrawingView);
        OnPhotoEditorListener onPhotoEditorListener = this.mOnPhotoEditorListener;
        if (onPhotoEditorListener != null) {
            onPhotoEditorListener.onAddViewListener(ViewType.BRUSH_DRAWING, this.addedViews.size());
        }
    }

    @Override
    public void onViewRemoved(BrushDrawingView brushDrawingView) {
        if (this.addedViews.size() > 0) {
            List<View> list = this.addedViews;
            View remove = list.remove(list.size() - 1);
            if (!(remove instanceof BrushDrawingView)) {
                this.parentView.removeView(remove);
            }
            this.redoViews.add(remove);
        }
        OnPhotoEditorListener onPhotoEditorListener = this.mOnPhotoEditorListener;
        if (onPhotoEditorListener != null) {
            onPhotoEditorListener.onRemoveViewListener(this.addedViews.size());
            this.mOnPhotoEditorListener.onRemoveViewListener(ViewType.BRUSH_DRAWING, this.addedViews.size());
        }
    }

    @Override
    public void onStartDrawing() {
        OnPhotoEditorListener onPhotoEditorListener = this.mOnPhotoEditorListener;
        if (onPhotoEditorListener != null) {
            onPhotoEditorListener.onStartViewChangeListener(ViewType.BRUSH_DRAWING);
        }
    }

    @Override
    public void onStopDrawing() {
        OnPhotoEditorListener onPhotoEditorListener = this.mOnPhotoEditorListener;
        if (onPhotoEditorListener != null) {
            onPhotoEditorListener.onStopViewChangeListener(ViewType.BRUSH_DRAWING);
        }
    }


    public static class Builder {
        public View alignView;
        public BrushDrawingView brushDrawingView;
        public Context context;
        public View deleteView;
        public Typeface emojiTypeface;
        public ImageView imageView;
        public boolean isTextPinchZoomable = true;
        public PhotoEditorView parentView;
        public Typeface textTypeface;
        public View zoomView;

        public Builder(Context context, PhotoEditorView photoEditorView) {
            this.context = context;
            this.parentView = photoEditorView;
            this.imageView = photoEditorView.getSource();
            this.brushDrawingView = photoEditorView.getBrushDrawingView();
        }

        public Builder setDefaultTextTypeface(Typeface typeface) {
            this.textTypeface = typeface;
            return this;
        }

        public Builder setDefaultEmojiTypeface(Typeface typeface) {
            this.emojiTypeface = typeface;
            return this;
        }

        public Builder setPinchTextScalable(boolean z) {
            this.isTextPinchZoomable = z;
            return this;
        }

        public PhotoEditor build() {
            return new PhotoEditor(this);
        }
    }
}
