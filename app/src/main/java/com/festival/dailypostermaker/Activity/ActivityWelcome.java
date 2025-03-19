package com.festival.dailypostermaker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.viewpager2.widget.ViewPager2;

import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.Adapter.AdapterWelcome;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivityWelcomeBinding;

public class ActivityWelcome extends ActivityBase {

    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupAd();
        setupViewPager();
        setupClickListeners();
        handleBackPress();
    }

    private void setupAd() {
        MyAdsManager.displayNativeSmallAd(this, findViewById(R.id.flNativeBanner));
    }

    private void setupViewPager() {
        AdapterWelcome adapterWelcome = new AdapterWelcome(getSupportFragmentManager(), getLifecycle());
        binding.viewPager.setAdapter(adapterWelcome);
        binding.dotsIndicator.attachTo(binding.viewPager);
        binding.viewPager.setCurrentItem(0, true);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateUIForPage(position);
            }
        });
    }

    private void updateUIForPage(int position) {
        boolean isLastPage = position == 2;
        binding.getStartedTxt.setVisibility(isLastPage ? View.VISIBLE : View.GONE);
        binding.nextImg.setVisibility(isLastPage ? View.GONE : View.VISIBLE);
    }

    private void setupClickListeners() {
        binding.nextImg.setOnClickListener(v -> {
            int nextPage = binding.viewPager.getCurrentItem() + 1;
            if (nextPage < 3) {
                binding.viewPager.setCurrentItem(nextPage, true);
            }
        });

        binding.getStartedTxt.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                MyAdsManager.displayInterstitialSecondWise(ActivityWelcome.this, () -> {
                    navigateToDashboard();
                });
            }
        });
    }

    private void navigateToDashboard() {
        MyPreference.set_FirstTimeUser(false);
        Intent intent = new Intent(this, ActivityMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("TemplateId", "0");
        intent.putExtra("PostType", "0");
        startActivity(intent);
        finish();
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(ActivityWelcome.this, ActivityAppExit1.class));
            }
        });
    }
}