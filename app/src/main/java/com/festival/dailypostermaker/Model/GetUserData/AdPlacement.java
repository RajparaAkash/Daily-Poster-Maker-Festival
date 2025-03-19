package com.festival.dailypostermaker.Model.GetUserData;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class AdPlacement {

    @SerializedName("id")
    private Integer id;

    @SerializedName("ads")
    private Integer ads;

    @SerializedName("privacy_policy")
    private String privacyPolicy;

    @SerializedName("terms_condition")
    private String termsCondition;

    @SerializedName("refund_cancel")
    private String refundCancel;

    @SerializedName("ad_place")
    private String adPlace;

    @SerializedName("priority")
    private String priority;

    @SerializedName("adx")
    private Adx adx;

    @SerializedName("admob")
    private Admob admob;

    public Integer getAds() {
        return ads;
    }

    public void setAds(Integer ads) {
        this.ads = ads;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    public void setPrivacyPolicy(String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    public String getTermsCondition() {
        return termsCondition;
    }

    public void setTermsCondition(String termsCondition) {
        this.termsCondition = termsCondition;
    }

    public String getRefundCancel() {
        return refundCancel;
    }

    public void setRefundCancel(String refundCancel) {
        this.refundCancel = refundCancel;
    }

    public String getAdPlace() {
        return adPlace;
    }

    public void setAdPlace(String adPlace) {
        this.adPlace = adPlace;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Adx getAdx() {
        return adx;
    }

    public void setAdx(Adx adx) {
        this.adx = adx;
    }

    public Admob getAdmob() {
        return admob;
    }

    public void setAdmob(Admob admob) {
        this.admob = admob;
    }

}