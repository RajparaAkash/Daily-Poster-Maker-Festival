package com.festival.dailypostermaker.Model.UpdatePersonalDetails;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class Data {

    @SerializedName("user")
    private PersonalDetails personalDetails;

    @SerializedName("token")
    private String token;

    public PersonalDetails getUser() {
        return personalDetails;
    }

    public String getToken() {
        return token;
    }
}