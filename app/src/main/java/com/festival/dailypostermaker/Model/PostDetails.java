package com.festival.dailypostermaker.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

@Keep
public class PostDetails implements Parcelable {

    Integer layoutList;
    private String p_Name;
    private String p_MobileNo;
    private String p_About;
    private String p_Address;
    private String p_Instagram;

    private String b_Category;
    private String b_Name;
    private String b_Designation;
    private String b_Address;
    private String b_Whatsapp;
    private String b_Instagram;
    private String b_ProfilePath;

    public PostDetails(Integer layoutList) {
        this.layoutList = layoutList;
    }

    public PostDetails(Integer layoutList, String p_Name, String p_MobileNo, String p_About, String p_Address, String p_Instagram,
                       String b_Category, String b_Name, String b_Designation, String b_Address, String b_Whatsapp, String b_Instagram, String b_ProfilePath) {
        this.layoutList = layoutList;
        this.p_Name = p_Name;
        this.p_MobileNo = p_MobileNo;
        this.p_About = p_About;
        this.p_Address = p_Address;
        this.p_Instagram = p_Instagram;

        this.b_Category = b_Category;
        this.b_Name = b_Name;
        this.b_Designation = b_Designation;
        this.b_Address = b_Address;
        this.b_Whatsapp = b_Whatsapp;
        this.b_Instagram = b_Instagram;
        this.b_ProfilePath = b_ProfilePath;
    }

    protected PostDetails(Parcel in) {
        if (in.readByte() == 0) {
            layoutList = null;
        } else {
            layoutList = in.readInt();
        }
        p_Name = in.readString();
        p_MobileNo = in.readString();
        p_About = in.readString();
        p_Address = in.readString();
        p_Instagram = in.readString();

        b_Category = in.readString();
        b_Name = in.readString();
        b_Designation = in.readString();
        b_Address = in.readString();
        b_Whatsapp = in.readString();
        b_Instagram = in.readString();
        b_ProfilePath = in.readString();
    }

    public static final Creator<PostDetails> CREATOR = new Creator<PostDetails>() {
        @Override
        public PostDetails createFromParcel(Parcel in) {
            return new PostDetails(in);
        }

        @Override
        public PostDetails[] newArray(int size) {
            return new PostDetails[size];
        }
    };

    public Integer getLayoutList() {
        return layoutList;
    }

    public void setLayoutList(Integer layoutList) {
        this.layoutList = layoutList;
    }


    public String getP_Name() {
        return p_Name;
    }

    public void setP_Name(String p_Name) {
        this.p_Name = p_Name;
    }

    public String getP_MobileNo() {
        return p_MobileNo;
    }

    public void setP_MobileNo(String p_MobileNo) {
        this.p_MobileNo = p_MobileNo;
    }

    public String getP_About() {
        return p_About;
    }

    public void setP_About(String p_About) {
        this.p_About = p_About;
    }

    public String getP_Address() {
        return p_Address;
    }

    public void setP_Address(String p_Address) {
        this.p_Address = p_Address;
    }

    public String getP_Instagram() {
        return p_Instagram;
    }

    public void setP_Instagram(String p_Instagram) {
        this.p_Instagram = p_Instagram;
    }

    public String getB_Category() {
        return b_Category;
    }

    public void setB_Category(String b_Category) {
        this.b_Category = b_Category;
    }

    public String getB_Name() {
        return b_Name;
    }

    public void setB_Name(String b_Name) {
        this.b_Name = b_Name;
    }

    public String getB_Designation() {
        return b_Designation;
    }

    public void setB_Designation(String b_Designation) {
        this.b_Designation = b_Designation;
    }

    public String getB_Address() {
        return b_Address;
    }

    public void setB_Address(String b_Address) {
        this.b_Address = b_Address;
    }

    public String getB_Whatsapp() {
        return b_Whatsapp;
    }

    public void setB_Whatsapp(String b_Whatsapp) {
        this.b_Whatsapp = b_Whatsapp;
    }

    public String getB_Instagram() {
        return b_Instagram;
    }

    public void setB_Instagram(String b_Instagram) {
        this.b_Instagram = b_Instagram;
    }

    public String getB_ProfilePath() {
        return b_ProfilePath;
    }

    public void setB_ProfilePath(String b_ProfilePath) {
        this.b_ProfilePath = b_ProfilePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (layoutList == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(layoutList);
        }
        dest.writeString(p_Name);
        dest.writeString(p_MobileNo);
        dest.writeString(p_About);
        dest.writeString(p_Address);
        dest.writeString(p_Instagram);

        dest.writeString(b_Category);
        dest.writeString(b_Name);
        dest.writeString(b_Designation);
        dest.writeString(b_Address);
        dest.writeString(b_Whatsapp);
        dest.writeString(b_Instagram);
        dest.writeString(b_ProfilePath);

    }
}
