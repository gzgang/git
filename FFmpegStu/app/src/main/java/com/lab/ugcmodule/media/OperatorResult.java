package com.lab.ugcmodule.media;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kuaigeng01 on 2017/6/14.
 */

public class OperatorResult implements Parcelable {

    private final boolean success;
    private final String output;

    public OperatorResult(boolean success, String output) {
        this.success = success;
        this.output = output;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getOutput() {
        return output;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(success ? 1 : 0);
        dest.writeString(output);
    }

    private OperatorResult(Parcel in) {
        success = in.readInt() == 1;
        output = in.readString();
    }

    public static Parcelable.Creator<OperatorResult> CREATOR = new Parcelable.Creator<OperatorResult>() {
        @Override
        public OperatorResult createFromParcel(Parcel source) {

            return new OperatorResult(source);
        }

        @Override
        public OperatorResult[] newArray(int size) {
            return new OperatorResult[size];
        }
    };

    @Override
    public String toString() {
        return "OperatorResult{" +
                "success=" + success +
                ", output='" + output + '\'' +
                '}';
    }
}
