package com.lab.ugcmodule.media.service;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.kg.v1.global.Global;
import com.lab.ugcmodule.media.ffmpeg.OperatorUtils;
import com.lab.ugcmodule.media.ffmpeg.cmd.Overlay;
import com.lab.ugcmodule.media.ffmpeg.cmd.Watermark;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 参数
 * Created by kuaigeng01 on 2017/6/23.
 */

public class MediaOperatorParams implements Parcelable {

    public static final int CMD_TRIM = 0x1;
    public static final int CMD_CONCAT = CMD_TRIM + 1;
    public static final int CMD_WATERMARK = CMD_CONCAT + 1;
    public static final int CMD_BACKGROUND_MUSIC = CMD_WATERMARK + 1;
    public static final int CMD_EXTRACT_VIDEO = CMD_BACKGROUND_MUSIC + 1;
    public static final int CMD_ADJUST_VOLUME = CMD_EXTRACT_VIDEO + 1;
    public static final int CMD_FAST_SLOW_VIDEO = CMD_ADJUST_VOLUME + 1;
    public static final int CMD_ADD_FILTER = CMD_FAST_SLOW_VIDEO + 1;
    public static final int CMD_COMPRESS = CMD_ADD_FILTER + 1;
    public static final int CMD_OVERLAY = CMD_COMPRESS + 1;
    public static final int CMD_PRIVATE_COMPLEX = CMD_OVERLAY + 1;
    public static final int CMD_SCALE_COMPRESS = CMD_PRIVATE_COMPLEX + 1;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CMD_TRIM, CMD_CONCAT, CMD_WATERMARK, CMD_BACKGROUND_MUSIC, CMD_EXTRACT_VIDEO, CMD_ADJUST_VOLUME, CMD_FAST_SLOW_VIDEO, CMD_ADD_FILTER, CMD_COMPRESS, CMD_OVERLAY, CMD_PRIVATE_COMPLEX, CMD_SCALE_COMPRESS})
    public @interface CmdTypeDef {
    }

    @CmdTypeDef
    private final int cmdTypeDef;

    private String inputMediaFilePath;
    private String outputMediaFilePath;
    private String inputBackgroundMusicPath;
    private List<Watermark> watermarks;
    private List<Overlay> videoOverlays;
    private List<String> inputVideoFilePathList;
    private int intArg1;
    private int intArg2;
    private float floatArg1;
    private String stringArg1;

    private List<MediaOperatorParams> multiplesOperator;

    private MediaOperatorParams(@CmdTypeDef int cmdType) {
        cmdTypeDef = cmdType;
    }

    @CmdTypeDef
    public int getCmdTypeDef() {
        return cmdTypeDef;
    }

    public String getInputMediaFilePath() {
        return inputMediaFilePath;
    }

    void setInputMediaFilePath(String inputMediaFilePath) {
        this.inputMediaFilePath = inputMediaFilePath;
    }

    public String getOutputMediaFilePath() {
        return outputMediaFilePath;
    }

    void setOutputMediaFilePath(String outputMediaFilePath) {
        this.outputMediaFilePath = outputMediaFilePath;
    }

    public String getInputBackgroundMusicPath() {
        return inputBackgroundMusicPath;
    }

    void setInputBackgroundMusicPath(String inputBackgroundMusicPath) {
        this.inputBackgroundMusicPath = inputBackgroundMusicPath;
    }

    public List<Watermark> getWatermarks() {
        return watermarks;
    }

    void setWatermarks(List<Watermark> watermarks) {
        this.watermarks = watermarks;
    }

    public List<Overlay> getVideoOverlays() {
        return videoOverlays;
    }

    void setVideoOverlays(List<Overlay> videoOverlays) {
        this.videoOverlays = videoOverlays;
    }

    public List<String> getInputVideoFilePathList() {
        return inputVideoFilePathList;
    }

    void setInputVideoFilePathList(List<String> inputVideoFilePathList) {
        this.inputVideoFilePathList = inputVideoFilePathList;
    }

    public int getIntArg1() {
        return intArg1;
    }

    void setIntArg1(int intArg1) {
        this.intArg1 = intArg1;
    }

    public int getIntArg2() {
        return intArg2;
    }

    void setIntArg2(int intArg2) {
        this.intArg2 = intArg2;
    }

    public float getFloatArg1() {
        return floatArg1;
    }

    void setFloatArg1(float floatArg1) {
        this.floatArg1 = floatArg1;
    }

    public String getStringArg1() {
        return stringArg1;
    }

    void setStringArg1(String stringArg1) {
        this.stringArg1 = stringArg1;
    }

    public List<MediaOperatorParams> getMultiplesOperator() {
        return multiplesOperator;
    }

    void setMultiplesOperator(List<MediaOperatorParams> multiplesOperator) {
        this.multiplesOperator = multiplesOperator;
    }

    long getMediaDuration() {

        long du = OperatorUtils.getMediaFileDuration(inputMediaFilePath);

        if (cmdTypeDef == CMD_FAST_SLOW_VIDEO) {
            du = (long) (du / floatArg1);
        }

        return du;
    }

    String generateDefaultOutputFilePath() {
        String defaultOutput = null;

        switch (cmdTypeDef) {
            case CMD_WATERMARK:

                defaultOutput = OperatorUtils.getFfmpegCacheTmpDir(Global.getGlobalContext()) + "/tmp_generate_watermark.mp4";
                break;
            case CMD_EXTRACT_VIDEO:

                defaultOutput = OperatorUtils.getFfmpegCacheTmpDir(Global.getGlobalContext()) + "/tmp_generate_silent.mp4";
                break;
            case CMD_ADD_FILTER:

                defaultOutput = OperatorUtils.getFfmpegCacheTmpDir(Global.getGlobalContext()) + "/tmp_generate_filter.mp4";
                break;
            case CMD_ADJUST_VOLUME:

                defaultOutput = OperatorUtils.getFfmpegCacheTmpDir(Global.getGlobalContext()) + "/tmp_generate_adjust_volume.mp4";
                break;
            case CMD_COMPRESS:

                defaultOutput = OperatorUtils.getFfmpegCacheTmpDir(Global.getGlobalContext()) + "/tmp_generate_compress.mp4";
                break;
            case CMD_PRIVATE_COMPLEX:

                defaultOutput = OperatorUtils.getFfmpegCacheTmpDir(Global.getGlobalContext()) + "/tmp_generate_complex.mp4";
                break;
            default:

                throw new IllegalStateException("you don't have set default generateDefaultOutputFilePath for cmdTypeDef = " + cmdTypeDef);
        }

        return defaultOutput;
    }

    //===========================================================
    //
    //===========================================================

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cmdTypeDef);
        dest.writeString(inputMediaFilePath);
        dest.writeString(outputMediaFilePath);
        dest.writeString(inputBackgroundMusicPath);
        dest.writeString(stringArg1);
        dest.writeInt(intArg1);
        dest.writeInt(intArg2);
        dest.writeFloat(floatArg1);

        dest.writeStringList(inputVideoFilePathList);
        dest.writeList(watermarks);
        dest.writeList(videoOverlays);
        dest.writeList(multiplesOperator);
    }

    private MediaOperatorParams(Parcel parcel) {
        cmdTypeDef = parcel.readInt();

        inputMediaFilePath = parcel.readString();
        outputMediaFilePath = parcel.readString();
        inputBackgroundMusicPath = parcel.readString();
        stringArg1 = parcel.readString();
        intArg1 = parcel.readInt();
        intArg2 = parcel.readInt();
        floatArg1 = parcel.readFloat();

        inputVideoFilePathList = new ArrayList<>();
        parcel.readStringList(inputVideoFilePathList);

        watermarks = parcel.readArrayList(Watermark.class.getClassLoader());
        videoOverlays = parcel.readArrayList(Overlay.class.getClassLoader());
        multiplesOperator = parcel.readArrayList(MediaOperatorParams.class.getClassLoader());
    }

    public static Creator<MediaOperatorParams> CREATOR = new Creator<MediaOperatorParams>() {
        @Override
        public MediaOperatorParams createFromParcel(Parcel source) {
            return new MediaOperatorParams(source);
        }

        @Override
        public MediaOperatorParams[] newArray(int size) {
            return new MediaOperatorParams[size];
        }
    };

    @Override
    public String toString() {
        return "MediaOperatorParams{" +
                "cmdTypeDef=" + cmdTypeDef +
                ", inputMediaFilePath='" + inputMediaFilePath + '\'' +
                ", outputMediaFilePath='" + outputMediaFilePath + '\'' +
                ", inputBackgroundMusicPath='" + inputBackgroundMusicPath + '\'' +
                ", intArg1=" + intArg1 +
                ", intArg2=" + intArg2 +
                ", floatArg1=" + floatArg1 +
                ", stringArg1='" + stringArg1 + '\'' +
                '}';
    }

    //===========================================================
    //
    //===========================================================

    public static class Builder {

        List<MediaOperatorParams> multiplesOperator;

        String inputMediaFilePath;
        String outputMediaFilePath;
        String inputBackgroundMusicPath;
        List<Watermark> watermarks;
        List<Overlay> videoOverlays;
        List<String> inputVideoFilePathList;
        String stringArg1;
        int intArg1;
        int intArg2;
        float floatArg1;

        public Builder setInputVideoFilePathList(List<String> inputVideoFilePathList) {
            this.inputVideoFilePathList = inputVideoFilePathList;
            return this;
        }

        public Builder setInputMediaFilePath(String inputMediaFilePath) {
            this.inputMediaFilePath = inputMediaFilePath;
            return this;
        }

        public Builder setOutputMediaFilePath(String outputMediaFilePath) {
            this.outputMediaFilePath = outputMediaFilePath;
            return this;
        }

        public Builder setInputBackgroundMusicPath(String inputBackgroundMusicPath) {
            this.inputBackgroundMusicPath = inputBackgroundMusicPath;
            return this;
        }

        public Builder setWatermarks(List<Watermark> watermarks) {
            this.watermarks = watermarks;
            return this;
        }

        public Builder setVideoOverlays(List<Overlay> videoOverlays) {
            this.videoOverlays = videoOverlays;
            return this;
        }

        public Builder setIntArg1(int intArg1) {
            this.intArg1 = intArg1;
            return this;
        }

        public Builder setIntArg2(int intArg2) {
            this.intArg2 = intArg2;
            return this;
        }

        public Builder setFloatArg1(float floatArg1) {
            this.floatArg1 = floatArg1;
            return this;
        }

        public Builder setStringArg1(String stringArg1) {
            this.stringArg1 = stringArg1;
            return this;
        }

        public Builder setMultiplesOperator(List<MediaOperatorParams> multiplesOperator) {
            this.multiplesOperator = multiplesOperator;
            return this;
        }

        public MediaOperatorParams build(@CmdTypeDef int cmdType) {
            MediaOperatorParams params = new MediaOperatorParams(cmdType);

            params.setInputMediaFilePath(inputMediaFilePath);
            params.setOutputMediaFilePath(outputMediaFilePath);
            params.setInputBackgroundMusicPath(inputBackgroundMusicPath);
            params.setWatermarks(watermarks);
            params.setVideoOverlays(videoOverlays);
            params.setInputVideoFilePathList(inputVideoFilePathList);

            params.setStringArg1(stringArg1);
            params.setIntArg1(intArg1);
            params.setIntArg2(intArg2);
            params.setFloatArg1(floatArg1);

            params.setMultiplesOperator(multiplesOperator);

            return params;
        }
    }

}
