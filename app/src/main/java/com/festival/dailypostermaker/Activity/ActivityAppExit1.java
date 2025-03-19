package com.festival.dailypostermaker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;

import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivityAppExit1Binding;

public class ActivityAppExit1 extends ActivityBase {

    private final ImageView[] starImages = new ImageView[5];

    private ActivityAppExit1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppExit1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupAd();
        initializeStarViews();
        setupStarClickListeners();
        setupClickListeners();
        handleBackPress();
    }

    private void setupAd() {
        new Handler(Looper.getMainLooper()).post(() -> {
            MyAdsManager.displayNativeLargeAd(this, findViewById(R.id.flNative));
        });
    }

    private void initializeStarViews() {
        starImages[0] = binding.rateStarImg01;
        starImages[1] = binding.rateStarImg02;
        starImages[2] = binding.rateStarImg03;
        starImages[3] = binding.rateStarImg04;
        starImages[4] = binding.rateStarImg05;
    }

    private void setupStarClickListeners() {
        for (int i = 0; i < starImages.length; i++) {
            final int starCount = i + 1;
            starImages[i].setOnClickListener(v -> {
                selectStars(starCount);
                rateApp();
            });
        }
    }

    private void selectStars(int count) {
        for (int i = 0; i < starImages.length; i++) {
            int drawableRes = (i < count) ? R.drawable.icon_rate_star_selected : R.drawable.icon_rate_star_unselected;
            starImages[i].setImageDrawable(ContextCompat.getDrawable(this, drawableRes));
        }
    }

    private void setupClickListeners() {
        binding.yesTxt.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                rateApp();
            }
        });

        binding.noTxt.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                startActivity(new Intent(ActivityAppExit1.this, ActivityAppExit2.class));
                finish();
            }
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