package com.festival.dailypostermaker.Model.CategoryName;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class PostItem {

    @SerializedName("icon")
    private String icon;

    @SerializedName("batch")
    private int batch;

    @SerializedName("is_videoOrBanner")
    private int isVideoOrBanner;

    @SerializedName("video_status")
    private int videoStatus;

    @SerializedName("remark")
    private Object remark;

    @SerializedName("language_id")
    private String languageId;

    @SerializedName("video")
    private String video;

    @SerializedName("type")
    private int type;

    @SerializedName("video_price")
    private Object videoPrice;

    @SerializedName("video_type")
    private int videoType;

    @SerializedName("total_download")
    private int totalDownload;

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("video_size")
    private String videoSize;

    @SerializedName("icon_size")
    private int iconSize;

    @SerializedName("id")
    private int id;

    @SerializedName("order")
    private int order;

    @SerializedName("banner_size")
    private int bannerSize;

    @SerializedName("tiniicon_size")
    private Object tiniiconSize;

    @SerializedName("banner")
    private String banner;

    @SerializedName("post_price")
    private Object postPrice;

    @SerializedName("tiniicon")
    private Object tiniicon;

    @SerializedName("banner_type")
    private int bannerType;

    @SerializedName("image_status")
    private int imageStatus;

    @SerializedName("status")
    private int status;

    public String getIcon() {
        return icon;
    }

    public int getBatch() {
        return batch;
    }

    public int getIsVideoOrBanner() {
        return isVideoOrBanner;
    }

    public int getVideoStatus() {
        return videoStatus;
    }

    public Object getRemark() {
        return remark;
    }

    public String getLanguageId() {
        return languageId;
    }

    public String getVideo() {
        return video;
    }

    public int getType() {
        return type;
    }

    public Object getVideoPrice() {
        return videoPrice;
    }

    public int getVideoType() {
        return videoType;
    }

    public int getTotalDownload() {
        return totalDownload;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getVideoSize() {
        return videoSize;
    }

    public int getIconSize() {
        return iconSize;
    }

    public int getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }

    public int getBannerSize() {
        return bannerSize;
    }

    public Object getTiniiconSize() {
        return tiniiconSize;
    }

    public String getBanner() {
        return banner;
    }

    public Object getPostPrice() {
        return postPrice;
    }

    public Object getTiniicon() {
        return tiniicon;
    }

    public int getBannerType() {
        return bannerType;
    }

    public int getImageStatus() {
        return imageStatus;
    }

    public int getStatus() {
        return status;
    }
}