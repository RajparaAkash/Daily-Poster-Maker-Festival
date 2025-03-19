package com.festival.dailypostermaker.Model.AddBusinessDetails;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class AddBusiness {

    @SerializedName("flag")
    private boolean flag;

    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private Data data;

    @SerializedName("message")
    private String message;

    public boolean isFlag() {
        return flag;
    }

    public int getCode() {
        return code;
    }

    public Data getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}