package com.festival.dailypostermaker.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaDrm;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.festival.dailypostermaker.AdPlacement.GoogleMobileAdsConsentManager;
import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.Api.ApiEndpoints;
import com.festival.dailypostermaker.BuildConfig;
import com.festival.dailypostermaker.Model.GetUserData.UserData;
import com.festival.dailypostermaker.MyApplication;
import com.festival.dailypostermaker.MyUtils.DialogUtil;
import com.festival.dailypostermaker.MyUtils.LocaleHelper;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.MyUtils.NetworkUtil;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivitySplashScreenBinding;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;

import java.security.MessageDigest;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivitySplashScreen extends AppCompatActivity {

    private static final String TAG = ActivitySplashScreen.class.getSimpleName();
    Handler tokenCheckHandler = new Handler();
    Runnable tokenCheckRunnable;
    int tokenCheckDelay = 1000;
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private final AtomicBoolean gatherConsentFinished = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private int checkApiResponseSuccess = 0;
    private long secondsRemaining;
    Handler apiCheckHandler = new Handler();
    Runnable apiCheckRunnable;
    int apiCheckDelay = 1000;
    private String templateId = "0";
    private String postType = "0";

    public ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (MyPreference.get_IsPremium() || MyPreference.get_Ad() == 0) {
            binding.adTxt.setVisibility(View.GONE);
        } else {
            binding.adTxt.setVisibility(View.VISIBLE);
        }

        if (MyPreference.get_DeviceId().isEmpty()) {
            MyUtil.device_androidId = getWidevineSN();
            MyPreference.set_DeviceId(MyUtil.device_androidId);
        } else {
            MyUtil.device_androidId = MyPreference.get_DeviceId();
        }

        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                templateId = extras.getString("TemplateId", "0");
                postType = extras.getString("PostType", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (NetworkUtil.isNetworkConnected(this)) {
            startConsentManger();
            checkApiResponseSuccess = 0;
            if (MyPreference.get_Token().equalsIgnoreCase("")) {
                tokenCheckHandler.postDelayed(tokenCheckRunnable = new Runnable() {
                    public void run() {
                        if (!MyPreference.get_Token().equalsIgnoreCase("")) {
                            getUserData();
                            tokenCheckHandler.removeCallbacks(tokenCheckRunnable);
                        } else {
                            tokenCheckHandler.postDelayed(tokenCheckRunnable, tokenCheckDelay);
                        }
                    }
                }, tokenCheckDelay);
            } else {
                getUserData();
            }
        } else {
            Dialog noInternetDialog = DialogUtil.getDialog(this, R.layout.dialog_no_internet);
            noInternetDialog.setCancelable(false);
            TextView tryAgainTxt = noInternetDialog.findViewById(R.id.tryAgainTxt);
            tryAgainTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (NetworkUtil.isNetworkConnected(ActivitySplashScreen.this)) {
                        noInternetDialog.dismiss();
                        startConsentManger();
                        checkApiResponseSuccess = 0;
                        if (MyPreference.get_Token().equalsIgnoreCase("")) {
                            tokenCheckHandler.postDelayed(tokenCheckRunnable = new Runnable() {
                                public void run() {
                                    if (!MyPreference.get_Token().equalsIgnoreCase("")) {
                                        getUserData();
                                        tokenCheckHandler.removeCallbacks(tokenCheckRunnable);
                                    } else {
                                        tokenCheckHandler.postDelayed(tokenCheckRunnable, tokenCheckDelay);
                                    }
                                }
                            }, tokenCheckDelay);
                        } else {
                            getUserData();
                        }
                    } else {
                        Toast.makeText(ActivitySplashScreen.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            noInternetDialog.show();
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        });
    }

    private void startConsentManger() {
        if (MyPreference.get_IsPremium()) {
            apiCheckHandler.postDelayed(apiCheckRunnable = new Runnable() {
                public void run() {
                    if (checkApiResponseSuccess == 1) {
                        goToNext();
                        apiCheckHandler.removeCallbacks(apiCheckRunnable);
                    } else if (checkApiResponseSuccess == 2) {
                        Toast.makeText(ActivitySplashScreen.this, "Something went wrong ! Try again", Toast.LENGTH_SHORT).show();
                        apiCheckHandler.removeCallbacks(apiCheckRunnable);
                    } else {
                        apiCheckHandler.postDelayed(apiCheckRunnable, apiCheckDelay);
                    }
                }
            }, apiCheckDelay);
            return;
        }

        createTimer();

        googleMobileAdsConsentManager =
                GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
        googleMobileAdsConsentManager.gatherConsent(
                this,
                consentError -> {
                    Log.e(TAG, "onCreate1: ");
                    if (consentError != null) {
                        // Consent not obtained in current session.
                        Log.e(TAG, String.format("%s: %s", consentError.getErrorCode(), consentError.getMessage()));
                    }
                    Log.e(TAG, "onCreate2: " + googleMobileAdsConsentManager.canRequestAds());

                    gatherConsentFinished.set(true);

                    if (googleMobileAdsConsentManager.canRequestAds()) {
                        MyApplication.canRequestAds = true;
                        initializeMobileAdsSdk();
                    }
                    if (secondsRemaining <= 0) {
                        if (checkApiResponseSuccess == 0) {
                            apiCheckHandler.postDelayed(apiCheckRunnable = new Runnable() {
                                public void run() {
                                    if (checkApiResponseSuccess == 1) {
                                        goToNext();
                                        apiCheckHandler.removeCallbacks(apiCheckRunnable);
                                    } else if (checkApiResponseSuccess == 2) {
                                        Toast.makeText(ActivitySplashScreen.this, "Something went wrong ! Try again", Toast.LENGTH_SHORT).show();
                                        apiCheckHandler.removeCallbacks(apiCheckRunnable);
                                    } else {
                                        apiCheckHandler.postDelayed(apiCheckRunnable, apiCheckDelay);
                                    }
                                }
                            }, apiCheckDelay);
                        } else if (checkApiResponseSuccess == 1) {
                            goToNext();
                        } else {
                            Toast.makeText(ActivitySplashScreen.this, "Something went wrong ! Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // This sample attempts to load ads using consent obtained in the previous session.
        Log.e(TAG, "onCreate3: " + googleMobileAdsConsentManager.canRequestAds());
        if (googleMobileAdsConsentManager.canRequestAds()) {
            MyApplication.canRequestAds = true;
            initializeMobileAdsSdk();
        }
    }

    private void createTimer() {
        CountDownTimer countDownTimer =
                new CountDownTimer(3000, 1000) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long millisUntilFinished) {
                        secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1;
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onFinish() {
                        secondsRemaining = 0;
                        Log.e(TAG, "onFinish: " + googleMobileAdsConsentManager.canRequestAds());
                        if (gatherConsentFinished.get()) {
                            if (googleMobileAdsConsentManager.canRequestAds()) {
                                MyApplication.canRequestAds = true;
                                initializeMobileAdsSdk();
                            }
                            if (secondsRemaining <= 0) {
                                if (checkApiResponseSuccess == 0) {
                                    apiCheckHandler.postDelayed(apiCheckRunnable = new Runnable() {
                                        public void run() {
                                            if (checkApiResponseSuccess == 1) {
                                                goToNext();
                                                apiCheckHandler.removeCallbacks(apiCheckRunnable);
                                            } else if (checkApiResponseSuccess == 2) {
                                                Toast.makeText(ActivitySplashScreen.this, "Something went wrong ! Try again", Toast.LENGTH_SHORT).show();
                                                apiCheckHandler.removeCallbacks(apiCheckRunnable);
                                            } else {
                                                apiCheckHandler.postDelayed(apiCheckRunnable, apiCheckDelay);
                                            }
                                        }
                                    }, apiCheckDelay);
                                } else if (checkApiResponseSuccess == 1) {
                                    goToNext();
                                } else {
                                    Toast.makeText(ActivitySplashScreen.this, "Something went wrong ! Try again", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    }
                };
        countDownTimer.start();
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }
        try {
            new Thread(() -> {
                MobileAds.initialize(ActivitySplashScreen.this, initializationStatus -> {
                });
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUserData() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyUtil.BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiEndpoints apiEndpoints = retrofit.create(ApiEndpoints.class);

        Call<UserData> call = apiEndpoints.getUserData(MyUtil.device_androidId, MyPreference.get_Token(), MyApplication.getAppDecryptKey(), BuildConfig.VERSION_CODE);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(@NonNull Call<UserData> call, @NonNull Response<UserData> response) {

                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getCode() == 2) {
                        Log.e(TAG, "onResponse: " + new Gson().toJson(response.body()));
                        UserData userData = response.body();

                        MyPreference.set_DeviceId(userData.getData().getUser().getDeviceId());
                        MyPreference.set_Token(userData.getData().getUser().getFbToken());
                        MyPreference.set_UserId(String.valueOf(userData.getData().getUser().getId()));

                        if (userData.getData().getUser().getUserPremiumStatus() == 1) {
                            MyPreference.set_IsPremium(true);
                        } else {
                            MyPreference.set_IsPremium(false);
                        }

                        if (userData.getData() != null) {
                            MyUtil.remainingFreePost = userData.getData().getUser().getRemainingFreePost();
                            MyUtil.totalFreePost = userData.getData().getUser().getFreePost();

                            if (userData.getData().getPaymentSetting() != null) {
                                MyUtil.paymentMode = userData.getData().getPaymentSetting().getPaymentMode();
                            }
                        }

                        /* Personal */
                        if (userData.getData().getUser().getName() != null && userData.getData().getUser().getMobile() != null
                                && userData.getData().getUser().getAboutUs() != null) {

                            MyPreference.set_P_Name(userData.getData().getUser().getName());
                            MyPreference.set_P_MobileNo(userData.getData().getUser().getMobile());
                            MyPreference.set_P_About(userData.getData().getUser().getAboutUs());

                            if (userData.getData().getUser().getProfile() != null) {
                                MyPreference.set_P_ProfilePath(userData.getData().getUser().getProfile());
                            }
                            if (userData.getData().getUser().getInstagram() != null) {
                                MyPreference.set_P_Instagram(userData.getData().getUser().getInstagram());
                            }
                            if (userData.getData().getUser().getYoutube() != null) {
                                MyPreference.set_P_YouTube(userData.getData().getUser().getYoutube());
                            }
                            if (userData.getData().getUser().getFacebook() != null) {
                                MyPreference.set_P_Facebook(userData.getData().getUser().getFacebook());
                            }
                            if (userData.getData().getUser().getTwitter() != null) {
                                MyPreference.set_P_Twitter(userData.getData().getUser().getTwitter());
                            }
                            if (userData.getData().getUser().getEmail() != null) {
                                MyPreference.set_P_Email(userData.getData().getUser().getEmail());
                            }
                            if (userData.getData().getUser().getWebsite() != null) {
                                MyPreference.set_P_Website(userData.getData().getUser().getWebsite());
                            }
                            MyPreference.set_IsProfileAdded(true);
                        } else {
                            MyPreference.set_IsProfileAdded(false);
                        }

                        /* Business */
                        if (userData.getData().getBusiness() != null && userData.getData().getBusiness().getLogo() != null
                                && userData.getData().getBusiness().getName() != null && userData.getData().getBusiness().getDesignation() != null
                                && userData.getData().getBusiness().getAddress() != null && userData.getData().getBusiness().getWhatsapp() != null) {

                            MyPreference.set_B_BusinessId(userData.getData().getBusiness().getId());
                            MyPreference.set_B_Name(userData.getData().getBusiness().getName());
                            MyPreference.set_B_Designation(userData.getData().getBusiness().getDesignation());
                            MyPreference.set_B_ProfilePath(userData.getData().getBusiness().getLogo());
                            MyPreference.set_B_Address(userData.getData().getBusiness().getAddress());
                            MyPreference.set_B_Whatsapp(userData.getData().getBusiness().getWhatsapp());

                            if (userData.getData().getBusiness().getInstagram() != null) {
                                MyPreference.set_B_Instagram(userData.getData().getBusiness().getInstagram());
                            }
                            if (userData.getData().getBusiness().getYoutube() != null) {
                                MyPreference.set_B_YouTube(userData.getData().getBusiness().getYoutube());
                            }
                            if (userData.getData().getBusiness().getFacebook() != null) {
                                MyPreference.set_B_Facebook(userData.getData().getBusiness().getFacebook());
                            }
                            if (userData.getData().getBusiness().getTwitter() != null) {
                                MyPreference.set_B_Twitter(userData.getData().getBusiness().getTwitter());
                            }
                            if (userData.getData().getBusiness().getEmail() != null) {
                                MyPreference.set_B_Email(userData.getData().getBusiness().getEmail());
                            }
                            if (userData.getData().getBusiness().getWebsite() != null) {
                                MyPreference.set_B_Website(userData.getData().getBusiness().getWebsite());
                            }
                            MyPreference.set_IsBusinessAdded(true);
                        } else {
                            MyPreference.set_IsBusinessAdded(false);
                        }

                        /* AdPlacement */
                        if (userData.getData().getAdPlacement() != null) {
                            if (userData.getData().getAdPlacement().getPrivacyPolicy() != null) {
                                MyPreference.set_PrivacyPolicy(userData.getData().getAdPlacement().getPrivacyPolicy());
                                MyUtil.privacy_policy = MyPreference.get_PrivacyPolicy();
                            }
                            if (userData.getData().getAdPlacement().getTermsCondition() != null) {
                                MyPreference.set_TermsCondition(userData.getData().getAdPlacement().getTermsCondition());
                                MyUtil.terms_condition = MyPreference.get_TermsCondition();
                            }
                            if (userData.getData().getAdPlacement().getRefundCancel() != null) {
                                MyPreference.set_RefundCancellation(userData.getData().getAdPlacement().getRefundCancel());
                                MyUtil.refund_cancel = MyPreference.get_RefundCancellation();
                            }

                            if (userData.getData().getAdPlacement().getAds() != null) {
                                MyPreference.set_Ad(userData.getData().getAdPlacement().getAds());
                            }

                            /*if (BuildConfig.DEBUG) {
                                MyPreference.set_Ad(0);
                            }*/

                            if (userData.getData().getAdPlacement().getAdPlace() != null) {
                                MyPreference.set_AdPlace(userData.getData().getAdPlacement().getAdPlace());
                            }

                            if (userData.getData().getAdPlacement().getPriority() != null) {
                                MyPreference.set_AdPriority(userData.getData().getAdPlacement().getPriority());
                            }

                            if (userData.getData().getAdPlacement().getAdmob() != null) {
                                if (userData.getData().getAdPlacement().getAdmob().getAmBanner() != null) {
                                    MyPreference.set_AmBanner(userData.getData().getAdPlacement().getAdmob().getAmBanner());
                                }
                                if (userData.getData().getAdPlacement().getAdmob().getAmInterstitial() != null) {
                                    MyPreference.set_AmInterstitial(userData.getData().getAdPlacement().getAdmob().getAmInterstitial());
                                }
                                if (userData.getData().getAdPlacement().getAdmob().getAmRewardedVideo() != null) {
                                    MyPreference.set_AmRewarded(userData.getData().getAdPlacement().getAdmob().getAmRewardedVideo());
                                }
                                if (userData.getData().getAdPlacement().getAdmob().getAmNative() != null) {
                                    MyPreference.set_AmNative(userData.getData().getAdPlacement().getAdmob().getAmNative());
                                }
                                if (userData.getData().getAdPlacement().getAdmob().getAmAppOpen() != null) {
                                    MyPreference.set_AmAppOpen(userData.getData().getAdPlacement().getAdmob().getAmAppOpen());
                                }
                                if (BuildConfig.DEBUG) {
                                    MyPreference.set_AmBanner("ca-app-pub-3940256099942544/9214589741");
                                    MyPreference.set_AmInterstitial("ca-app-pub-3940256099942544/1033173712");
                                    MyPreference.set_AmRewarded("ca-app-pub-3940256099942544/5224354917");
                                    MyPreference.set_AmNative("ca-app-pub-3940256099942544/2247696110");
                                    MyPreference.set_AmAppOpen("ca-app-pub-3940256099942544/9257395921");
                                }
                            }

                            if (userData.getData().getAdPlacement().getAdx() != null) {
                                if (userData.getData().getAdPlacement().getAdx().getAdxBanner() != null) {
                                    MyPreference.set_AdxBanner(userData.getData().getAdPlacement().getAdx().getAdxBanner());
                                }
                                if (userData.getData().getAdPlacement().getAdx().getAdxInterstitial() != null) {
                                    MyPreference.set_AdxInterstitial(userData.getData().getAdPlacement().getAdx().getAdxInterstitial());
                                }
                                if (userData.getData().getAdPlacement().getAdx().getAdxRewardedVideo() != null) {
                                    MyPreference.set_AdxRewarded(userData.getData().getAdPlacement().getAdx().getAdxRewardedVideo());
                                }
                                if (userData.getData().getAdPlacement().getAdx().getAdxNative() != null) {
                                    MyPreference.set_AdxNative(userData.getData().getAdPlacement().getAdx().getAdxNative());
                                }
                                if (userData.getData().getAdPlacement().getAdx().getAdxAppOpen() != null) {
                                    MyPreference.set_AdxAppOpen(userData.getData().getAdPlacement().getAdx().getAdxAppOpen());
                                }
                                if (BuildConfig.DEBUG) {
                                    MyPreference.set_AdxBanner("/6499/example/adaptive-banner");
                                    MyPreference.set_AdxInterstitial("/6499/example/interstitial");
                                    MyPreference.set_AdxRewarded("/6499/example/rewarded");
                                    MyPreference.set_AdxNative("/6499/example/native");
                                    MyPreference.set_AdxAppOpen("/6499/example/app-open");
                                }
                            }
                        }
                        checkApiResponseSuccess = 1;
                    } else {
                        checkApiResponseSuccess = 2;
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserData> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                checkApiResponseSuccess = 2;
            }
        });
    }

    public String getWidevineSN() {
        String sRet = "";
        String sRet2 = "";

        MediaDrm mediaDrm = null;
        try {
            UUID WIDEVINE_UUID = new UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L);
            mediaDrm = new MediaDrm(WIDEVINE_UUID);
            byte[] widevineId = mediaDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(widevineId);

            sRet = convertToHex(md.digest()); //we convert byte[] to hex for our purposes
            sRet2 = sRet + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            //WIDEVINE is not available
            sRet2 = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            return sRet2;
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (mediaDrm != null) {
                    mediaDrm.close();
                }
            } else {
                if (mediaDrm != null) {
                    mediaDrm.release();
                }
            }
        }
        return sRet2;
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte datum : data) {
            int halfbyte = (datum >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if (halfbyte <= 9) buf.append((char) ('0' + halfbyte));
                else buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = datum & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public void goToNext() {
        if (MyPreference.get_IsPremium()) {
            if (MyPreference.isFirstTimeUser()) {
                startActivity(new Intent(ActivitySplashScreen.this, ActivityLanguageSelection.class));
                finish();
            } else {
                startActivity(new Intent(ActivitySplashScreen.this, ActivityMain.class).putExtra("TemplateId", templateId)
                        .putExtra("PostType", postType));
                finish();
            }
        } else {
            MyAdsManager.displayInterstitialSplash(ActivitySplashScreen.this, () -> {
                if (MyPreference.isFirstTimeUser()) {
                    startActivity(new Intent(ActivitySplashScreen.this, ActivityLanguageSelection.class));
                    finish();
                } else {
                    startActivity(new Intent(ActivitySplashScreen.this, ActivityMain.class)
                            .putExtra("TemplateId", templateId)
                            .putExtra("PostType", postType));
                    finish();
                }
            }, findViewById(R.id.llBanner));
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LocaleHelper.onAttach(context));
    }
}