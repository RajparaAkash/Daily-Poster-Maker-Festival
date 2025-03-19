package com.festival.dailypostermaker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.Adapter.AdapterDownloads;
import com.festival.dailypostermaker.Model.Downloads;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivityDownloadsBinding;

import java.util.ArrayList;

public class ActivityDownloads extends ActivityBase {

    private ActivityDownloadsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownloadsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupClickListeners();
        handleBackPress();
    }

    private void setupAd() {
        MyAdsManager.displayAdaptiveBannerAdCollapsable(this, findViewById(R.id.llBanner));
    }

    private void setupRecyclerView() {
        ArrayList<Downloads> downloadsList = MyUtil.getCreationData(ActivityDownloads.this);

        if (downloadsList.isEmpty()) {
            binding.creationLL.setVisibility(View.GONE);
            binding.noDataFoundLay.setVisibility(View.VISIBLE);
        } else {
            setupAd();
            binding.noDataFoundLay.setVisibility(View.GONE);
            binding.creationLL.setVisibility(View.VISIBLE);
            binding.creationRV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

            AdapterDownloads adapterDownloads = new AdapterDownloads(ActivityDownloads.this, downloadsList, new AdapterDownloads.Click() {
                @Override
                public void onClick(int pos, String path) {
                    MyAdsManager.displayInterstitialSecondWise(ActivityDownloads.this, () -> {
                        Intent intent = new Intent(ActivityDownloads.this, ActivitySharePost.class);
                        intent.putExtra("finalImagePath", path);
                        intent.putExtra("isFromCreation", true);
                        intent.putExtra("isShare", false);
                        startActivity(intent);
                    });
                }
            });
            binding.creationRV.setAdapter(adapterDownloads);
        }
    }

    private void setupClickListeners() {
        binding.backLay.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
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