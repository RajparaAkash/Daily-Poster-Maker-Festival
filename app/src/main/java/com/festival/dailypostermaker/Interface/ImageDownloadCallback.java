package com.festival.dailypostermaker.Interface;

public interface ImageDownloadCallback {
    void onDownloadComplete(String filePath);

    void onDownloadFailed(String errorMessage);
}
