package com.festival.dailypostermaker.Model.GetUserData;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class Data {

    @SerializedName("user")
    private User user;

    @SerializedName("business")
    private BusinessItem business;

    @SerializedName("ad_placement")
    private AdPlacement adPlacement;

    @SerializedName("payment_setting")
    private PaymentSetting paymentSetting;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BusinessItem getBusiness() {
        return business;
    }

    public void setBusiness(BusinessItem business) {
        this.business = business;
    }

    public AdPlacement getAdPlacement() {
        return adPlacement;
    }

    public void setAdPlacement(AdPlacement adPlacement) {
        this.adPlacement = adPlacement;
    }

    public PaymentSetting getPaymentSetting() {
        return paymentSetting;
    }
}