package com.festival.dailypostermaker.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.util.ArrayList;

@Keep
public class TemplateMainLayout implements Parcelable {

    private final Integer layoutList;
    private final ArrayList<PostDetails> postDetailsList;

    public TemplateMainLayout(Integer layoutList, ArrayList<PostDetails> postDetailsList) {
        this.layoutList = layoutList;
        this.postDetailsList = postDetailsList;
    }

    public Integer getLayoutList() {
        return layoutList;
    }

    public ArrayList<PostDetails> getPostDetailsList() {
        return postDetailsList;
    }

    protected TemplateMainLayout(Parcel in) {
        if (in.readByte() == 0) {
            layoutList = null;
        } else {
            layoutList = in.readInt();
        }
        postDetailsList = in.createTypedArrayList(PostDetails.CREATOR);
    }

    public static final Creator<TemplateMainLayout> CREATOR = new Creator<TemplateMainLayout>() {
        @Override
        public TemplateMainLayout createFromParcel(Parcel in) {
            return new TemplateMainLayout(in);
        }

        @Override
        public TemplateMainLayout[] newArray(int size) {
            return new TemplateMainLayout[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (layoutList == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(layoutList);
        }
        dest.writeTypedList(postDetailsList);
    }
}
