package com.festival.dailypostermaker.Preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.festival.dailypostermaker.MyApplication;

public class HelperPreference {

    public static HelperPreference instance;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences settings;

    private HelperPreference() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().getSharedPreferences("Daily Poster Maker & Festival", Context.MODE_PRIVATE);
        this.settings = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    public static HelperPreference getInstance() {
        if (instance == null) {
            synchronized (HelperPreference.class) {
                if (instance == null) {
                    instance = new HelperPreference();
                }
            }
        }
        return instance;
    }

    public String getString(String key, String defValue) {
        return this.settings.getString(key, defValue);
    }

    public HelperPreference setString(String key, String value) {
        this.editor.putString(key, value);
        this.editor.commit();
        return this;
    }

    public int getInt(String key, int defValue) {
        return this.settings.getInt(key, defValue);
    }

    public HelperPreference setInt(String key, int value) {
        this.editor.putInt(key, value);
        this.editor.commit();
        return this;
    }

    public boolean getBoolean(String key, boolean defValue) {
        return this.settings.getBoolean(key, defValue);
    }

    public HelperPreference setBoolean(String key, boolean value) {
        this.editor.putBoolean(key, value);
        this.editor.commit();
        return this;
    }

    public void clear() {
        this.editor.clear();
        this.editor.commit();
    }
}
