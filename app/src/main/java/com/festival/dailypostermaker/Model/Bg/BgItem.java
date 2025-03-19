package com.festival.dailypostermaker.Model.Bg;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class BgItem {

    @SerializedName("id")
    private int id;

    @SerializedName("assets_id")
    private int assetsId;

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("name")
    private String name;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("zip_file")
    private String zipFile;

    @SerializedName("pro_free")
    private int proFree;

    @SerializedName("order")
    private int order;

    @SerializedName("status")
    private int status;


    public int getId() {
        return id;
    }

    public int getAssetsId() {
        return assetsId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getZipFile() {
        return zipFile;
    }

    public int getProFree() {
        return proFree;
    }

    public int getOrder() {
        return order;
    }

    public int getStatus() {
        return status;
    }
}