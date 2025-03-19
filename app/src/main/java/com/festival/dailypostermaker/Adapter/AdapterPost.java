package com.festival.dailypostermaker.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.festival.dailypostermaker.Activity.ActivityEditDetails;
import com.festival.dailypostermaker.Activity.ActivitySharePost;
import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.Api.ApiEndpoints;
import com.festival.dailypostermaker.Api.RetrofitClient;
import com.festival.dailypostermaker.Interface.ImageDownloadCallback;
import com.festival.dailypostermaker.Model.CategoryName.PostItem;
import com.festival.dailypostermaker.Model.PostExport;
import com.festival.dailypostermaker.Model.TemplateMainLayout;
import com.festival.dailypostermaker.MyUtils.DownloaderImage;
import com.festival.dailypostermaker.MyUtils.FileUtils;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.PageIndicatorView.PageIndicatorView;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterPost extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Activity activity;
    private final ArrayList<PostItem> bannerListWithAds;
    private ArrayList<TemplateMainLayout> templateMainLayoutList = new ArrayList<>();
    private String withFramePath = "";
    private Dialog pleaseWaitDialog;
    private int lastPosition = -1;
    private int postID = 0;

    public AdapterPost(Activity activity, ArrayList<PostItem> bannerList, ArrayList<PostItem> bannerListWithAds, ArrayList<TemplateMainLayout> templateMainLayoutList, int totalCategoryData) {
        this.activity = activity;
        this.bannerListWithAds = bannerListWithAds;
        this.templateMainLayoutList = repeatTemplateMainLayList(templateMainLayoutList, getTotalCategoryDataWithAds(totalCategoryData, 3));
    }

    private int getTotalCategoryDataWithAds(int list, int count) {
        int total = 0;
        if (list > 0) {
            for (int i = 0; i < list; i++) {
                if (count != 0) {
                    if (i != 0 && i % count == 0) {
                        total++;
                    }
                }
                total++;
            }
        }
        return total;
    }

    private ArrayList<TemplateMainLayout> repeatTemplateMainLayList(ArrayList<TemplateMainLayout> templateMainLayoutList, int targetSize) {
        ArrayList<TemplateMainLayout> repeatedList = new ArrayList<>();
        if (!templateMainLayoutList.isEmpty() && targetSize > 0) {
            int templateIndex = 0;
            for (int i = 0; i < targetSize; i++) {
                repeatedList.add(templateMainLayoutList.get(templateIndex));
                templateIndex = (templateIndex + 1) % templateMainLayoutList.size();
            }
        }
        return repeatedList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            if (viewType == -1) {
                return new AdViewHolder(LayoutInflater.from(activity).inflate(R.layout.ad_native_space_holder_recyclerview, parent, false));
            } else {
                if (viewType < templateMainLayoutList.size()) {
                    return new ViewHolder(LayoutInflater.from(activity).inflate(templateMainLayoutList.get(viewType).getLayoutList(), parent, false));
                } else {
                    return new AdViewHolder(LayoutInflater.from(activity).inflate(R.layout.ad_native_space_holder_recyclerview, parent, false));
                }
            }
        } catch (Exception e) {
            return new AdViewHolder(LayoutInflater.from(activity).inflate(R.layout.ad_native_space_holder_recyclerview, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            if (getItemViewType(position) == -1) {
                AdViewHolder myViewHolder = null;
                try {
                    myViewHolder = (AdViewHolder) holder;
                    //show native ad
                    if (MyPreference.get_IsPremium() || MyPreference.get_Ad() == 0) {
                        myViewHolder.llNative.removeAllViews();
                        myViewHolder.llNative.setVisibility(View.GONE);
                    } else {
                        if (position > lastPosition) {
                            myViewHolder.llNative.setVisibility(View.VISIBLE);
                            MyAdsManager.displayNativeLargeAd(activity, myViewHolder.flNative);
                            lastPosition = position;
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    if (myViewHolder != null) {
                        myViewHolder.llNative.removeAllViews();
                        myViewHolder.llNative.setVisibility(View.GONE);
                    }
                }
            } else {
                if (getItemViewType(position) < templateMainLayoutList.size()) {
                    ViewHolder viewHolder = (ViewHolder) holder;
                    PostItem banner = bannerListWithAds.get(position);

                    String loadImageUrl = " ";
                    if (banner.getIsVideoOrBanner() == 1) {
                        loadImageUrl = banner.getBanner();
                        /*viewHolder.videoPlayImg.setVisibility(View.GONE);*/
                    } else if (banner.getIsVideoOrBanner() == 2) {
                        loadImageUrl = banner.getIcon();
                        /*viewHolder.videoPlayImg.setVisibility(View.VISIBLE);*/
                    }

                    Glide.with(activity.getApplicationContext()).load(loadImageUrl).placeholder(R.drawable.img_place_holder).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            viewHolder.tempMainImg.setImageDrawable(resource);

                            // Use Palette API to extract colors
                            Palette.from(((BitmapDrawable) resource).getBitmap()).generate(palette -> {
                                Palette.Swatch dominantSwatch = palette.getDominantSwatch();
                                if (dominantSwatch != null) {
                                    int dominantColor = palette.getMutedColor(ContextCompat.getColor(activity, R.color.defaultPostBgColor));
                                    viewHolder.layoutTemp.setBackgroundColor(dominantColor);
                                }
                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Handle placeholder clearing
                        }
                    });

                    AdapterFrameList adapterFrameList = new AdapterFrameList(activity, templateMainLayoutList.get(position).getPostDetailsList(), loadImageUrl);
                    viewHolder.itemViewPager.setAdapter(adapterFrameList);
                    viewHolder.itemViewPager.setNestedScrollingEnabled(false);
                    viewHolder.pageIndicatorView.setCount(templateMainLayoutList.get(position).getPostDetailsList().size());
                    viewHolder.itemViewPager.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
                        @Override
                        public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder holder, int adapterPosition) {
                            viewHolder.pageIndicatorView.setSelection(adapterPosition);
                        }
                    });

                    viewHolder.layEdit.setOnClickListener(new SingleClickListener() {
                        @Override
                        public void onSingleClick(View view) {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                                Dexter.withContext(activity).withPermissions(
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .withListener(new MultiplePermissionsListener() {
                                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                                    layEditClick();
                                                }
                                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                                    showSettingsDialog();
                                                }
                                            }

                                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                                permissionToken.continuePermissionRequest();
                                            }
                                        }).withErrorListener(new PermissionRequestErrorListener() {
                                            public void onError(DexterError dexterError) {
                                                Toast.makeText(activity, "Error occurred!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).onSameThread().check();
                            } else {
                                layEditClick();
                            }
                        }

                        public void layEditClick() {
                            MyAdsManager.displayInterstitialSecondWise(activity, () -> {
                                activity.startActivity(new Intent(activity, ActivityEditDetails.class));
                            });
                        }
                    });


                    viewHolder.layDownload.setOnClickListener(new SingleClickListener() {
                        @Override
                        public void onSingleClick(View view) {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                                Dexter.withContext(activity).withPermissions(
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .withListener(new MultiplePermissionsListener() {
                                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                                    layDownloadClick();
                                                }
                                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                                    showSettingsDialog();
                                                }
                                            }

                                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                                permissionToken.continuePermissionRequest();
                                            }
                                        }).withErrorListener(new PermissionRequestErrorListener() {
                                            public void onError(DexterError dexterError) {
                                                Toast.makeText(activity, "Error occurred!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).onSameThread().check();
                            } else {
                                layDownloadClick();
                            }
                        }

                        public void layDownloadClick() {
                            if (banner.getIsVideoOrBanner() == 1) {
                                postID = banner.getId();
                                downloadImageData(viewHolder.layoutTemp, banner.getBanner(), false);
                            }
                        }
                    });

                    viewHolder.layShare.setOnClickListener(new SingleClickListener() {
                        @Override
                        public void onSingleClick(View view) {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                                Dexter.withContext(activity).withPermissions(
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .withListener(new MultiplePermissionsListener() {
                                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                                    layShareClick();
                                                }
                                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                                    showSettingsDialog();
                                                }
                                            }

                                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                                permissionToken.continuePermissionRequest();
                                            }
                                        }).withErrorListener(new PermissionRequestErrorListener() {
                                            public void onError(DexterError dexterError) {
                                                Toast.makeText(activity, "Error occurred!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).onSameThread().check();
                            } else {
                                layShareClick();
                            }
                        }

                        public void layShareClick() {
                            if (banner.getIsVideoOrBanner() == 1) {
                                postID = banner.getId();
                                downloadImageData(viewHolder.layoutTemp, banner.getBanner(), true);
                            }
                        }
                    });

                    viewHolder.itemView.setOnClickListener(v -> {

                    });

                } else {
                    AdViewHolder myViewHolder = null;
                    try {
                        myViewHolder = (AdViewHolder) holder;
                        //show native ad
                        if (MyPreference.get_IsPremium() || MyPreference.get_Ad() == 0) {
                            myViewHolder.llNative.removeAllViews();
                            myViewHolder.llNative.setVisibility(View.GONE);
                        } else {
                            if (position > lastPosition) {
                                myViewHolder.llNative.setVisibility(View.VISIBLE);
                                MyAdsManager.displayNativeLargeAd(activity, myViewHolder.flNative);
                                lastPosition = position;
                            }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        if (myViewHolder != null) {
                            myViewHolder.llNative.removeAllViews();
                            myViewHolder.llNative.setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception e) {
            AdViewHolder myViewHolder = null;
            try {
                myViewHolder = (AdViewHolder) holder;
                //show native ad
                if (MyPreference.get_IsPremium() || MyPreference.get_Ad() == 0) {
                    myViewHolder.llNative.removeAllViews();
                    myViewHolder.llNative.setVisibility(View.GONE);
                } else {
                    if (position > lastPosition) {
                        myViewHolder.llNative.setVisibility(View.VISIBLE);
                        MyAdsManager.displayNativeLargeAd(activity, myViewHolder.flNative);
                        lastPosition = position;
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                if (myViewHolder != null) {
                    myViewHolder.llNative.removeAllViews();
                    myViewHolder.llNative.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (bannerListWithAds.get(position) == null) {
            return -1;
        } else {
            return position;
        }
    }

    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialogInterface, i) -> {
            dialogInterface.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }

    public void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        try {
            if (bannerListWithAds != null && !bannerListWithAds.isEmpty()) {
                return bannerListWithAds.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void downloadImageData(View saveView, String onlineImageUrl, boolean isShare) {
        showPleaseWaitDialog();
        Bitmap bitmap = getBitmapFromView(saveView);
        String fileName = "Daily_Poster_" + System.currentTimeMillis() + ".png";

        DownloadImageTask downloadTask = new DownloadImageTask(Executors.newSingleThreadExecutor(),
                new Handler(Looper.getMainLooper()), bitmap, fileName, onlineImageUrl, isShare);
        downloadTask.startDownload();
    }

    private void shareFileOpen(String finalImagePath) {
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

    public class DownloadImageTask {

        private final Executor executor;
        private final Handler handler;
        private final Bitmap bitmap;
        private final String fileName;
        private final String onlineImageUrl;
        private final Boolean isShare;

        public DownloadImageTask(Executor executor, Handler handler, Bitmap bitmap, String fileName, String onlineImageUrl, Boolean isShare) {
            this.executor = executor;
            this.handler = handler;
            this.bitmap = bitmap;
            this.fileName = fileName;
            this.onlineImageUrl = onlineImageUrl;
            this.isShare = isShare;
        }

        public void startDownload() {
            executor.execute(() -> {
                boolean result = saveImageBitmap(bitmap, fileName);
                handler.post(() -> {
                    /*handleDownloadResult(result);*/
                    showWatchAdDialog(isShare);
                });
            });
        }

        private void handleDownloadResult(boolean result) {
            if (result) {
                if (MyPreference.get_IsPremium()) {
                    dismissPleaseWaitDialog();
                    if (isShare) {
                        shareFileOpen(withFramePath);
                    } else {
                        Intent intent = new Intent(activity, ActivitySharePost.class);
                        intent.putExtra("finalImagePath", withFramePath);
                        intent.putExtra("isFromCreation", false);
                        intent.putExtra("isShare", isShare);
                        activity.startActivity(intent);
                    }
                } else {
                    if (onlineImageUrl != null) {
                        DownloaderImage.downloadImage(activity, onlineImageUrl, new ImageDownloadCallback() {
                            @Override
                            public void onDownloadComplete(String withOutFramePath) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissPleaseWaitDialog();
                                        if (MyUtil.remainingFreePost > 0) {
                                            MyAdsManager.displayInterstitialSecondWise(activity, new MyAdsManager.NextStepListener() {
                                                @Override
                                                public void onNextStep() {
                                                    /*Intent intent = new Intent(activity, WatchVideoToExportActivity.class);
                                                    intent.putExtra("postID", postID);
                                                    intent.putExtra("withFramePath", withFramePath);
                                                    intent.putExtra("isShare", isShare);
                                                    activity.startActivity(intent);*/
                                                }
                                            });
                                        } else {
                                            MyAdsManager.displayInterstitialSecondWise(activity, new MyAdsManager.NextStepListener() {
                                                @Override
                                                public void onNextStep() {
                                                    /*Intent intent = new Intent(activity, WatchVideoToExport2Activity.class);
                                                    intent.putExtra("withFramePath", withFramePath);
                                                    intent.putExtra("withOutFramePath", withOutFramePath);
                                                    intent.putExtra("isShare", isShare);
                                                    activity.startActivity(intent);*/
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onDownloadFailed(String errorMessage) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissPleaseWaitDialog();
                                    }
                                });
                                Log.e("TAG", "onDownloadFailed: " + errorMessage);
                            }
                        });
                    } else {
                        dismissPleaseWaitDialog();
                        Log.e("TAG", "Image URL is null");
                    }
                }
            } else {
                dismissPleaseWaitDialog();
                Log.e("TAG", "Failed to download: ");
            }
        }

        private boolean saveImageBitmap(Bitmap bitmap, String fileName) {
            File folder;
            if (MyPreference.get_IsPremium()) {
                folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + activity.getResources().getString(R.string.app_name));
            } else {
                folder = new File(activity.getCacheDir(), "TEMP");
            }
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = new File(folder, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

                MediaScannerConnection.scanFile(activity, new String[]{new File(file.getPath()).getAbsolutePath()}, null, null);
                withFramePath = file.getPath();

                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private void showWatchAdDialog(Boolean isShare) {
        dismissPleaseWaitDialog();
        BottomSheetDialog watchAdDialog = new BottomSheetDialog(activity, R.style.CustomBottomSheetDialog);
        View bottomSheetView = activity.getLayoutInflater().inflate(R.layout.dialog_watch_ad_to_download, null);
        watchAdDialog.setContentView(bottomSheetView);
        watchAdDialog.setCancelable(false);

        AppCompatImageView downloadedImg = watchAdDialog.findViewById(R.id.downloadedImg);
        AppCompatImageView closeImg = watchAdDialog.findViewById(R.id.closeImg);
        FrameLayout watchToDownloadLay = watchAdDialog.findViewById(R.id.watchToDownloadLay);

        Glide.with(activity.getApplicationContext())
                .load(withFramePath)
                .error(R.drawable.img_place_holder)
                .placeholder(R.drawable.img_place_holder)
                .into(downloadedImg);

        closeImg.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (!activity.isFinishing() && !activity.isDestroyed() && watchAdDialog.isShowing()) {
                    watchAdDialog.dismiss();
                }
            }
        });

        watchToDownloadLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (!activity.isFinishing() && !activity.isDestroyed() && watchAdDialog.isShowing()) {
                    watchAdDialog.dismiss();
                }
                MyAdsManager.displayRewardedAd(activity, () -> {
                    postExportCount(withFramePath, isShare);
                });
            }
        });

        watchAdDialog.show();
    }

    private void postExportCount(String withFramePath, Boolean isShare) {
        showPleaseWaitDialog();
        ApiEndpoints apiService = RetrofitClient.getInstance().create(ApiEndpoints.class);

        Call<PostExport> call = apiService.postExportCount(postID, MyPreference.get_UserId());
        call.enqueue(new Callback<PostExport>() {
            @Override
            public void onResponse(@NonNull Call<PostExport> call, @NonNull Response<PostExport> response) {
                dismissPleaseWaitDialog();
                if (response.isSuccessful()) {
                    finalDownloadImage(withFramePath, isShare);
                } else {
                    Toast.makeText(activity, "Something went wrong ! Try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostExport> call, @NonNull Throwable t) {
                dismissPleaseWaitDialog();
                Toast.makeText(activity, "Something went wrong ! Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void finalDownloadImage(String finalImagePath, Boolean isShare) {
        File appFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + activity.getResources().getString(R.string.app_name));
        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }
        MyUtil.downloadFinalImageWithOutClose(activity, new File(finalImagePath), appFolder, isShare);
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void showPleaseWaitDialog() {
        try {
            pleaseWaitDialog = new Dialog(activity);
            pleaseWaitDialog.setContentView(R.layout.dialog_please_wait);
            if (pleaseWaitDialog.getWindow() != null) {
                pleaseWaitDialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                pleaseWaitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            pleaseWaitDialog.setCanceledOnTouchOutside(false);
            pleaseWaitDialog.setCancelable(false);

            if (!activity.isFinishing() && !activity.isDestroyed() && pleaseWaitDialog != null) {
                pleaseWaitDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissPleaseWaitDialog() {
        if (!activity.isFinishing() && !activity.isDestroyed() && pleaseWaitDialog != null && pleaseWaitDialog.isShowing()) {
            pleaseWaitDialog.dismiss();
        }
    }

    private static class AdViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flNative;
        LinearLayout llNative;

        public AdViewHolder(View inflate) {
            super(inflate);
            flNative = inflate.findViewById(R.id.flNative);
            llNative = inflate.findViewById(R.id.llNative);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView tempMainImg;
        RelativeLayout layoutTemp;
        DiscreteScrollView itemViewPager;
        PageIndicatorView pageIndicatorView;
        LinearLayout layEdit;
        LinearLayout layDownload;
        AppCompatImageView layShare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tempMainImg = itemView.findViewById(R.id.tempMainImg);
            layoutTemp = itemView.findViewById(R.id.layoutTemp);
            itemViewPager = itemView.findViewById(R.id.itemViewPager);
            pageIndicatorView = itemView.findViewById(R.id.pageIndicatorView);
            layEdit = itemView.findViewById(R.id.layEdit);
            layDownload = itemView.findViewById(R.id.layDownload);
            layShare = itemView.findViewById(R.id.layShare);
        }
    }
}
