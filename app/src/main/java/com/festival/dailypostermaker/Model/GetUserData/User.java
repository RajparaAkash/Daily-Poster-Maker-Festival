package com.festival.dailypostermaker.Model.GetUserData;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class User {

    @SerializedName("youtube")
    private String youtube;

    @SerializedName("user_status")
    private int userStatus;

    @SerializedName("website")
    private String website;

    @SerializedName("address")
    private String address;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("fb_token")
    private String fbToken;

    @SerializedName("profile")
    private String profile;

    @SerializedName("facebook")
    private String facebook;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("instagram")
    private String instagram;

    @SerializedName("twitter")
    private String twitter;

    @SerializedName("user_type")
    private int userType;

    @SerializedName("about_us")
    private String aboutUs;

    @SerializedName("user_premium_status")
    private int userPremiumStatus;

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private int id;

    @SerializedName("email")
    private String email;

    @SerializedName("remaining_free_post")
    private int remainingFreePost;

    @SerializedName("free_post")
    private int freePost;


    public String getYoutube() {
        return youtube;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public String getWebsite() {
        return website;
    }

    public String getAddress() {
        return address;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getFbToken() {
        return fbToken;
    }

    public String getProfile() {
        return profile;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getMobile() {
        return mobile;
    }

    public String getInstagram() {
        return instagram;
    }

    public String getTwitter() {
        return twitter;
    }

    public int getUserType() {
        return userType;
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public int getUserPremiumStatus() {
        return userPremiumStatus;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public int getRemainingFreePost() {
        return remainingFreePost;
    }

    public int getFreePost() {
        return freePost;
    }
}