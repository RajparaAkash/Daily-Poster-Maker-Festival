package com.festival.dailypostermaker.PhotoEditor;


public class SaveSettings {
    private final boolean isClearViewsEnabled;
    private final boolean isTransparencyEnabled;

    public boolean isTransparencyEnabled() {
        return this.isTransparencyEnabled;
    }

    public boolean isClearViewsEnabled() {
        return this.isClearViewsEnabled;
    }

    private SaveSettings(Builder builder) {
        this.isClearViewsEnabled = builder.isClearViewsEnabled;
        this.isTransparencyEnabled = builder.isTransparencyEnabled;
    }

    
    public static class Builder {
        public boolean isClearViewsEnabled = true;
        public boolean isTransparencyEnabled = true;

        public Builder setTransparencyEnabled(boolean z) {
            this.isTransparencyEnabled = z;
            return this;
        }

        public Builder setClearViewsEnabled(boolean z) {
            this.isClearViewsEnabled = z;
            return this;
        }

        public SaveSettings build() {
            return new SaveSettings(this);
        }
    }
}
