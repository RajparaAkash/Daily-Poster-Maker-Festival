package com.festival.dailypostermaker.Model.Bg;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class Bg {

    @SerializedName("flag")
    private boolean flag;

    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<Data> data;

    @SerializedName("message")
    private String message;

    public boolean isFlag() {
        return flag;
    }

    public int getCode() {
        return code;
    }

    public List<Data> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}