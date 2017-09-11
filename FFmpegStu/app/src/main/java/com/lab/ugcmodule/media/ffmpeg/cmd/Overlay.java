package com.lab.ugcmodule.media.ffmpeg.cmd;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 视频叠加
 * Created by kuaigeng01 on 2017/7/4.
 */

public class Overlay implements Parcelable {
    private String videoPath;
    private int insertTime;
    private int locationX;
    private int locationY;

    public Overlay(String videoPath, int insertTime, int locationX, int locationY) {
        this.videoPath = videoPath;
        this.insertTime = insertTime;
        this.locationX = locationX;
        this.locationY = locationY;
    }

    private Overlay(Parcel in) {
        videoPath = in.readString();
        insertTime = in.readInt();
        locationX = in.readInt();
        locationY = in.readInt();
    }

    public static final Creator<Overlay> CREATOR = new Creator<Overlay>() {
        @Override
        public Overlay createFromParcel(Parcel in) {
            return new Overlay(in);
        }

        @Override
        public Overlay[] newArray(int size) {
            return new Overlay[size];
        }
    };

    String getVideoPath() {
        return videoPath;
    }

    int getInsertTime() {
        return insertTime;
    }

    int getLocationX() {
        return locationX;
    }

    int getLocationY() {
        return locationY;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoPath);
        dest.writeInt(insertTime);
        dest.writeInt(locationX);
        dest.writeInt(locationY);
    }
}
