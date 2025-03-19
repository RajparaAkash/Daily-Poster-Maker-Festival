package com.festival.dailypostermaker.AdPlacement;


import static com.festival.dailypostermaker.MyApplication.canRequestAds;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.festival.dailypostermaker.MyApplication;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.R;

import java.util.Objects;

public class MyAdsManager {

    public static RewardedAd mRewardedVideoAd = null;
    public static boolean rewardedAdIsLoading;
    public static boolean userRewarded = false;
    public static InterstitialAd googleInterstitialAd = null;
    public static boolean adIsLoading = false;
    public static boolean isShowingAd = false;
    public static NextStepListener nextStepListener;
    public static final String TAG = MyAdsManager.class.getSimpleName();
    public static long adCloseTime = 0;
    public static long currentTime = 0;
    public static NativeAd mNativeAd = null;
    public static boolean nativeAdIsLoading = false;

    public interface NextStepListener {
        void onNextStep();
    }

    public static void displayInterstitialSecondWise(Activity activity, NextStepListener mNextStepListener) {
        nextStepListener = mNextStepListener;
        if (!canRequestAds || MyPreference.get_Ad() == 0) {
            if (nextStepListener != null) {
                nextStepListener.onNextStep();
                nextStepListener = null;
            }
            return;
        }
        if (MyPreference.get_IsPremium()) {
            if (nextStepListener != null) {
                nextStepListener.onNextStep();
                nextStepListener = null;
            }
            return;
        }
        currentTime = System.currentTimeMillis();
        if ((currentTime - adCloseTime) >= 30000) {
            InterstitialAd interstitialAd = googleInterstitialAd;
            if (interstitialAd != null) {
                interstitialAd.show(activity);
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        adCloseTime = System.currentTimeMillis();
                        isShowingAd = false;
                        if (nextStepListener != null) {
                            nextStepListener.onNextStep();
                            nextStepListener = null;
                        }
                        loadInterstitialAd(activity);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        isShowingAd = false;
                        if (nextStepListener != null) {
                            nextStepListener.onNextStep();
                            nextStepListener = null;
                        }
                        loadInterstitialAd(activity);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        isShowingAd = true;
                    }
                });
            } else {
                if (nextStepListener != null) {
                    nextStepListener.onNextStep();
                    nextStepListener = null;
                }
                loadInterstitialAd(activity);
            }
        } else {
            if (nextStepListener != null) {
                nextStepListener.onNextStep();
                nextStepListener = null;
            }
        }
    }

    public static void displayInterstitialSplash(Activity activity, NextStepListener mNextStepListener, ViewGroup viewGroup) {
        nextStepListener = mNextStepListener;
        if (!canRequestAds || MyPreference.get_Ad() == 0) {
            if (nextStepListener != null) {
                nextStepListener.onNextStep();
                nextStepListener = null;
            }
            return;
        }
        if (MyPreference.get_IsPremium()) {
            if (nextStepListener != null) {
                nextStepListener.onNextStep();
                nextStepListener = null;
            }
            return;
        }

        String bannerAd = "";
        if (MyPreference.get_AdPriority().equalsIgnoreCase("admob")) {
            bannerAd = MyPreference.get_AmBanner();
        } else if (MyPreference.get_AdPriority().equalsIgnoreCase("adx")) {
            bannerAd = MyPreference.get_AdxBanner();
        }

        if (bannerAd == null || bannerAd.equalsIgnoreCase("") || bannerAd.equalsIgnoreCase("null")) {
            if (MyPreference.get_AdPriority().equalsIgnoreCase("admob")) {
                requestToLoadInterstitialAdSplash(MyPreference.get_AmInterstitial(), activity, "admob", MyPreference.get_AdxInterstitial(), "adx", true);
            } else if (MyPreference.get_AdPriority().equalsIgnoreCase("adx")) {
                requestToLoadInterstitialAdSplash(MyPreference.get_AdxInterstitial(), activity, "adx", MyPreference.get_AmInterstitial(), "admob", true);
            }
            return;
        }

        try {
            viewGroup.setVisibility(View.VISIBLE);
            AdView adView = new AdView(activity);
            adView.setAdUnitId(bannerAd);
            AdSize adSize = getAdSize(activity, viewGroup);
            adView.setAdSize(adSize);

            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.e(TAG, "showAdmobBannerAd onAdFailedToLoad :: " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                    viewGroup.removeAllViews();
                    viewGroup.setVisibility(View.GONE);

                    if (MyPreference.get_AdPriority().equalsIgnoreCase("admob")) {
                        requestToLoadInterstitialAdSplash(MyPreference.get_AmInterstitial(), activity, "admob", MyPreference.get_AdxInterstitial(), "adx", true);
                    } else if (MyPreference.get_AdPriority().equalsIgnoreCase("adx")) {
                        requestToLoadInterstitialAdSplash(MyPreference.get_AdxInterstitial(), activity, "adx", MyPreference.get_AmInterstitial(), "admob", true);
                    }
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.e(TAG, "showAdmobBannerAd onAdLoaded");
                    viewGroup.removeAllViews();
                    viewGroup.addView(adView);

                    if (MyPreference.get_AdPriority().equalsIgnoreCase("admob")) {
                        requestToLoadInterstitialAdSplash(MyPreference.get_AmInterstitial(), activity, "admob", MyPreference.get_AdxInterstitial(), "adx", true);
                    } else if (MyPreference.get_AdPriority().equalsIgnoreCase("adx")) {
                        requestToLoadInterstitialAdSplash(MyPreference.get_AdxInterstitial(), activity, "adx", MyPreference.get_AmInterstitial(), "admob", true);
                    }
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdSwipeGestureClicked() {
                    super.onAdSwipeGestureClicked();
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
            if (nextStepListener != null) {
                nextStepListener.onNextStep();
                nextStepListener = null;
            }
        }

        /*if (MyPreference.get_AdPriority().equalsIgnoreCase("admob")) {
            requestToLoadInterstitialAdSplash(MyPreference.get_AmInterstitial(), activity, "admob", MyPreference.get_AdxInterstitial(), "adx", true);
        } else if (MyPreference.get_AdPriority().equalsIgnoreCase("adx")) {
            requestToLoadInterstitialAdSplash(MyPreference.get_AdxInterstitial(), activity, "adx", MyPreference.get_AmInterstitial(), "admob", true);
        }*/
    }

    public static void loadInterstitialAd(Activity activity) {
        if (!canRequestAds || MyPreference.get_Ad() == 0) {
            return;
        }
        if (MyPreference.get_IsPremium()) {
            return;
        }
        if (MyPreference.get_AdPriority().equalsIgnoreCase("admob")) {
            requestToLoadInterstitialAd(MyPreference.get_AmInterstitial(), activity, "admob", MyPreference.get_AdxInterstitial(), "adx", false);
        } else if (MyPreference.get_AdPriority().equalsIgnoreCase("adx")) {
            requestToLoadInterstitialAd(MyPreference.get_AdxInterstitial(), activity, "adx", MyPreference.get_AmInterstitial(), "admob", false);
        }
    }

    private static void requestToLoadInterstitialAd(String interstitialAd, Activity activity, String msg, String interstitialAd1, String msg1, Boolean isFromSplash) {
        if (interstitialAd == null || interstitialAd.equalsIgnoreCase("") || interstitialAd.equalsIgnoreCase("null")) {
            if (MyPreference.get_AdPlace().equalsIgnoreCase("multiple")) {
                if (interstitialAd1 == null || interstitialAd1.equalsIgnoreCase("") || interstitialAd1.equalsIgnoreCase("null")) {
                    if (isFromSplash) {
                        loadNativeAd(activity, false);
                    }
                    return;
                }

                if (adIsLoading) {
                    return;
                }

                adIsLoading = true;

                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(activity.getApplicationContext(), interstitialAd1, adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        Log.e(TAG, "loadAdmobInterstitialAd onAdLoaded: " + msg1 + " : " + ad);
                        googleInterstitialAd = ad;
                        adIsLoading = false;
                        if (isFromSplash) {
                            loadNativeAd(activity, false);
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(TAG, "loadAdmobInterstitialAd onAdFailedToLoad: " + msg1 + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                        adIsLoading = false;
                        if (isFromSplash) {
                            loadNativeAd(activity, false);
                        }
                    }
                });
            }
            return;
        }

        if (adIsLoading) {
            return;
        }
        adIsLoading = true;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity.getApplicationContext(), interstitialAd, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                Log.e(TAG, "loadAdmobInterstitialAd onAdLoaded: " + msg + " : " + ad);
                googleInterstitialAd = ad;
                adIsLoading = false;
                if (isFromSplash) {
                    loadNativeAd(activity, false);
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e(TAG, "loadAdmobInterstitialAd onAdFailedToLoad: " + msg + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                adIsLoading = false;
                if (MyPreference.get_AdPlace().equalsIgnoreCase("multiple")) {
                    if (interstitialAd1 == null || interstitialAd1.equalsIgnoreCase("") || interstitialAd1.equalsIgnoreCase("null")) {
                        if (isFromSplash) {
                            loadNativeAd(activity, false);
                        }
                        return;
                    }

                    if (adIsLoading) {
                        return;
                    }

                    adIsLoading = true;

                    AdRequest adRequest = new AdRequest.Builder().build();
                    InterstitialAd.load(activity.getApplicationContext(), interstitialAd1, adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd ad) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            Log.e(TAG, "loadAdmobInterstitialAd onAdLoaded: " + msg1 + " : " + ad);
                            googleInterstitialAd = ad;
                            adIsLoading = false;
                            if (isFromSplash) {
                                loadNativeAd(activity, false);
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e(TAG, "loadAdmobInterstitialAd onAdFailedToLoad: " + msg1 + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                            adIsLoading = false;
                            if (isFromSplash) {
                                loadNativeAd(activity, false);
                            }
                        }
                    });
                }
            }
        });
    }

    private static void requestToLoadInterstitialAdSplash(String interstitialAd, Activity activity, String msg, String interstitialAd1, String msg1, Boolean isFromSplash) {
        if (interstitialAd == null || interstitialAd.equalsIgnoreCase("") || interstitialAd.equalsIgnoreCase("null")) {
            if (MyPreference.get_AdPlace().equalsIgnoreCase("multiple")) {
                if (interstitialAd1 == null || interstitialAd1.equalsIgnoreCase("") || interstitialAd1.equalsIgnoreCase("null")) {
                    if (isFromSplash) {
                        loadNativeAd(activity, false);
                    }
                    if (nextStepListener != null) {
                        nextStepListener.onNextStep();
                        nextStepListener = null;
                    }
                    return;
                }

                if (adIsLoading) {
                    if (isFromSplash) {
                        loadNativeAd(activity, false);
                    }
                    if (nextStepListener != null) {
                        nextStepListener.onNextStep();
                        nextStepListener = null;
                    }
                    return;
                }

                adIsLoading = true;

                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(activity.getApplicationContext(), interstitialAd1, adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        Log.e(TAG, "loadAdmobInterstitialAd onAdLoaded: " + msg1 + " : " + ad);
                        adIsLoading = false;
                        if (isFromSplash) {
                            loadNativeAd(activity, false);
                        }
                        googleInterstitialAd = ad;
                        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                adCloseTime = System.currentTimeMillis();
                                isShowingAd = false;
                                if (nextStepListener != null) {
                                    nextStepListener.onNextStep();
                                    nextStepListener = null;
                                }
                                loadInterstitialAd(activity);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                isShowingAd = false;
                                if (nextStepListener != null) {
                                    nextStepListener.onNextStep();
                                    nextStepListener = null;
                                }
                                loadInterstitialAd(activity);
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                isShowingAd = true;
                            }
                        });
                        ad.show(activity);

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(TAG, "loadAdmobInterstitialAd onAdFailedToLoad: " + msg1 + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                        adIsLoading = false;
                        if (isFromSplash) {
                            loadNativeAd(activity, false);
                        }
                        if (nextStepListener != null) {
                            nextStepListener.onNextStep();
                            nextStepListener = null;
                        }
                    }
                });
            }
            return;
        }

        if (adIsLoading) {
            if (isFromSplash) {
                loadNativeAd(activity, false);
            }
            if (nextStepListener != null) {
                nextStepListener.onNextStep();
                nextStepListener = null;
            }
            return;
        }
        adIsLoading = true;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity.getApplicationContext(), interstitialAd, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                Log.e(TAG, "loadAdmobInterstitialAd onAdLoaded: " + msg + " : " + ad);
                adIsLoading = false;
                if (isFromSplash) {
                    loadNativeAd(activity, false);
                }
                googleInterstitialAd = ad;
                ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        adCloseTime = System.currentTimeMillis();
                        isShowingAd = false;
                        if (nextStepListener != null) {
                            nextStepListener.onNextStep();
                            nextStepListener = null;
                        }
                        loadInterstitialAd(activity);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        isShowingAd = false;
                        if (nextStepListener != null) {
                            nextStepListener.onNextStep();
                            nextStepListener = null;
                        }
                        loadInterstitialAd(activity);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        isShowingAd = true;
                    }
                });
                ad.show(activity);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e(TAG, "loadAdmobInterstitialAd onAdFailedToLoad: " + msg + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                adIsLoading = false;
                if (MyPreference.get_AdPlace().equalsIgnoreCase("multiple")) {
                    if (interstitialAd1 == null || interstitialAd1.equalsIgnoreCase("") || interstitialAd1.equalsIgnoreCase("null")) {
                        if (isFromSplash) {
                            loadNativeAd(activity, false);
                        }
                        if (nextStepListener != null) {
                            nextStepListener.onNextStep();
                            nextStepListener = null;
                        }
                        return;
                    }

                    if (adIsLoading) {
                        if (isFromSplash) {
                            loadNativeAd(activity, false);
                        }
                        if (nextStepListener != null) {
                            nextStepListener.onNextStep();
                            nextStepListener = null;
                        }
                        return;
                    }

                    adIsLoading = true;

                    AdRequest adRequest = new AdRequest.Builder().build();
                    InterstitialAd.load(activity.getApplicationContext(), interstitialAd1, adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd ad) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            Log.e(TAG, "loadAdmobInterstitialAd onAdLoaded: " + msg1 + " : " + ad);
                            adIsLoading = false;
                            if (isFromSplash) {
                                loadNativeAd(activity, false);
                            }
                            googleInterstitialAd = ad;
                            ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    adCloseTime = System.currentTimeMillis();
                                    isShowingAd = false;
                                    if (nextStepListener != null) {
                                        nextStepListener.onNextStep();
                                        nextStepListener = null;
                                    }
                                    loadInterstitialAd(activity);
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    isShowingAd = false;
                                    if (nextStepListener != null) {
                                        nextStepListener.onNextStep();
                                        nextStepListener = null;
                                    }
                                    loadInterstitialAd(activity);
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    isShowingAd = true;
                                }
                            });
                            ad.show(activity);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e(TAG, "loadAdmobInterstitialAd onAdFailedToLoad: " + msg1 + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                            adIsLoading = false;
                            if (isFromSplash) {
                                loadNativeAd(activity, false);
                            }
                            if (nextStepListener != null) {
                                nextStepListener.onNextStep();
                                nextStepListener = null;
                            }
                        }
                    });
                }
            }
        });
    }

    public static void displayInterstitialAdFailOnRewardedAd(Activity activity, NextStepListener mNextStepListener) {
        nextStepListener = mNextStepListener;
        if (!canRequestAds || MyPreference.get_Ad() == 0) {
            if (nextStepListener != null) {
                nextStepListener.onNextStep();
                nextStepListener = null;
            }
            return;
        }
        if (MyPreference.get_IsPremium()) {
            if (nextStepListener != null) {
                nextStepListener.onNextStep();
                nextStepListener = null;
            }
            return;
        }
        InterstitialAd interstitialAd = googleInterstitialAd;
        if (interstitialAd != null) {
            interstitialAd.show(activity);
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    adCloseTime = System.currentTimeMillis();
                    isShowingAd = false;
                    if (nextStepListener != null) {
                        nextStepListener.onNextStep();
                        nextStepListener = null;
                    }
                    loadInterstitialAd(activity);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    isShowingAd = false;
                    if (nextStepListener != null) {
                        nextStepListener.onNextStep();
                        nextStepListener = null;
                    }
                    loadInterstitialAd(activity);
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    isShowingAd = true;
                }
            });
        } else {
            if (nextStepListener != null) {
                nextStepListener.onNextStep();
                nextStepListener = null;
            }
            loadInterstitialAd(activity);
        }
    }


    public static void displayNativeSmallAd(Activity activity, ViewGroup viewGroup) {
        if (!canRequestAds || MyPreference.get_Ad() == 0) {
            viewGroup.removeAllViews();
            viewGroup.setVisibility(View.GONE);
            return;
        }
        if (MyPreference.get_IsPremium()) {
            viewGroup.removeAllViews();
            viewGroup.setVisibility(View.GONE);
            return;
        }
        viewGroup.setVisibility(View.VISIBLE);
        NativeAd nativeAd = mNativeAd;
        if (nativeAd != null) {
            viewGroup.setVisibility(View.VISIBLE);
            NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_native_layout_small, viewGroup, false);
            populateNativeSmallAdView(activity, nativeAd, adView);
            viewGroup.removeAllViews();
            viewGroup.addView(adView);
            loadNativeAd(activity, false);
        } else {
            loadNativeAd(activity, false);
            viewGroup.removeAllViews();
            viewGroup.setVisibility(View.GONE);
        }
    }

    public static void displayNativeLargeAd(Activity activity, ViewGroup viewGroup) {
        if (!canRequestAds || MyPreference.get_Ad() == 0) {
            viewGroup.removeAllViews();
            viewGroup.setVisibility(View.GONE);
            return;
        }
        if (MyPreference.get_IsPremium()) {
            viewGroup.removeAllViews();
            viewGroup.setVisibility(View.GONE);
            return;
        }
        NativeAd nativeAd = mNativeAd;
        if (nativeAd != null) {
            viewGroup.setVisibility(View.VISIBLE);
            NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_native_layout_large, viewGroup, false);
            populateNativeLargeAdView(activity, nativeAd, adView);
            viewGroup.removeAllViews();
            viewGroup.addView(adView);
            loadNativeAd(activity, false);
        } else {
            loadNativeAd(activity, false);
            viewGroup.removeAllViews();
            viewGroup.setVisibility(View.GONE);
        }
    }

    public static void loadNativeAd(Activity activity, Boolean isFromSplash) {
        if (!canRequestAds || MyPreference.get_Ad() == 0) {
            if (isFromSplash) {
                if (nextStepListener != null) {
                    nextStepListener.onNextStep();
                    nextStepListener = null;
                }
            }
            return;
        }
        if (MyPreference.get_IsPremium()) {
            if (isFromSplash) {
                if (nextStepListener != null) {
                    nextStepListener.onNextStep();
                    nextStepListener = null;
                }
            }
            return;
        }
        if (MyPreference.get_AdPriority().equalsIgnoreCase("admob")) {
            requestToLoadNativeAd(MyPreference.get_AmNative(), activity, "admob", MyPreference.get_AdxNative(), "adx", isFromSplash);
        } else if (MyPreference.get_AdPriority().equalsIgnoreCase("adx")) {
            requestToLoadNativeAd(MyPreference.get_AdxNative(), activity, "adx", MyPreference.get_AmNative(), "admob", isFromSplash);
        }
    }

    private static void requestToLoadNativeAd(String nativeAdId, Activity activity, String msg, String nativeAdId1, String msg1, Boolean isFromSplash) {
        if (nativeAdId.equalsIgnoreCase("") || nativeAdId.equalsIgnoreCase("null")) {
            if (MyPreference.get_AdPlace().equalsIgnoreCase("multiple")) {
                if (nativeAdId1.equalsIgnoreCase("") || nativeAdId1.equalsIgnoreCase("null")) {
                    if (isFromSplash) {
                        if (nextStepListener != null) {
                            nextStepListener.onNextStep();
                            nextStepListener = null;
                        }
                        loadInterstitialAd(activity);
                    }
                    return;
                }
                if (nativeAdIsLoading) {
                    return;
                }
                nativeAdIsLoading = true;
                AdLoader.Builder builder = new AdLoader.Builder(activity, nativeAdId1);
                builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        Log.e(TAG, "loadAdmobNativeAd onNativeAdLoaded: " + msg1 + " : " + nativeAd);
                        nativeAdIsLoading = false;
                        mNativeAd = nativeAd;
                        if (isFromSplash) {
                            if (nextStepListener != null) {
                                nextStepListener.onNextStep();
                                nextStepListener = null;
                            }
                            loadInterstitialAd(activity);
                        }
                    }
                });

                VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();

                NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

                builder.withNativeAdOptions(adOptions);

                AdLoader adLoader = builder.withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(TAG, "loadAdmobNativeAd onAdFailedToLoad: " + msg1 + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                        nativeAdIsLoading = false;
                        if (isFromSplash) {
                            if (nextStepListener != null) {
                                nextStepListener.onNextStep();
                                nextStepListener = null;
                            }
                            loadInterstitialAd(activity);
                        }
                    }
                }).build();
                adLoader.loadAd(new AdRequest.Builder().build());
            }
            return;
        }

        if (nativeAdIsLoading) {
            return;
        }
        nativeAdIsLoading = true;
        AdLoader.Builder builder = new AdLoader.Builder(activity, nativeAdId);
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                Log.e(TAG, "loadAdmobNativeAd onNativeAdLoaded: " + msg + " : " + nativeAd);
                nativeAdIsLoading = false;
                mNativeAd = nativeAd;
                if (isFromSplash) {
                    if (nextStepListener != null) {
                        nextStepListener.onNextStep();
                        nextStepListener = null;
                    }
                    loadInterstitialAd(activity);
                }
            }
        });

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e(TAG, "loadAdmobNativeAd onAdFailedToLoad: " + msg + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                nativeAdIsLoading = false;
                if (MyPreference.get_AdPlace().equalsIgnoreCase("multiple")) {
                    if (nativeAdId1.equalsIgnoreCase("") || nativeAdId1.equalsIgnoreCase("null")) {
                        if (isFromSplash) {
                            if (nextStepListener != null) {
                                nextStepListener.onNextStep();
                                nextStepListener = null;
                            }
                            loadInterstitialAd(activity);
                        }
                        return;
                    }

                    if (nativeAdIsLoading) {
                        return;
                    }

                    nativeAdIsLoading = true;
                    AdLoader.Builder builder = new AdLoader.Builder(activity, nativeAdId1);
                    builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            Log.e(TAG, "loadAdmobNativeAd onNativeAdLoaded: " + msg1 + " : " + nativeAd);
                            nativeAdIsLoading = false;
                            mNativeAd = nativeAd;
                            if (isFromSplash) {
                                if (nextStepListener != null) {
                                    nextStepListener.onNextStep();
                                    nextStepListener = null;
                                }
                                loadInterstitialAd(activity);
                            }
                        }
                    });

                    VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();

                    NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

                    builder.withNativeAdOptions(adOptions);

                    AdLoader adLoader = builder.withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e(TAG, "loadAdmobNativeAd onAdFailedToLoad: " + msg1 + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                            nativeAdIsLoading = false;
                            if (isFromSplash) {
                                if (nextStepListener != null) {
                                    nextStepListener.onNextStep();
                                    nextStepListener = null;
                                }
                                loadInterstitialAd(activity);
                            }
                        }
                    }).build();
                    adLoader.loadAd(new AdRequest.Builder().build());
                }
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public static void populateNativeSmallAdView(Activity activity, NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));
        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        if (nativeAd.getHeadline() == null) {
            Objects.requireNonNull(adView.getHeadlineView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getHeadlineView()).setVisibility(View.VISIBLE);
            ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
        }

        if (nativeAd.getMediaContent() == null) {
            Objects.requireNonNull(adView.getMediaView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getMediaView()).setVisibility(View.VISIBLE);
            Objects.requireNonNull(adView.getMediaView()).setMediaContent(nativeAd.getMediaContent());
        }

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.img_place_holder));
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.GONE);
        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView())).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.GONE);
        } else {
            ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);

        VideoController vc = Objects.requireNonNull(nativeAd.getMediaContent()).getVideoController();

        if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent().hasVideoContent()) {
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }
    }

    public static void populateNativeLargeAdView(Activity activity, NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));
        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        if (nativeAd.getHeadline() == null) {
            Objects.requireNonNull(adView.getHeadlineView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getHeadlineView()).setVisibility(View.VISIBLE);
            ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
        }

        if (nativeAd.getMediaContent() == null) {
            Objects.requireNonNull(adView.getMediaView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getMediaView()).setVisibility(View.VISIBLE);
            Objects.requireNonNull(adView.getMediaView()).setMediaContent(nativeAd.getMediaContent());
        }

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.img_place_holder));
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.GONE);
        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView())).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);

        VideoController vc = Objects.requireNonNull(nativeAd.getMediaContent()).getVideoController();

        if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent().hasVideoContent()) {
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }
    }


    public static void displayAdaptiveBannerAd(Activity activity, ViewGroup viewGroup) {
        if (!canRequestAds || MyPreference.get_Ad() == 0) {
            viewGroup.removeAllViews();
            viewGroup.setVisibility(View.GONE);
            return;
        }
        if (MyPreference.get_IsPremium()) {
            viewGroup.removeAllViews();
            viewGroup.setVisibility(View.GONE);
            return;
        }
        viewGroup.setVisibility(View.VISIBLE);
        if (MyPreference.get_AdPriority().equalsIgnoreCase("admob")) {
            loadAdaptiveBanner(MyPreference.get_AmBanner(), activity, "admob", MyPreference.get_AdxBanner(), "adx", viewGroup);
        } else if (MyPreference.get_AdPriority().equalsIgnoreCase("adx")) {
            loadAdaptiveBanner(MyPreference.get_AdxBanner(), activity, "adx", MyPreference.get_AmBanner(), "admob", viewGroup);
        }
    }

    public static void displayAdaptiveBannerAdCollapsable(Activity activity, ViewGroup viewGroup) {
        if (!canRequestAds || MyPreference.get_Ad() == 0) {
            viewGroup.removeAllViews();
            viewGroup.setVisibility(View.GONE);
            return;
        }
        if (MyPreference.get_IsPremium()) {
            viewGroup.removeAllViews();
            viewGroup.setVisibility(View.GONE);
            return;
        }
        viewGroup.setVisibility(View.VISIBLE);
        if (MyPreference.get_AdPriority().equalsIgnoreCase("admob")) {
            loadAdaptiveBannerCollapsable(MyPreference.get_AmBanner(), activity, "admob", MyPreference.get_AdxBanner(), "adx", viewGroup);
        } else if (MyPreference.get_AdPriority().equalsIgnoreCase("adx")) {
            loadAdaptiveBannerCollapsable(MyPreference.get_AdxBanner(), activity, "adx", MyPreference.get_AmBanner(), "admob", viewGroup);
        }
    }

    private static void loadAdaptiveBanner(String bannerAd, Activity activity, String msg, String bannerAd1, String msg1, ViewGroup viewGroup) {
        if (bannerAd == null || bannerAd.equalsIgnoreCase("") || bannerAd.equalsIgnoreCase("null")) {
            if (MyPreference.get_AdPlace().equalsIgnoreCase("multiple")) {
                if (bannerAd1 == null || bannerAd1.equalsIgnoreCase("") || bannerAd1.equalsIgnoreCase("null")) {
                    return;
                }

                AdView adView = new AdView(activity);
                adView.setAdUnitId(bannerAd1);
                AdSize adSize = getAdSize(activity, viewGroup);
                adView.setAdSize(adSize);

                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);

                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.e(TAG, "showAdmobBannerAd onAdFailedToLoad: " + msg1 + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.e(TAG, "showAdmobBannerAd onAdLoaded: " + msg1);
                        viewGroup.removeAllViews();
                        viewGroup.addView(adView);
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                    }

                    @Override
                    public void onAdSwipeGestureClicked() {
                        super.onAdSwipeGestureClicked();
                    }
                });
            }
            return;
        }

        AdView adView = new AdView(activity);
        adView.setAdUnitId(bannerAd);
        AdSize adSize = getAdSize(activity, viewGroup);
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e(TAG, "showAdmobBannerAd onAdFailedToLoad: " + msg + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());

                if (MyPreference.get_AdPlace().equalsIgnoreCase("multiple")) {
                    if (bannerAd1 == null || bannerAd1.equalsIgnoreCase("") || bannerAd1.equalsIgnoreCase("null")) {
                        return;
                    }

                    AdView adView = new AdView(activity);
                    adView.setAdUnitId(bannerAd1);
                    AdSize adSize = getAdSize(activity, viewGroup);
                    adView.setAdSize(adSize);

                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);

                    adView.setAdListener(new AdListener() {
                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                        }

                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            Log.e(TAG, "showAdmobBannerAd onAdFailedToLoad: " + msg1 + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            Log.e(TAG, "showAdmobBannerAd onAdLoaded: " + msg1);
                            viewGroup.removeAllViews();
                            viewGroup.addView(adView);
                        }

                        @Override
                        public void onAdOpened() {
                            super.onAdOpened();
                        }

                        @Override
                        public void onAdSwipeGestureClicked() {
                            super.onAdSwipeGestureClicked();
                        }
                    });
                }
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.e(TAG, "showAdmobBannerAd onAdLoaded: " + msg);
                viewGroup.removeAllViews();
                viewGroup.addView(adView);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked();
            }
        });

    }

    private static void loadAdaptiveBannerCollapsable(String bannerAd, Activity activity, String msg, String bannerAd1, String msg1, ViewGroup viewGroup) {
        if (bannerAd == null || bannerAd.equalsIgnoreCase("") || bannerAd.equalsIgnoreCase("null")) {
            if (MyPreference.get_AdPlace().equalsIgnoreCase("multiple")) {
                if (bannerAd1 == null || bannerAd1.equalsIgnoreCase("") || bannerAd1.equalsIgnoreCase("null")) {
                    return;
                }

                AdView adView = new AdView(activity);
                adView.setAdUnitId(bannerAd1);
                AdSize adSize = getAdSize(activity, viewGroup);
                adView.setAdSize(adSize);
                Bundle extras = new Bundle();
                extras.putString("collapsible", "bottom");
                AdRequest adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .build();
                adView.loadAd(adRequest);

                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.e(TAG, "showAdmobBannerAd onAdFailedToLoad: " + msg1 + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.e(TAG, "showAdmobBannerAd onAdLoaded: " + msg1);
                        viewGroup.removeAllViews();
                        viewGroup.addView(adView);
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                    }

                    @Override
                    public void onAdSwipeGestureClicked() {
                        super.onAdSwipeGestureClicked();
                    }
                });
            }
            return;
        }

        AdView adView = new AdView(activity);
        adView.setAdUnitId(bannerAd);
        AdSize adSize = getAdSize(activity, viewGroup);
        adView.setAdSize(adSize);
        Bundle extras = new Bundle();
        extras.putString("collapsible", "bottom");
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e(TAG, "showAdmobBannerAd onAdFailedToLoad: " + msg + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());

                if (MyPreference.get_AdPlace().equalsIgnoreCase("multiple")) {
                    if (bannerAd1 == null || bannerAd1.equalsIgnoreCase("") || bannerAd1.equalsIgnoreCase("null")) {
                        return;
                    }

                    AdView adView = new AdView(activity);
                    adView.setAdUnitId(bannerAd1);
                    AdSize adSize = getAdSize(activity, viewGroup);
                    adView.setAdSize(adSize);
                    Bundle extras = new Bundle();
                    extras.putString("collapsible", "bottom");
                    AdRequest adRequest = new AdRequest.Builder()
                            .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                            .build();
                    adView.loadAd(adRequest);

                    adView.setAdListener(new AdListener() {
                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                        }

                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            Log.e(TAG, "showAdmobBannerAd onAdFailedToLoad: " + msg1 + " : " + loadAdError.getCode() + " : " + loadAdError.getMessage());
                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            Log.e(TAG, "showAdmobBannerAd onAdLoaded: " + msg1);
                            viewGroup.removeAllViews();
                            viewGroup.addView(adView);
                        }

                        @Override
                        public void onAdOpened() {
                            super.onAdOpened();
                        }

                        @Override
                        public void onAdSwipeGestureClicked() {
                            super.onAdSwipeGestureClicked();
                        }
                    });
                }
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.e(TAG, "showAdmobBannerAd onAdLoaded: " + msg);
                viewGroup.removeAllViews();
                viewGroup.addView(adView);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked();
            }
        });

    }

    public static AdSize getAdSize(Activity activity, ViewGroup viewGroup) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = viewGroup.getWidth();

        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }


    public static void displayRewardedAd(Activity activity, NextStepListener mNextStepListener) {
        nextStepListener = mNextStepListener;
        if (!canRequestAds || MyPreference.get_Ad() == 0) {
            if (nextStepListener != null) {
                nextStepListener.onNextStep();
                nextStepListener = null;
            }
            return;
        }
        if (MyPreference.get_IsPremium()) {
            if (nextStepListener != null) {
                nextStepListener.onNextStep();
                nextStepListener = null;
            }
            return;
        }
        userRewarded = false;
        RewardedAd rewardedAd = mRewardedVideoAd;
        if (rewardedAd != null) {
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    if (userRewarded) {
                        if (nextStepListener != null) {
                            nextStepListener.onNextStep();
                            nextStepListener = null;
                        }
                        userRewarded = false;
                    }
                    MyAdsManager.adCloseTime = System.currentTimeMillis();
                    loadRewardedAd(activity, false);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);

                    if (nextStepListener != null) {
                        nextStepListener.onNextStep();
                        nextStepListener = null;
                    }
                    userRewarded = false;
                    loadRewardedAd(activity, false);
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    userRewarded = false;
                    isShowingAd = true;
                }
            });
            rewardedAd.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    userRewarded = true;
                }
            });
        } else {
            displayInterstitialAdFailOnRewardedAd(activity, nextStepListener);
            loadRewardedAd(activity, false);
        }
    }

    public static void loadRewardedAd(Activity activity, Boolean isFromDashboard) {
        if (!canRequestAds || MyPreference.get_Ad() == 0) {
            return;
        }
        if (MyPreference.get_IsPremium()) {
            return;
        }
        if (MyPreference.get_AdPriority().equalsIgnoreCase("admob")) {
            requestToLoadRewardedAd(MyPreference.get_AmRewarded(), activity, "admob", MyPreference.get_AdxRewarded(), "Adx", isFromDashboard);
        } else if (MyPreference.get_AdPriority().equalsIgnoreCase("adx")) {
            requestToLoadRewardedAd(MyPreference.get_AdxRewarded(), activity, "Adx", MyPreference.get_AmRewarded(), "admob", isFromDashboard);
        }
    }

    private static void requestToLoadRewardedAd(String rewardedAdId, Activity activity, String msg, String rewardedAdId1, String msg4, Boolean isFromDashboard) {
        if (rewardedAdId == null || rewardedAdId.equalsIgnoreCase("") || rewardedAdId.equalsIgnoreCase("null")) {
            if (MyPreference.get_AdPlace().equalsIgnoreCase("multiple")) {
                if (rewardedAdId1 == null || rewardedAdId1.equalsIgnoreCase("") || rewardedAdId1.equalsIgnoreCase("null")) {
                    if (isFromDashboard) {
                        if (MyApplication.getInstance().appOpenManager == null && !MyPreference.get_IsPremium()) {
                            MyApplication.getInstance().appOpenManager = new AppOpenManager();
                            MyApplication.getInstance().appOpenManager.loadAd(activity);
                        }
                    }
                    return;
                }

                if (rewardedAdIsLoading) {
                    return;
                }
                rewardedAdIsLoading = true;

                AdRequest adRequest = new AdRequest.Builder().build();

                RewardedAd.load(activity, rewardedAdId1, adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        rewardedAdIsLoading = false;
                        if (isFromDashboard) {
                            if (MyApplication.getInstance().appOpenManager == null && !MyPreference.get_IsPremium()) {
                                MyApplication.getInstance().appOpenManager = new AppOpenManager();
                                MyApplication.getInstance().appOpenManager.loadAd(activity);
                            }
                        }
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        rewardedAdIsLoading = false;
                        mRewardedVideoAd = rewardedAd;
                        if (isFromDashboard) {
                            if (MyApplication.getInstance().appOpenManager == null && !MyPreference.get_IsPremium()) {
                                MyApplication.getInstance().appOpenManager = new AppOpenManager();
                                MyApplication.getInstance().appOpenManager.loadAd(activity);
                            }
                        }
                    }

                });
            }
            return;
        }

        if (rewardedAdIsLoading) {
            return;
        }
        rewardedAdIsLoading = true;

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(activity, rewardedAdId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                rewardedAdIsLoading = false;
                if (MyPreference.get_AdPlace().equalsIgnoreCase("multiple")) {
                    if (rewardedAdId1.equalsIgnoreCase("") || rewardedAdId1.equalsIgnoreCase("null")) {
                        if (isFromDashboard) {
                            if (MyApplication.getInstance().appOpenManager == null && !MyPreference.get_IsPremium()) {
                                MyApplication.getInstance().appOpenManager = new AppOpenManager();
                                MyApplication.getInstance().appOpenManager.loadAd(activity);
                            }
                        }
                        return;
                    }
                    rewardedAdIsLoading = true;
                    AdRequest adRequest = new AdRequest.Builder().build();
                    RewardedAd.load(activity, rewardedAdId1, adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            rewardedAdIsLoading = false;
                            if (isFromDashboard) {
                                if (MyApplication.getInstance().appOpenManager == null && !MyPreference.get_IsPremium()) {
                                    MyApplication.getInstance().appOpenManager = new AppOpenManager();
                                    MyApplication.getInstance().appOpenManager.loadAd(activity);
                                }
                            }
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            rewardedAdIsLoading = false;
                            mRewardedVideoAd = rewardedAd;
                            if (isFromDashboard) {
                                if (MyApplication.getInstance().appOpenManager == null && !MyPreference.get_IsPremium()) {
                                    MyApplication.getInstance().appOpenManager = new AppOpenManager();
                                    MyApplication.getInstance().appOpenManager.loadAd(activity);
                                }
                            }
                        }
                    });
                }

            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                rewardedAdIsLoading = false;
                mRewardedVideoAd = rewardedAd;
                if (isFromDashboard) {
                    if (MyApplication.getInstance().appOpenManager == null && !MyPreference.get_IsPremium()) {
                        MyApplication.getInstance().appOpenManager = new AppOpenManager();
                        MyApplication.getInstance().appOpenManager.loadAd(activity);
                    }
                }
            }

        });


    }

}