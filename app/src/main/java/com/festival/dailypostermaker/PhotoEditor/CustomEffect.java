package com.festival.dailypostermaker.PhotoEditor;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;


public class CustomEffect {
    private final String mEffectName;
    private final Map<String, Object> parametersMap;

    private CustomEffect(Builder builder) {
        this.mEffectName = builder.mEffectName;
        this.parametersMap = builder.parametersMap;
    }

    public String getEffectName() {
        return this.mEffectName;
    }

    public Map<String, Object> getParameters() {
        return this.parametersMap;
    }


    public static class Builder {
        public String mEffectName;
        public Map<String, Object> parametersMap = new HashMap();

        public Builder(String str) {
            if (!TextUtils.isEmpty(str)) {
                this.mEffectName = str;
                return;
            }
        }

        public Builder setParameter(String str, Object obj) {
            this.parametersMap.put(str, obj);
            return this;
        }

        public CustomEffect build() {
            return new CustomEffect(this);
        }
    }
}
