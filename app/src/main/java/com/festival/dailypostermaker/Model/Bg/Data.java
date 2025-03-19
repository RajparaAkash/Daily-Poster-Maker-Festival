package com.festival.dailypostermaker.Model.Bg;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@Keep
public class Data {

    @SerializedName("assets")
    private ArrayList<BgItem> assets;

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private int id;

    public ArrayList<BgItem> getAssets() {
        return assets;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}