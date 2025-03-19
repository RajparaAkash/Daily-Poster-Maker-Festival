package com.festival.dailypostermaker.Model.CategoryName;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@Keep
public class Data {

    @SerializedName("pagination")
    private Pagination pagination;

    @SerializedName("post")
    private ArrayList<PostItem> post;

    @SerializedName("category")
    private ArrayList<CategoryItem> category;

    public Pagination getPagination() {
        return pagination;
    }

    public ArrayList<PostItem> getBanner() {
        return post;
    }

    public ArrayList<CategoryItem> getCategory() {
        return category;
    }
}