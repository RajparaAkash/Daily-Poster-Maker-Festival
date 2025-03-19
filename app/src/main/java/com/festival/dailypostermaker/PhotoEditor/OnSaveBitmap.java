package com.festival.dailypostermaker.PhotoEditor;

import android.graphics.Bitmap;


public interface OnSaveBitmap {
    void onBitmapReady(Bitmap bitmap);

    void onFailure(Exception exc);
}
