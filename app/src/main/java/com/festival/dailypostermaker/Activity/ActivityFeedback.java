package com.festival.dailypostermaker.Activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.Api.ApiEndpoints;
import com.festival.dailypostermaker.Api.RetrofitClient;
import com.festival.dailypostermaker.BuildConfig;
import com.festival.dailypostermaker.Model.Feedback.Feedback;
import com.festival.dailypostermaker.MyApplication;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivityFeedbackBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityFeedback extends ActivityBase {

    private Dialog pleaseWaitDialog;
    private ActivityFeedbackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupLoadingDialog();
        setupAd();
        setupRatingBar();
        setupClickListeners();
        handleBackPress();
    }

    private void setupAd() {
        MyAdsManager.displayAdaptiveBannerAd(this, findViewById(R.id.llBanner));
    }

    private void setupRatingBar() {
        binding.ratingBar.setOnRatingChangeListener((ratingBar, rating, fromUser) -> {
            if (rating >= 4) {
                rateApp();
            }
        });
    }

    private void setupClickListeners() {
        binding.backLay.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.submitFeedbackTxt.setOnClickListener(view -> {
            String feedbackText = binding.describeEt.getText().toString().trim();
            if (!feedbackText.isEmpty()) {
                sendFeedbackToAPI(feedbackText);
            } else {
                binding.describeEt.requestFocus();
                binding.describeEt.setError("Feedback text is required");
            }
        });
    }

    private void sendFeedbackToAPI(String feedbackText) {
        showPleaseWaitDialog();
        ApiEndpoints apiEndpoints = RetrofitClient.getInstance().create(ApiEndpoints.class);
        Call<Feedback> call = apiEndpoints.sendUserFeedback(feedbackText, MyApplication.getAppDecryptKey(), BuildConfig.VERSION_CODE, MyPreference.get_UserId());

        call.enqueue(new Callback<Feedback>() {
            @Override
            public void onResponse(@NonNull Call<Feedback> call, @NonNull Response<Feedback> response) {
                dismissPleaseWaitDialog();
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("TAG", "getMessage: " + response.body().getMessage());
                    Log.d("TAG", "getCode: " + response.body().getCode());

                    Toast.makeText(ActivityFeedback.this, getResources().getString(R.string.feedback_is_submitted_successfully), Toast.LENGTH_SHORT).show();
                    getOnBackPressedDispatcher().onBackPressed();
                } else {
                    Toast.makeText(ActivityFeedback.this, getResources().getString(R.string.failed_to_submit_feedback), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Feedback> call, @NonNull Throwable t) {
                dismissPleaseWaitDialog();
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }

    public void setupLoadingDialog() {
        try {
            pleaseWaitDialog = new Dialog(this);
            pleaseWaitDialog.setContentView(R.layout.dialog_please_wait);
            if (pleaseWaitDialog.getWindow() != null) {
                pleaseWaitDialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                pleaseWaitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            pleaseWaitDialog.setCanceledOnTouchOutside(false);
            pleaseWaitDialog.setCancelable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPleaseWaitDialog() {
        if (!isFinishing() && !isDestroyed() && pleaseWaitDialog != null && !pleaseWaitDialog.isShowing()) {
            pleaseWaitDialog.show();
        }
    }

    private void dismissPleaseWaitDialog() {
        if (!isFinishing() && !isDestroyed() && pleaseWaitDialog != null && pleaseWaitDialog.isShowing()) {
            pleaseWaitDialog.dismiss();
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