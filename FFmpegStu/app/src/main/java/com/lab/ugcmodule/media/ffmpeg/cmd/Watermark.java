package com.lab.ugcmodule.media.ffmpeg.cmd;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kuaigeng01 on 2017/6/16.
 */

public class Watermark implements Parcelable {
    private String imagePath;
    private int locationX;
    private int locationY;

    public Watermark(String imagePath, int locationX, int locationY) {
        this.imagePath = imagePath;
        this.locationX = locationX;
        this.locationY = locationY;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public boolean isGif() {
        return imagePath.endsWith(".gif");
    }


    @Override
    public int describeContents() {
        return 0;
    }

    private Watermark(Parcel parcel) {
        this.imagePath = parcel.readString();
        this.locationX = parcel.readInt();
        this.locationY = parcel.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imagePath);
        dest.writeInt(locationX);
        dest.writeInt(locationY);
    }

    public static Parcelable.Creator<Watermark> CREATOR = new Creator<Watermark>() {
        @Override
        public Watermark createFromParcel(Parcel source) {

            return new Watermark(source);
        }

        @Override
        public Watermark[] newArray(int size) {
            return new Watermark[size];
        }
    };
}
