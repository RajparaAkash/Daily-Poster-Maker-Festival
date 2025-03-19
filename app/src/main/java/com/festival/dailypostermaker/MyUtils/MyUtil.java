package com.festival.dailypostermaker.MyUtils;

import static com.yalantis.ucrop.util.FileUtils.getDataColumn;
import static com.yalantis.ucrop.util.FileUtils.isDownloadsDocument;
import static com.yalantis.ucrop.util.FileUtils.isExternalStorageDocument;
import static com.yalantis.ucrop.util.FileUtils.isMediaDocument;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.festival.dailypostermaker.Activity.ActivitySharePost;
import com.festival.dailypostermaker.Model.Bg.BgSub;
import com.festival.dailypostermaker.Model.Downloads;
import com.festival.dailypostermaker.MyApplication;
import com.festival.dailypostermaker.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Executors;

public class MyUtil {

    private static final String TAG = MyUtil.class.getSimpleName();
    public static boolean isUpdate = false;
    public static boolean isAlternative = false;
    public static String changedText = "Double tap to edit !";

    public static String BASE_URL = MyApplication.getAppBaseUrl();
    public static String device_androidId = "";
    public static String privacy_policy = "";
    public static String terms_condition = "";
    public static String refund_cancel = "";

    public static int edit_font_pos = -1;
    public static int edit_box_color_pos = -1;
    public static int edit_color_pos = -1;
    public static int edit_shadow_color_pos = -1;

    public static int remainingFreePost = 5;
    public static int totalFreePost = 5;
    public static String paymentMode = "revenuecat";

    public static void hideKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static ArrayList<BgSub> getSubBgData(Context context) {
        ArrayList<BgSub> bgSubs = new ArrayList<>();

        final File download = context.getFilesDir();

        if (!download.exists()) {
            download.mkdirs();
        }
        Log.e(TAG, "getSubBgData: download " + download.exists());
        File directory = new File(download, "Background/");
        File[] files = directory.listFiles();
        if (files != null) {
            Log.e(TAG, "getSubBgData: files " + files.length);
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.compare(f2.lastModified(), f1.lastModified());
                }
            });

            for (File file : files) {
                if (file.isDirectory() && file.listFiles().length > 0) {
                    File[] mediaFiles = file.listFiles();
                    if (mediaFiles != null && mediaFiles.length > 0) {
                        String thumbnailPath = mediaFiles[0].getPath();

                        ArrayList<String> mediaPaths = new ArrayList<>();
                        for (File mediaFile : mediaFiles) {
                            mediaPaths.add(mediaFile.getPath());
                        }
                        bgSubs.add(new BgSub(file.getName(), file.getPath(), thumbnailPath, mediaFiles.length, mediaPaths));
                    }
                }
            }
        }
        Log.e(TAG, "getSubBgData: bgSubs " + bgSubs.size());
        return bgSubs;
    }

    public static File getOutputMediaFile(Context context) {
        File file = new File(context.getCacheDir(), "TEMP");
        if (!file.exists()) {
            file.mkdirs();
        }
        if (file.exists() || file.mkdirs()) {
            return new File(file.getPath() + File.separator + "Daily_Poster_" + System.currentTimeMillis() + ".png");
        }
        return null;
    }

    public static String getAppFolder(Context activity) {
        return activity.getExternalFilesDir(null).getPath() + "/";
    }

    public static String getPathFromURI(final Context context, final Uri uri) {

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static void cropFileMove(Context context, File file, File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File newFile = new File(dir, file.getName());
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            MediaScannerConnection.scanFile(context, new String[]{newFile.getAbsolutePath()}, null,
                    (path, uri) -> {
                        file.delete();
                    });
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void downloadFinalImageWithOutClose(Activity activity, File file, File dir, Boolean isShare) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File newFile = new File(dir, file.getName());

            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {
                fis = new FileInputStream(file);
                fos = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                int length;

                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }

                // Run UI updates and navigation on the main thread
                activity.runOnUiThread(() -> {
                    MediaScannerConnection.scanFile(activity, new String[]{newFile.getAbsolutePath()}, null,
                            (path, uri) -> {
                            });

                    if (isShare) {
                        shareFileOpen(activity, newFile.getPath());
                    } else {
                        Intent intent = new Intent(activity, ActivitySharePost.class);
                        intent.putExtra("finalImagePath", newFile.getPath());
                        intent.putExtra("isFromCreation", false);
                        intent.putExtra("isShare", isShare);
                        activity.startActivity(intent);
                    }
                });

            } catch (Exception e) {
                activity.runOnUiThread(() -> {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } finally {
                try {
                    if (fis != null) fis.close();
                    if (fos != null) fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void shareFileOpen(Activity activity, String finalImagePath) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String mimeType = FileUtils.getMimeType(finalImagePath);
        shareIntent.setType(mimeType);

        // Set the file URI as the intent data
        File file = new File(finalImagePath);
        Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(Intent.createChooser(shareIntent, "Share with"));
    }

    public static ArrayList<Downloads> getCreationData(Context context) {
        ArrayList<Downloads> downloadsArrayList = new ArrayList<>();
        String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + context.getResources().getString(R.string.app_name);
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATE_ADDED
        };
        String selection = MediaStore.Files.FileColumns.DATA + " LIKE ?";
        String[] selectionArgs = new String[]{
                "%" + folderPath + "%"
        };

        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));

                downloadsArrayList.add(new Downloads(id, filePath, fileName));
            }
            cursor.close();
        }
        return downloadsArrayList;
    }
}
