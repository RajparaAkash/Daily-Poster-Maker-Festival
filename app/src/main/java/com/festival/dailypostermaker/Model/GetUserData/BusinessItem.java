package com.festival.dailypostermaker.Model.GetUserData;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class BusinessItem {

    @SerializedName("whatsapp")
    private String whatsapp;

    @SerializedName("youtube")
    private String youtube;

    @SerializedName("website")
    private String website;

    @SerializedName("address")
    private String address;

    @SerializedName("business_category_id")
    private int businessCategoryId;

    @SerializedName("facebook")
    private String facebook;

    @SerializedName("instagram")
    private String instagram;

    @SerializedName("twitter")
    private String twitter;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("name")
    private String name;

    @SerializedName("logo")
    private String logo;

    @SerializedName("id")
    private int id;

    @SerializedName("designation")
    private String designation;

    @SerializedName("email")
    private String email;

    public String getWhatsapp() {
        return whatsapp;
    }

    public String getYoutube() {
        return youtube;
    }

    public String getWebsite() {
        return website;
    }

    public String getAddress() {
        return address;
    }

    public int getBusinessCategoryId() {
        return businessCategoryId;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public String getTwitter() {
        return twitter;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public int getId() {
        return id;
    }

    public String getDesignation() {
        return designation;
    }

    public String getEmail() {
        return email;
    }
}