package com.festival.dailypostermaker.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.MyUtils.FileUtils;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivitySharePostBinding;

import java.io.File;

public class ActivitySharePost extends ActivityBase {

    private String finalImagePath;
    private boolean isShare = false;
    private boolean isFromCreation = false;

    public ActivitySharePostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySharePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null) {
            finalImagePath = intent.getStringExtra("finalImagePath");
            isShare = intent.getBooleanExtra("isShare", false);
            isFromCreation = intent.getBooleanExtra("isFromCreation", false);
        } else {
            getOnBackPressedDispatcher().onBackPressed();
        }

        setupAd();

        if (isFromCreation) {
            binding.backImg.setImageResource(R.drawable.icon_back);
        } else {
            binding.backImg.setImageResource(R.drawable.icon_home);
        }

        Glide.with(this).load(finalImagePath)
                .placeholder(R.drawable.img_place_holder)
                .error(R.drawable.img_place_holder)
                .into(binding.finalImg);

        if (isShare) {
            new Handler().postDelayed(this::shareFileOpen, 500);
        }

        setupClickListeners();
        /*startReviewFlow();*/
        handleBackPress();
    }

    private void setupAd() {
        MyAdsManager.displayAdaptiveBannerAdCollapsable(this, findViewById(R.id.llBanner));
    }

    private void setupClickListeners() {
        binding.backLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (isFromCreation) {
                    getOnBackPressedDispatcher().onBackPressed();
                } else {
                    MyUtil.isUpdate = false;
                    MyUtil.isAlternative = false;
                    MyAdsManager.displayInterstitialSecondWise(ActivitySharePost.this, () -> {
                        Intent intent = new Intent(ActivitySharePost.this, ActivityMain.class);
                        intent.putExtra("TemplateId", "0");
                        intent.putExtra("PostType", "0");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    });
                }
            }
        });

        binding.shareLayWhatsapp.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (isAppInstalled("com.whatsapp")) {
                    shareFileOpen("com.whatsapp");
                } else if (isAppInstalled("com.whatsapp.w4b")) {
                    shareFileOpen("com.whatsapp.w4b");
                } else {
                    Toast.makeText(ActivitySharePost.this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.shareLayFacebook.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (isAppInstalled("com.facebook.katana")) {
                    shareFileOpen("com.facebook.katana");
                } else {
                    Toast.makeText(ActivitySharePost.this, "Facebook is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.shareLayInstagram.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (isAppInstalled("com.instagram.android")) {
                    shareFileOpen("com.instagram.android");
                } else {
                    Toast.makeText(ActivitySharePost.this, "Instagram is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.shareLayTwitter.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (isAppInstalled("com.twitter.android")) {
                    shareFileOpen("com.twitter.android");
                } else {
                    Toast.makeText(ActivitySharePost.this, "Twitter is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.shareLayMore.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                shareFileOpen();
            }
        });
    }

    /*private void startReviewFlow() {
        try {
            ReviewManager reviewManager = ReviewManagerFactory.create(ActivitySharePost.this);
            Task<ReviewInfo> request = reviewManager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // We can get the ReviewInfo object
                    ReviewInfo reviewInfo = task.getResult();
                    launchReviewFlow(reviewManager, reviewInfo);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*private void launchReviewFlow(ReviewManager reviewManager, ReviewInfo reviewInfo) {
        Task<Void> reviewFlow = reviewManager.launchReviewFlow(ActivitySharePost.this, reviewInfo);
        reviewFlow.addOnCompleteListener(task -> {
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
        });
    }*/

    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void shareFileOpen(String packageName) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setPackage(packageName); // Set the package name for the target app
            String mimeType = FileUtils.getMimeType(finalImagePath);
            shareIntent.setType(mimeType);
            // Set the file URI as the intent data
            File file = new File(finalImagePath);
            Uri uri = FileProvider.getUriForFile(ActivitySharePost.this, getApplicationContext().getPackageName() + ".provider", file);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(shareIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void shareFileOpen() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String mimeType = FileUtils.getMimeType(finalImagePath);
        shareIntent.setType(mimeType);

        // Set the file URI as the intent data
        File file = new File(finalImagePath);
        Uri uri = FileProvider.getUriForFile(ActivitySharePost.this, getApplicationContext().getPackageName() + ".provider", file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Via"));
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