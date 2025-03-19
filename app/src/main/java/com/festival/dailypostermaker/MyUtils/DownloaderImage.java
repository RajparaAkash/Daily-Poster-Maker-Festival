package com.festival.dailypostermaker.MyUtils;

import android.content.Context;
import android.util.Log;

import com.festival.dailypostermaker.Interface.ImageDownloadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloaderImage {

    private static final String TAG = "ImageDownloader";

    public static void downloadImage(Context context, String imageUrl, ImageDownloadCallback callback) {
        if (imageUrl == null) {
            Log.e(TAG, "Image URL is null");
            if (callback != null) {
                callback.onDownloadFailed("Image URL is null");
            }
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(imageUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Image download failed: " + e.getMessage());
                if (callback != null) {
                    callback.onDownloadFailed(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response){
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Failed to download image: " + response.message());
                    if (callback != null) {
                        callback.onDownloadFailed(response.message());
                    }
                    return;
                }

                InputStream inputStream = null;
                FileOutputStream outputStream = null;
                String filePath = null;

                try {
                    inputStream = response.body().byteStream();

                    File directory = new File(context.getCacheDir(), "TEMP");
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    File imageFile = new File(directory, "Daily_Poster_" + System.currentTimeMillis() + ".png");
                    outputStream = new FileOutputStream(imageFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    filePath = imageFile.getAbsolutePath();
                    Log.d(TAG, "Image downloaded and saved as: " + filePath);

                    if (callback != null) {
                        callback.onDownloadComplete(filePath);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error saving image: " + e.getMessage());
                    if (callback != null) {
                        callback.onDownloadFailed(e.getMessage());
                    }
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
