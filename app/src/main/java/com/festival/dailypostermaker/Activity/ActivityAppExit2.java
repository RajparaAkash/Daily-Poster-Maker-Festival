package com.festival.dailypostermaker.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.OnBackPressedCallback;

import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivityAppExit2Binding;

public class ActivityAppExit2 extends ActivityBase {

    public ActivityAppExit2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppExit2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupAd();
        setupClickListeners();
        handleBackPress();
    }

    private void setupAd() {
        new Handler(Looper.getMainLooper()).post(() -> {
            MyAdsManager.displayNativeLargeAd(this, findViewById(R.id.flNative));
        });
    }

    private void setupClickListeners() {
        binding.noTxt.setOnClickListener(v -> {
            finish();
        });

        binding.yesTxt.setOnClickListener(v -> {
            finishAffinity();
            finishAndRemoveTask();
            System.gc();
            System.exit(0);
        });
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        });
    }
}