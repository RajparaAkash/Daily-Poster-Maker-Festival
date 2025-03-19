package com.festival.dailypostermaker.Model;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class PostExport {

    @SerializedName("flag")
    @Expose
    private Boolean flag;

    @SerializedName("data")
    @Expose
    private Object data;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("code")
    @Expose
    private Integer code;

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}