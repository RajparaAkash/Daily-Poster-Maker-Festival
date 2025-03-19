package com.festival.dailypostermaker.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.festival.dailypostermaker.Api.ApiEndpoints;
import com.festival.dailypostermaker.Api.RetrofitClient;
import com.festival.dailypostermaker.Model.PostExport;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.Adapter.AdapterPostCreate;
import com.festival.dailypostermaker.Model.PostDetails;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivityCreateSavedPostBinding;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCreateSavedPost extends ActivityBase {

    private AdapterPostCreate adapterPostCreate;
    private String withFramePath = "";
    private String withOutFramePath;

    private final ArrayList<PostDetails> framePersonalList = new ArrayList<>();
    private final ArrayList<PostDetails> frameBusinessList = new ArrayList<>();
    private ArrayList<PostDetails> frameDetailsList = new ArrayList<>();
    private Dialog pleaseWaitDialog;

    private ActivityCreateSavedPostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateSavedPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        withOutFramePath = getIntent().getStringExtra("withOutFrame");

        framePersonalList.add(new PostDetails(R.layout.frame_personal_1_1));
        framePersonalList.add(new PostDetails(R.layout.frame_personal_1_2));
        framePersonalList.add(new PostDetails(R.layout.frame_personal_1_3));
        framePersonalList.add(new PostDetails(R.layout.frame_personal_1_4));
        framePersonalList.add(new PostDetails(R.layout.frame_personal_2_1));
        framePersonalList.add(new PostDetails(R.layout.frame_personal_2_2));

        frameBusinessList.add(new PostDetails(R.layout.frame_business_1_1));
        frameBusinessList.add(new PostDetails(R.layout.frame_business_1_2));
        frameBusinessList.add(new PostDetails(R.layout.frame_business_1_3));
        frameBusinessList.add(new PostDetails(R.layout.frame_business_1_4));

        if (MyPreference.get_IsProfileBusiness()) {
            frameDetailsList = frameBusinessList;
            binding.pLayoutTemp.setVisibility(View.GONE);
            binding.bLayoutTemp.setVisibility(View.VISIBLE);
        } else {
            frameDetailsList = framePersonalList;
            binding.bLayoutTemp.setVisibility(View.GONE);
            binding.pLayoutTemp.setVisibility(View.VISIBLE);
        }

        setupAd();
        setupClickListeners();
        handleBackPress();
        setupRecyclerView();
    }

    private void setupAd() {
        MyAdsManager.displayAdaptiveBannerAdCollapsable(this, findViewById(R.id.llBanner));
    }

    private void setupClickListeners() {
        binding.backLay.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.editTxt.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                MyAdsManager.displayInterstitialSecondWise(ActivityCreateSavedPost.this, () -> {
                    startActivity(new Intent(ActivityCreateSavedPost.this, ActivityEditDetails.class));
                });
            }
        });

        binding.layDownload.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (MyPreference.get_IsProfileBusiness()) {
                    downloadData(binding.bLayoutTemp, false);
                } else {
                    downloadData(binding.pLayoutTemp, false);
                }
            }
        });

        binding.layShare.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (MyPreference.get_IsProfileBusiness()) {
                    downloadData(binding.bLayoutTemp, true);
                } else {
                    downloadData(binding.pLayoutTemp, true);
                }
            }
        });
    }

    private void setupRecyclerView() {
        Glide.with(ActivityCreateSavedPost.this).load(withOutFramePath)
                .placeholder(R.drawable.img_place_holder)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.pTempMainImg.setImageDrawable(resource);
                        binding.bTempMainImg.setImageDrawable(resource);

                        // Use Palette API to extract colors
                        Palette.from(((BitmapDrawable) resource).getBitmap()).generate(palette -> {
                            Palette.Swatch dominantSwatch = palette.getDominantSwatch();
                            if (dominantSwatch != null) {
                                int dominantColor = palette.getMutedColor(ContextCompat.getColor(ActivityCreateSavedPost.this, R.color.defaultPostBgColor));
                                binding.pLayoutTemp.setBackgroundColor(dominantColor);
                                binding.bLayoutTemp.setBackgroundColor(dominantColor);
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle placeholder clearing
                    }
                });

        adapterPostCreate = new AdapterPostCreate(ActivityCreateSavedPost.this, frameDetailsList, withOutFramePath);
        binding.pItemViewPager.setAdapter(adapterPostCreate);
        binding.bItemViewPager.setAdapter(adapterPostCreate);

        binding.indicatorView.setSliderColor(ContextCompat.getColor(this, R.color.indicatorUnselectColor), ContextCompat.getColor(this, R.color.colorPrimary));
        binding.indicatorView.setSliderWidth(getResources().getDimension(R.dimen.dp_10));
        binding.indicatorView.setSliderHeight(getResources().getDimension(R.dimen.dp_5));
        binding.indicatorView.setPageSize(frameDetailsList.size());

        binding.pItemViewPager.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
            @Override
            public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                binding.indicatorView.onPageSelected(adapterPosition);
            }
        });

        binding.bItemViewPager.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
            @Override
            public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                binding.indicatorView.onPageSelected(adapterPosition);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (MyUtil.isAlternative) {

            if (MyPreference.get_IsProfileBusiness()) {
                frameDetailsList = frameBusinessList;
                binding.pLayoutTemp.setVisibility(View.GONE);
                binding.bLayoutTemp.setVisibility(View.VISIBLE);
            } else {
                frameDetailsList = framePersonalList;
                binding.bLayoutTemp.setVisibility(View.GONE);
                binding.pLayoutTemp.setVisibility(View.VISIBLE);
            }
            setupRecyclerView();
            return;
        }

        if (MyUtil.isUpdate) {
            adapterPostCreate.notifyDataSetChanged();
        }
    }

    public void downloadData(View saveView, boolean isShare) {
        showPleaseWaitDialog();
        Bitmap bitmap = getBitmapFromView(saveView);
        String fileName = "Daily_Poster_" + System.currentTimeMillis() + ".png";

        DownloadImageTask downloadTask = new DownloadImageTask(Executors.newSingleThreadExecutor(),
                new Handler(Looper.getMainLooper()), bitmap, fileName, isShare);
        downloadTask.startDownload();
    }

    public class DownloadImageTask {

        private final Executor executor;
        private final Handler handler;
        private final Bitmap bitmap;
        private final String fileName;
        private final Boolean isShare;

        public DownloadImageTask(Executor executor, Handler handler, Bitmap bitmap, String fileName, Boolean isShare) {
            this.executor = executor;
            this.handler = handler;
            this.bitmap = bitmap;
            this.fileName = fileName;
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
                dismissPleaseWaitDialog();
                if (MyPreference.get_IsPremium()) {
                    Intent intent = new Intent(ActivityCreateSavedPost.this, ActivitySharePost.class);
                    intent.putExtra("finalImagePath", withFramePath);
                    intent.putExtra("isFromCreation", false);
                    intent.putExtra("isShare", isShare);
                    startActivity(intent);
                } else {
                    if (MyUtil.remainingFreePost > 0) {
                        MyAdsManager.displayInterstitialSecondWise(ActivityCreateSavedPost.this, new MyAdsManager.NextStepListener() {
                            @Override
                            public void onNextStep() {
                                /*Intent intent = new Intent(ActivityCreateSavedPost.this, WatchVideoToExportActivity.class);
                                intent.putExtra("postID", 0);
                                intent.putExtra("withFramePath", withFramePath);
                                intent.putExtra("isShare", isShare);
                                startActivity(intent);*/
                            }
                        });
                    } else {
                        MyAdsManager.displayInterstitialSecondWise(ActivityCreateSavedPost.this, new MyAdsManager.NextStepListener() {
                            @Override
                            public void onNextStep() {
                                /*Intent intent = new Intent(ActivityCreateSavedPost.this, WatchVideoToExport2Activity.class);
                                intent.putExtra("withFramePath", withFramePath);
                                intent.putExtra("withOutFramePath", withOutFramePath);
                                intent.putExtra("isShare", isShare);
                                startActivity(intent);*/
                            }
                        });
                    }
                }
            } else {
                dismissPleaseWaitDialog();
                Log.e("TAG", "Failed to download: ");
            }
        }

        // Implement saveImageBitmap method here (assuming it's a helper method)
        private boolean saveImageBitmap(Bitmap bitmap, String fileName) {
            File folder;
            if (MyPreference.get_IsPremium()) {
                folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + getResources().getString(R.string.app_name));
            } else {
                folder = new File(getCacheDir(), "TEMP");
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

                MediaScannerConnection.scanFile(ActivityCreateSavedPost.this, new String[]{new File(file.getPath()).getAbsolutePath()}, null, null);
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
        BottomSheetDialog watchAdDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_watch_ad_to_download, null);
        watchAdDialog.setContentView(bottomSheetView);
        watchAdDialog.setCancelable(false);

        AppCompatImageView downloadedImg = watchAdDialog.findViewById(R.id.downloadedImg);
        AppCompatImageView closeImg = watchAdDialog.findViewById(R.id.closeImg);
        FrameLayout watchToDownloadLay = watchAdDialog.findViewById(R.id.watchToDownloadLay);

        Glide.with(getApplicationContext())
                .load(withFramePath)
                .error(R.drawable.img_place_holder)
                .placeholder(R.drawable.img_place_holder)
                .into(downloadedImg);

        closeImg.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (!isFinishing() && !isDestroyed() && watchAdDialog.isShowing()) {
                    watchAdDialog.dismiss();
                }
            }
        });

        watchToDownloadLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (!isFinishing() && !isDestroyed() && watchAdDialog.isShowing()) {
                    watchAdDialog.dismiss();
                }
                MyAdsManager.displayRewardedAd(ActivityCreateSavedPost.this, () -> {
                    postExportCount(withFramePath, isShare);
                });
            }
        });

        watchAdDialog.show();
    }

    private void postExportCount(String withFramePath, Boolean isShare) {
        showPleaseWaitDialog();
        ApiEndpoints apiService = RetrofitClient.getInstance().create(ApiEndpoints.class);

        Call<PostExport> call = apiService.postExportCount(0, MyPreference.get_UserId());
        call.enqueue(new Callback<PostExport>() {
            @Override
            public void onResponse(@NonNull Call<PostExport> call, @NonNull Response<PostExport> response) {
                dismissPleaseWaitDialog();
                if (response.isSuccessful()) {
                    finalDownloadImage(withFramePath, isShare);
                } else {
                    Toast.makeText(ActivityCreateSavedPost.this, "Something went wrong ! Try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostExport> call, @NonNull Throwable t) {
                dismissPleaseWaitDialog();
                Toast.makeText(ActivityCreateSavedPost.this, "Something went wrong ! Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showPleaseWaitDialog() {
        try {
            pleaseWaitDialog = new Dialog(this);
            pleaseWaitDialog.setContentView(R.layout.dialog_please_wait);
            if (pleaseWaitDialog.getWindow() != null) {
                pleaseWaitDialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                pleaseWaitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            pleaseWaitDialog.setCanceledOnTouchOutside(false);
            pleaseWaitDialog.setCancelable(false);

            if (!isFinishing() && !isDestroyed() && pleaseWaitDialog != null) {
                pleaseWaitDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissPleaseWaitDialog() {
        if (!isFinishing() && !isDestroyed() && pleaseWaitDialog != null && pleaseWaitDialog.isShowing()) {
            pleaseWaitDialog.dismiss();
        }
    }

    private void finalDownloadImage(String finalImagePath, Boolean isShare) {
        File appFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + getResources().getString(R.string.app_name));
        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }
        MyUtil.downloadFinalImageWithOutClose(this, new File(finalImagePath), appFolder, isShare);
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
}