package com.festival.dailypostermaker.Model.GetUserData;

import com.google.gson.annotations.SerializedName;

public class PaymentSetting {

    @SerializedName("payment_mode")
    private String paymentMode;

    @SerializedName("payment_status")
    private String paymentStatus;

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
}
