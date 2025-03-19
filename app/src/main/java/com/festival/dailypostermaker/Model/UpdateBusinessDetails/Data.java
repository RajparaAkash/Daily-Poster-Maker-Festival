package com.festival.dailypostermaker.Model.UpdateBusinessDetails;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class Data {

    @SerializedName("business")
    private Business business;

    public Business getBusiness() {
        return business;
    }
}