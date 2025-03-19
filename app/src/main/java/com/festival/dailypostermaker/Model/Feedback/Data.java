package com.festival.dailypostermaker.Model.Feedback;

import androidx.annotation.Keep;

@Keep
public class Data {
    private String feedback;
    private String date;
    private int id;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
