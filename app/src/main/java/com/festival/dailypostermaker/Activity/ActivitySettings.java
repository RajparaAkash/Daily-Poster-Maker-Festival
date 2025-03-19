package com.festival.dailypostermaker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.BuildConfig;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivitySettingsBinding;

import java.io.File;
import java.util.Locale;

public class ActivitySettings extends ActivityBase {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.settingVersionTxt.setText(String.format("V %s", BuildConfig.VERSION_NAME));

        Locale locale = new Locale(MyPreference.get_LanguageCode());
        binding.languageTxt.setText(locale.getDisplayLanguage(locale));

        setupClickListeners();
        handleBackPress();
    }

    private void setupClickListeners() {
        binding.backLay.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.settingUpgradeImg.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                /*startActivity(new Intent(SettingActivity.this, UpgradeNowActivity.class));*/
            }
        });

        binding.downloadsLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (getDownloadFileCount() > 0) {
                    MyAdsManager.displayInterstitialSecondWise(ActivitySettings.this, () -> {
                        startActivity(new Intent(ActivitySettings.this, ActivityDownloads.class));
                    });
                } else {
                    startActivity(new Intent(ActivitySettings.this, ActivityDownloads.class));
                }
            }
        });

        binding.feedbackLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                MyAdsManager.displayInterstitialSecondWise(ActivitySettings.this, () -> {
                    startActivity(new Intent(ActivitySettings.this, ActivityFeedback.class));
                });
            }
        });

        binding.languageLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                MyAdsManager.displayInterstitialSecondWise(ActivitySettings.this, () -> {
                    startActivity(new Intent(ActivitySettings.this, ActivityLanguageSelection.class));
                });
            }
        });

        binding.aboutUsLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Toast.makeText(ActivitySettings.this, "Version : " + BuildConfig.VERSION_NAME, Toast.LENGTH_SHORT).show();
            }
        });

        binding.privacyLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                openLinkInBrowser(ActivitySettings.this, MyUtil.privacy_policy);
            }
        });

        binding.termsConditionLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                openLinkInBrowser(ActivitySettings.this, MyUtil.terms_condition);
            }
        });

        binding.refundLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                openLinkInBrowser(ActivitySettings.this, MyUtil.refund_cancel);
            }
        });
    }

    private int getDownloadFileCount() {
        try {
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
            if (!folder.exists()) folder.mkdirs();
            return folder.isDirectory() ? folder.list().length : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
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