package com.festival.dailypostermaker.Model.Bg;

import androidx.annotation.Keep;

import java.util.ArrayList;

@Keep
public class BgSub {

    private String name;
    private String path;
    private String thumbnail;
    private int totalFiles;
    private ArrayList<String> mediaPaths = new ArrayList<>();
    private boolean isSelected;

    public BgSub(String name, String path, String thumbnail, int totalFiles, ArrayList<String> mediaPaths) {
        this.name = name;
        this.path = path;
        this.thumbnail = thumbnail;
        this.totalFiles = totalFiles;
        this.mediaPaths = mediaPaths;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public ArrayList<String> getMediaPaths() {
        return mediaPaths;
    }

    public void setMediaPaths(ArrayList<String> mediaPaths) {
        this.mediaPaths = mediaPaths;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
