package com.festival.dailypostermaker.Model.UpdatePersonalDetails;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class PersonalDetails {

    @SerializedName("youtube")
    private Object youtube;

    @SerializedName("user_status")
    private int userStatus;

    @SerializedName("website")
    private Object website;

    @SerializedName("address")
    private String address;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("profile")
    private String profile;

    @SerializedName("facebook")
    private Object facebook;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("instagram")
    private String instagram;

    @SerializedName("twitter")
    private Object twitter;

    @SerializedName("user_type")
    private int userType;

    @SerializedName("about_us")
    private String aboutUs;

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private int id;

    @SerializedName("email")
    private String email;

    public Object getYoutube() {
        return youtube;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public Object getWebsite() {
        return website;
    }

    public String getAddress() {
        return address;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getProfile() {
        return profile;
    }

    public Object getFacebook() {
        return facebook;
    }

    public String getMobile() {
        return mobile;
    }

    public String getInstagram() {
        return instagram;
    }

    public Object getTwitter() {
        return twitter;
    }

    public int getUserType() {
        return userType;
    }

    public String getAboutUs() {
        return aboutUs;
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
}