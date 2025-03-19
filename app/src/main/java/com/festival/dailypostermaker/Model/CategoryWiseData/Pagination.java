package com.festival.dailypostermaker.Model.CategoryWiseData;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class Pagination {

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_items")
    private int totalItems;

    @SerializedName("current_page")
    private int currentPage;

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}