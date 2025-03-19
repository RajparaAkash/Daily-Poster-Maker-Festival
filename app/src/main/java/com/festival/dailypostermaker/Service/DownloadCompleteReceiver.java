package com.festival.dailypostermaker.Service;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.festival.dailypostermaker.MyUtils.DownloadListener;
import com.festival.dailypostermaker.MyUtils.ZipUtils;

import java.io.File;

public class DownloadCompleteReceiver extends BroadcastReceiver {

    private DownloadListener downloadListener;

    public DownloadCompleteReceiver() {
        // Required empty constructor
    }

    public DownloadCompleteReceiver(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId != -1) {
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                boolean downloadSuccess = checkDownloadStatus(downloadManager, downloadId);
                if (downloadSuccess) {
                    File zipFile = getDownloadedFile(context, downloadId);
                    if (zipFile != null) {
                        File downloadDir = getDownloadDirectory(context);
                        if (!downloadDir.exists()) {
                            downloadDir.mkdirs();
                        }
                        File categoryFolder = new File(downloadDir, "Background");
                        if (!categoryFolder.exists()) {
                            categoryFolder.mkdirs();
                        }
                        try {
                            ZipUtils.unzip(zipFile, categoryFolder);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (downloadListener != null) {
                            downloadListener.onDownloadComplete();
                        }
                    }
                } else {
                    Toast.makeText(context, "Something went wrong ! Try again", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean checkDownloadStatus(DownloadManager downloadManager, long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            cursor.close();
            return status == DownloadManager.STATUS_SUCCESSFUL;
        }
        return false;
    }

    private File getDownloadedFile(Context context, long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
            String downloadUriString = cursor.getString(columnIndex);
            cursor.close();
            return new File(Uri.parse(downloadUriString).getPath());
        }
        cursor.close();
        return null;
    }

    private File getDownloadDirectory(Context context) {
        return context.getFilesDir();
    }
}