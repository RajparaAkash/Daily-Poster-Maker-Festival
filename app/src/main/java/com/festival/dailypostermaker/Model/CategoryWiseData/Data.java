package com.festival.dailypostermaker.Model.CategoryWiseData;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;
import com.festival.dailypostermaker.Model.CategoryName.PostItem;

import java.util.List;

@Keep
public class Data {

    @SerializedName("pagination")
    private Pagination pagination;

    @SerializedName("post")
    private List<PostItem> post;

    public Pagination getPagination() {
        return pagination;
    }

    public List<PostItem> getPost() {
        return post;
    }
}