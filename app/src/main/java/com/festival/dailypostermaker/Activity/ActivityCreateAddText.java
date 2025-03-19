package com.festival.dailypostermaker.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivityCreateAddTextBinding;

public class ActivityCreateAddText extends ActivityBase {

    private ActivityCreateAddTextBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAddTextBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupAd();
        setupClickListeners();
        handleBackPress();

        binding.changeFontEt.setText(MyUtil.changedText);
        binding.changeFontEt.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void setupAd() {
        MyAdsManager.displayNativeLargeAd(this, findViewById(R.id.flNative));
    }

    private void setupClickListeners() {
        binding.closeImg.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.doneTxt.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                MyUtil.hideKeyBoard(ActivityCreateAddText.this, binding.changeFontEt);

                if (binding.changeFontEt.getText().toString().isEmpty()) {
                    Toast.makeText(ActivityCreateAddText.this, "Please Enter Text", Toast.LENGTH_SHORT).show();
                    return;
                }
                MyUtil.changedText = binding.changeFontEt.getText().toString();
                getOnBackPressedDispatcher().onBackPressed();
            }
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