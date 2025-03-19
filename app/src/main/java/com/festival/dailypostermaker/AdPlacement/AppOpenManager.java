package com.festival.dailypostermaker.AdPlacement;

import static com.festival.dailypostermaker.MyApplication.canRequestAds;
import static com.festival.dailypostermaker.MyApplication.isShowingAd;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.festival.dailypostermaker.Preference.MyPreference;

import java.util.Date;

public class AppOpenManager {
    private static final String TAG = AppOpenManager.class.getSimpleName();
    public AppOpenAd appOpenAd = null;
    public boolean isLoadingAd = false;
    public long loadTime = 0;

    public AppOpenManager() {
    }

    public void loadAd(Context context) {
        Log.e(TAG, "loadAd: " + !canRequestAds);
        if (!canRequestAds|| MyPreference.get_Ad() == 0) {
            return;
        }
        if (MyPreference.get_IsPremium()) {
            return;
        }
        if (isLoadingAd || isAdAvailable()) {
            return;
        }
        Log.e(TAG, "loadAd: ");
        if (MyPreference.get_AdPriority().equalsIgnoreCase("admob")) {
            loadAppOpenAd(context, MyPreference.get_AmAppOpen(), "admob", MyPreference.get_AdxAppOpen(), "adx");
        } else if (MyPreference.get_AdPriority().equalsIgnoreCase("adx")) {
            loadAppOpenAd(context, MyPreference.get_AdxAppOpen(), "adx", MyPreference.get_AmAppOpen(), "admob");
        }
    }

    private void loadAppOpenAd(Context context, String appOpenId, String adsType, String appOpenId1, String adsType1) {
        if (appOpenId == null || appOpenId.equalsIgnoreCase("") || appOpenId.equalsIgnoreCase("null")) {
            if (MyPreference.get_AdPlace().equalsIgnoreCase("multiple")) {
                if (appOpenId1 == null || appOpenId1.equalsIgnoreCase("") || appOpenId1.equalsIgnoreCase("null")) {
                    return;
                }
                isLoadingAd = true;
                AdRequest request = new AdRequest.Builder().build();
                AppOpenAd.load(context, appOpenId1, request, new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        Log.e(TAG, "loadAppOpenAd onAdLoaded: " + adsType1 + " : " + ad);
                        appOpenAd = ad;
                        isLoadingAd = false;
                        loadTime = (new Date()).getTime();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(TAG, "loadAppOpenAd onAdFailedToLoad: " + adsType1 + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                        isLoadingAd = false;
                    }
                });
            }
            return;
        }
        isLoadingAd = true;
        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(context, appOpenId, request, new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd ad) {
                Log.e(TAG, "loadAppOpenAd onAdLoaded: " + adsType + " : " + ad);
                appOpenAd = ad;
                isLoadingAd = false;
                loadTime = (new Date()).getTime();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e(TAG, "loadAppOpenAd onAdFailedToLoad: " + adsType + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                isLoadingAd = false;
                if (MyPreference.get_AdPlace().equalsIgnoreCase("multiple")) {
                    if (appOpenId1 == null || appOpenId1.equalsIgnoreCase("") || appOpenId1.equalsIgnoreCase("null")) {
                        return;
                    }
                    isLoadingAd = true;
                    AdRequest request = new AdRequest.Builder().build();
                    AppOpenAd.load(context, appOpenId1, request, new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull AppOpenAd ad) {
                            Log.e(TAG, "loadAppOpenAd onAdLoaded: " + adsType1 + " : " + ad);
                            appOpenAd = ad;
                            isLoadingAd = false;
                            loadTime = (new Date()).getTime();
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e(TAG, "loadAppOpenAd onAdFailedToLoad: " + adsType1 + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                            isLoadingAd = false;
                        }
                    });
                }
            }
        });
    }

    public boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    public void showAdIfAvailable(@NonNull final Activity activity) {
        showAdIfAvailable(
                activity,
                new AdDisplayListener() {
                    @Override
                    public void onAdDisplayed() {
                        // Empty because the user will go back to the activity that shows the ad.
                    }
                });
    }


    public void showAdIfAvailable(
            @NonNull final Activity activity,
            @NonNull AdDisplayListener adDisplayListener) {

        if (MyPreference.get_IsPremium()) {
            return;
        }

        if (isShowingAd) {
            return;
        }

        Log.e(TAG, "showAdIfAvailable: " + !isAdAvailable());
        if (!isAdAvailable()) {
            adDisplayListener.onAdDisplayed();
            loadAd(activity);
            Log.e(TAG, "showAdIfAvailable: loadAd");
            return;
        }

        appOpenAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        Log.e(TAG, "showAdIfAvailable onAdDismissedFullScreenContent: ");
                        appOpenAd = null;
                        isShowingAd = false;

                        adDisplayListener.onAdDisplayed();
                        loadAd(activity);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        Log.e(TAG, "showAdIfAvailable onAdFailedToShowFullScreenContent: " + adError.getCode() + " : " + adError.getMessage());
                        appOpenAd = null;
                        isShowingAd = false;

                        adDisplayListener.onAdDisplayed();
                        loadAd(activity);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.e(TAG, "showAdIfAvailable onAdShowedFullScreenContent: ");
                        isShowingAd = true;
                    }
                });
        appOpenAd.show(activity);
    }
}
