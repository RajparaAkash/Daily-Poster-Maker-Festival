package com.festival.dailypostermaker.PhotoEditor;

import android.view.View;

public interface OnPhotoEditorListener {

    void onAddViewListener(ViewType viewType, int i);

    void onAdded(StrokeTextView strokeTextView, RoundFrameLayout roundFrameLayout);

    void onClickGetEditTextChangeListener(StrokeTextView strokeTextView, RoundFrameLayout roundFrameLayout);

    void onEditTextChangeListener(View view, String str, int i);

    void onRemoveViewListener(int i);

    void onRemoveViewListener(ViewType viewType, int i);

    void onStartViewChangeListener(ViewType viewType);

    void onStopViewChangeListener(ViewType viewType);
}
