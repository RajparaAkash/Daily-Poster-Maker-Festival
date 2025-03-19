package com.festival.dailypostermaker.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.Model.Bg.BgItem;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.R;

import java.io.File;
import java.util.ArrayList;

public class AdapterBgDialogOnline extends RecyclerView.Adapter<AdapterBgDialogOnline.ViewHolder> {

    private final Activity activity;
    private final ArrayList<BgItem> backgroundList;
    private final Click click;
    private final BottomSheetDialog bgDialog;
    private ProgressDialog progressDialog;

    public AdapterBgDialogOnline(BottomSheetDialog bgDialog, Activity activity, ArrayList<BgItem> backgroundList, Click click) {
        this.bgDialog = bgDialog;
        this.activity = activity;
        this.backgroundList = backgroundList;
        this.click = click;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bg_dialog_online, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BgItem background = backgroundList.get(position);
        holder.bgCategoryTxt.setText(background.getName());

        Glide.with(activity.getApplicationContext())
                .load(background.getThumbnail())
                .error(R.drawable.img_place_holder)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(holder.backgroundImg);

        final File download = activity.getFilesDir();
        if (!download.exists()) {
            download.mkdirs();
        }
        String dowloadFolder = "Background/" + background.getName();
        File categoryFolder = new File(download, dowloadFolder);

        if (categoryFolder.exists()) {
            holder.downloadBgImg.setVisibility(View.GONE);
            holder.bgSuccessfulImg.setVisibility(View.VISIBLE);
            /*holder.txtWatchAd.setVisibility(View.GONE);*/
        } else {
            holder.bgSuccessfulImg.setVisibility(View.GONE);
            holder.downloadBgImg.setVisibility(View.VISIBLE);
            /*holder.txtWatchAd.setVisibility(View.VISIBLE);*/
        }

        holder.downloadBgImg.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                activity.runOnUiThread(() -> {
                    MyAdsManager.displayRewardedAd(activity, () -> {
                        downloadAndExtract(activity, background.getZipFile(), background.getName());
                    });
                });
            }
        });

        holder.bgSuccessfulImg.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Toast.makeText(activity, "This category already downloaded", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public interface Click {
        void onDownloadClick();
    }

    private void downloadAndExtract(final Context context, final String zipUrl, final String categoryName) {
        progressDialog = new ProgressDialog(context, R.style.ProgressDialogStyle);
        progressDialog.setMessage(context.getResources().getString(R.string.background_downloading));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final File downloadDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), context.getString(R.string.app_name));

        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        final File zipFile = new File(downloadDir, categoryName + ".zip");

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(zipUrl));
        request.setTitle("Downloading " + categoryName + " assets");
        request.setDescription("Downloading " + categoryName + " assets");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationUri(Uri.fromFile(zipFile));

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = downloadManager.enqueue(request);


        // Setup a thread to update the progress dialog
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean downloading = true;
                while (downloading) {

                    try {
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(downloadId);
                        Cursor cursor = downloadManager.query(query);
                        if (cursor.moveToFirst()) {
                            @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                            if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                                downloading = false;
                            }
                            @SuppressLint("Range") int bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            @SuppressLint("Range") int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                            final int progress = (int) ((bytesDownloaded * 100L) / bytesTotal);
                            progressDialog.setProgress(progress);
                            if (progress == 100) {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            }
                        }
                        cursor.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return backgroundList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ShapeableImageView backgroundImg;
        AppCompatImageView downloadBgImg;
        AppCompatImageView bgSuccessfulImg;
        TextView bgCategoryTxt;
        TextView txtWatchAd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            backgroundImg = itemView.findViewById(R.id.backgroundImg);
            downloadBgImg = itemView.findViewById(R.id.downloadBgImg);
            bgSuccessfulImg = itemView.findViewById(R.id.bgSuccessfulImg);
            bgCategoryTxt = itemView.findViewById(R.id.bgCategoryTxt);
            txtWatchAd = itemView.findViewById(R.id.txtWatchAd);
        }
    }

    public void dismissDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (bgDialog != null && bgDialog.isShowing()) {
                bgDialog.dismiss();
            }
            click.onDownloadClick();
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissDialog2() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
