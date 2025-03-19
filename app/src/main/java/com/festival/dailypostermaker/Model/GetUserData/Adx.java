package com.festival.dailypostermaker.Model.GetUserData;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class Adx {

    @SerializedName("adx_appid")
    private String adxAppid;

    @SerializedName("adx_banner")
    private String adxBanner;

    @SerializedName("adx_interstitial")
    private String adxInterstitial;

    @SerializedName("adx_native")
    private String adxNative;

    @SerializedName("adx_native_1")
    private String adxNative1;

    @SerializedName("adx_rewarded_video")
    private String adxRewardedVideo;

    @SerializedName("adx_app_open")
    private String adxAppOpen;

    public String getAdxAppid() {
        return adxAppid;
    }

    public void setAdxAppid(String adxAppid) {
        this.adxAppid = adxAppid;
    }

    public String getAdxBanner() {
        return adxBanner;
    }

    public void setAdxBanner(String adxBanner) {
        this.adxBanner = adxBanner;
    }

    public String getAdxInterstitial() {
        return adxInterstitial;
    }

    public void setAdxInterstitial(String adxInterstitial) {
        this.adxInterstitial = adxInterstitial;
    }

    public String getAdxNative() {
        return adxNative;
    }

    public void setAdxNative(String adxNative) {
        this.adxNative = adxNative;
    }

    public String getAdxNative1() {
        return adxNative1;
    }

    public void setAdxNative1(String adxNative1) {
        this.adxNative1 = adxNative1;
    }

    public String getAdxRewardedVideo() {
        return adxRewardedVideo;
    }

    public void setAdxRewardedVideo(String adxRewardedVideo) {
        this.adxRewardedVideo = adxRewardedVideo;
    }

    public String getAdxAppOpen() {
        return adxAppOpen;
    }

    public void setAdxAppOpen(String adxAppOpen) {
        this.adxAppOpen = adxAppOpen;
    }

}