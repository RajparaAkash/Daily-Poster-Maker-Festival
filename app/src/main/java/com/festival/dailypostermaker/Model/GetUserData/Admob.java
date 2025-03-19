package com.festival.dailypostermaker.Model.GetUserData;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class Admob {

    @SerializedName("am_app_id")
    private String amAppId;

    @SerializedName("am_banner")
    private String amBanner;

    @SerializedName("am_interstitial")
    private String amInterstitial;

    @SerializedName("am_native")
    private String amNative;

    @SerializedName("am_native_1")
    private String amNative1;

    @SerializedName("am_rewarded_video")
    private String amRewardedVideo;

    @SerializedName("am_app_open")
    private String amAppOpen;

    public String getAmAppId() {
        return amAppId;
    }

    public void setAmAppId(String amAppId) {
        this.amAppId = amAppId;
    }

    public String getAmBanner() {
        return amBanner;
    }

    public void setAmBanner(String amBanner) {
        this.amBanner = amBanner;
    }

    public String getAmInterstitial() {
        return amInterstitial;
    }

    public void setAmInterstitial(String amInterstitial) {
        this.amInterstitial = amInterstitial;
    }

    public String getAmNative() {
        return amNative;
    }

    public void setAmNative(String amNative) {
        this.amNative = amNative;
    }

    public String getAmNative1() {
        return amNative1;
    }

    public void setAmNative1(String amNative1) {
        this.amNative1 = amNative1;
    }

    public String getAmRewardedVideo() {
        return amRewardedVideo;
    }

    public void setAmRewardedVideo(String amRewardedVideo) {
        this.amRewardedVideo = amRewardedVideo;
    }

    public String getAmAppOpen() {
        return amAppOpen;
    }

    public void setAmAppOpen(String amAppOpen) {
        this.amAppOpen = amAppOpen;
    }

}