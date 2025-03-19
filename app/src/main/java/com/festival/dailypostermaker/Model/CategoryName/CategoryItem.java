package com.festival.dailypostermaker.Model.CategoryName;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class CategoryItem {

    @SerializedName("is_display_cat")
    private int isDisplayCat;

    @SerializedName("icon")
    private String icon;

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private int id;

    @SerializedName("cat_date")
    private String catDate;

    private boolean isSelected;

    public int getIsDisplayCat() {
        return isDisplayCat;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCatDate() {
        return catDate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}