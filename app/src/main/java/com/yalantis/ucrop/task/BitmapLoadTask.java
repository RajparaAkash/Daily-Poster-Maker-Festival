package com.yalantis.ucrop.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yalantis.ucrop.OkHttpClientStore;
import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.util.BitmapLoadUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;

public class BitmapLoadTask extends AsyncTask<Void, Void, BitmapLoadTask.BitmapWorkerResult> {

    private static final String TAG = "BitmapWorkerTask";

    private static final int MAX_BITMAP_SIZE = 100 * 1024 * 1024;   // 100 MB

    private final WeakReference<Context> mContext;
    private Uri mInputUri;
    private Uri mOutputUri;
    private final int mRequiredWidth;
    private final int mRequiredHeight;

    private final BitmapLoadCallback mBitmapLoadCallback;

    public static class BitmapWorkerResult {

        Bitmap mBitmapResult;
        ExifInfo mExifInfo;
        Exception mBitmapWorkerException;

        public BitmapWorkerResult(@NonNull Bitmap bitmapResult, @NonNull ExifInfo exifInfo) {
            mBitmapResult = bitmapResult;
            mExifInfo = exifInfo;
        }

        public BitmapWorkerResult(@NonNull Exception bitmapWorkerException) {
            mBitmapWorkerException = bitmapWorkerException;
        }

    }

    public BitmapLoadTask(@NonNull Context context,
                          @NonNull Uri inputUri, @Nullable Uri outputUri,
                          int requiredWidth, int requiredHeight,
                          BitmapLoadCallback loadCallback) {
        mContext = new WeakReference<>(context);
        mInputUri = inputUri;
        mOutputUri = outputUri;
        mRequiredWidth = requiredWidth;
        mRequiredHeight = requiredHeight;
        mBitmapLoadCallback = loadCallback;
    }

    @Override
    @NonNull
    protected BitmapWorkerResult doInBackground(Void... params) {
        Context context = mContext.get();
        if (context == null) {
            return new BitmapWorkerResult(new NullPointerException("context is null"));
        }

        if (mInputUri == null) {
            return new BitmapWorkerResult(new NullPointerException("Input Uri cannot be null"));
        }

        try {
            processInputUri();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = BitmapLoadUtils.calculateInSampleSize(options, mRequiredWidth, mRequiredHeight);
        options.inJustDecodeBounds = false;

        Bitmap decodeSampledBitmap = null;

        boolean decodeAttemptSuccess = false;
        while (!decodeAttemptSuccess) {
            try {
                InputStream stream = context.getContentResolver().openInputStream(mInputUri);
                try {
                    decodeSampledBitmap = BitmapFactory.decodeStream(stream, null, options);
                    if (options.outWidth == -1 || options.outHeight == -1) {
                        return new BitmapWorkerResult(new IllegalArgumentException("Bounds for bitmap could not be retrieved from the Uri: [" + mInputUri + "]"));
                    }
                } finally {
                    BitmapLoadUtils.close(stream);
                }
                if (checkSize(decodeSampledBitmap, options)) continue;
                decodeAttemptSuccess = true;
            } catch (OutOfMemoryError error) {
                Log.e(TAG, "doInBackground: BitmapFactory.decodeFileDescriptor: ", error);
                options.inSampleSize *= 2;
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: ImageDecoder.createSource: ", e);
                return new BitmapWorkerResult(new IllegalArgumentException("Bitmap could not be decoded from the Uri: [" + mInputUri + "]", e));
            }
        }

        if (decodeSampledBitmap == null) {
            return new BitmapWorkerResult(new IllegalArgumentException("Bitmap could not be decoded from the Uri: [" + mInputUri + "]"));
        }

        int exifOrientation = BitmapLoadUtils.getExifOrientation(context, mInputUri);
        int exifDegrees = BitmapLoadUtils.exifToDegrees(exifOrientation);
        int exifTranslation = BitmapLoadUtils.exifToTranslation(exifOrientation);

        ExifInfo exifInfo = new ExifInfo(exifOrientation, exifDegrees, exifTranslation);

        Matrix matrix = new Matrix();
        if (exifDegrees != 0) {
            matrix.preRotate(exifDegrees);
        }
        if (exifTranslation != 1) {
            matrix.postScale(exifTranslation, 1);
        }
        if (!matrix.isIdentity()) {
            return new BitmapWorkerResult(BitmapLoadUtils.transformBitmap(decodeSampledBitmap, matrix), exifInfo);
        }

        return new BitmapWorkerResult(decodeSampledBitmap, exifInfo);
    }

    private void processInputUri(){
        Log.d(TAG, "Uri scheme: " + mInputUri.getScheme());
        if (isDownloadUri(mInputUri)) {
            try {
                downloadFile(mInputUri, mOutputUri);
            } catch (NullPointerException e) {
                Log.e(TAG, "Downloading failed", e);
                e.printStackTrace();
            }
        } else if (isContentUri(mInputUri)) {
            try {
                copyFile(mInputUri, mOutputUri);
            } catch (NullPointerException e) {
                Log.e(TAG, "Copying failed", e);
                e.printStackTrace();
            }
        } else if (!isFileUri(mInputUri)) {
            String inputUriScheme = mInputUri.getScheme();
            Log.e(TAG, "Invalid Uri scheme " + inputUriScheme);
        }
    }

    private void copyFile(@NonNull Uri inputUri, @Nullable Uri outputUri) {
        Log.d(TAG, "copyFile");

        if (outputUri == null) {
            return;
        }

        Context context = mContext.get();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(inputUri);
            if (inputStream == null) {
                return;
            }

            if (isContentUri(outputUri)) {
                outputStream = context.getContentResolver().openOutputStream(outputUri);
            } else {
                outputStream = new FileOutputStream(new File(outputUri.getPath()));
            }

            byte buffer[] = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BitmapLoadUtils.close(outputStream);
            BitmapLoadUtils.close(inputStream);

            // swap uris, because input image was copied to the output destination
            // (cropped image will override it later)
            mInputUri = mOutputUri;
        }
    }

    private void downloadFile(@NonNull Uri inputUri, @Nullable Uri outputUri){
        Log.d(TAG, "downloadFile");

        if (outputUri == null) {
            return;
        }

        Context context = mContext.get();
        if (context == null) {
            return;
        }

        OkHttpClient client = OkHttpClientStore.INSTANCE.getClient();

        BufferedSource source = null;
        Sink sink = null;
        Response response = null;
        try {
            Request request = new Request.Builder()
                .url(inputUri.toString())
                .build();
            response = client.newCall(request).execute();
            source = response.body().source();

            OutputStream outputStream;

            if (isContentUri(mOutputUri)) {
                outputStream = context.getContentResolver().openOutputStream(outputUri);
            } else {
                outputStream = new FileOutputStream(new File(outputUri.getPath()));
            }

            if (outputStream != null) {
                sink = Okio.sink(outputStream);
                source.readAll(sink);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BitmapLoadUtils.close(source);
            BitmapLoadUtils.close(sink);
            if (response != null) {
                BitmapLoadUtils.close(response.body());
            }
            client.dispatcher().cancelAll();

            // swap uris, because input image was downloaded to the output destination
            // (cropped image will override it later)
            mInputUri = mOutputUri;
        }
    }

    @Override
    protected void onPostExecute(@NonNull BitmapWorkerResult result) {
        if (result.mBitmapWorkerException == null) {
            mBitmapLoadCallback.onBitmapLoaded(result.mBitmapResult, result.mExifInfo, mInputUri, mOutputUri);
        } else {
            mBitmapLoadCallback.onFailure(result.mBitmapWorkerException);
        }
    }

    private boolean checkSize(Bitmap bitmap, BitmapFactory.Options options) {
        int bitmapSize = bitmap != null ? bitmap.getByteCount() : 0;
        if (bitmapSize > MAX_BITMAP_SIZE) {
            options.inSampleSize *= 2;
            return true;
        }
        return false;
    }

    private boolean isDownloadUri(Uri uri) {
        final String schema = uri.getScheme();
        return schema.equals("http") || schema.equals("https");
    }

    private boolean isContentUri(Uri uri) {
        final String schema = uri.getScheme();
        return schema.equals("content");
    }

    private boolean isFileUri(Uri uri) {
        final String schema = uri.getScheme();
        return schema.equals("file");
    }
}
