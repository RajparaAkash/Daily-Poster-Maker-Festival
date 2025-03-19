package com.festival.dailypostermaker.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import java.io.File;
import java.util.Objects;

@Keep
public class Downloads implements Parcelable {

    private final long id;
    private final String filePath;
    private final String fileName;

    public Downloads(long id, String filePath, String fileName) {
        this.id = id;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Downloads downloads = (Downloads) obj;
        return Objects.equals(id, downloads.id) &&
                Objects.equals(filePath, downloads.filePath) &&
                Objects.equals(fileName, downloads.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filePath, fileName);
    }

    protected Downloads(Parcel in) {
        id = in.readLong();
        filePath = in.readString();
        fileName = in.readString();
    }

    public static final Creator<Downloads> CREATOR = new Creator<Downloads>() {
        @Override
        public Downloads createFromParcel(Parcel in) {
            return new Downloads(in);
        }

        @Override
        public Downloads[] newArray(int size) {
            return new Downloads[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public File getFile() {
        if (this.filePath != null) {
            File file = new File(this.filePath);
            if (file.exists()) {
                return file;
            }
            return null;
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(filePath);
        dest.writeString(fileName);
    }
}
