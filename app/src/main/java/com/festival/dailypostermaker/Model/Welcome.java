package com.festival.dailypostermaker.Model;

import androidx.annotation.Keep;

@Keep
public class Welcome {

    private final int icon;
    private final String title;

    public Welcome(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

}
