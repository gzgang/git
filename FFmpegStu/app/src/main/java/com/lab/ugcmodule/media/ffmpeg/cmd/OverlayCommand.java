package com.lab.ugcmodule.media.ffmpeg.cmd;

import android.support.annotation.NonNull;

import com.lab.ugcmodule.media.ffmpeg.FFmpegNative;

import java.util.List;

/**
 * 在视频上面叠加视频
 * Created by kuaigeng01 on 2017/7/4.
 */

public class OverlayCommand extends BaseCommand {
    //private static final String CMD = "ffmpeg -i %s -i %s -filter_complex [1]setpts=PTS-STARTPTS+%d/TB[top];[0:0][top]overlay=repeatlast=0=enable='gte(t\\,%d)'[vout];[1]adelay=%d[atop];[0:a][atop]amix=inputs=2:duration=first:dropout_transition=2[aout] -map [vout] -map [aout] -c:v libx264 -preset ultrafast -crf 18 %s";

    private OverlayCommand(@NonNull String command) {
        super(command);
    }

    public static class Builder implements IBuilder {

        String inputVideoFilePath;
        String outputVideoFilePath;
        List<Overlay> overlayList;

        public Builder setInputVideoFilePath(@NonNull String inputVideoFilePath) {
            this.inputVideoFilePath = inputVideoFilePath;
            return this;
        }

        public Builder setOutputVideoPath(@NonNull String outputVideoFilePath) {
            this.outputVideoFilePath = outputVideoFilePath;
            return this;
        }

        public Builder setOverlayList(@NonNull List<Overlay> overlayList) {
            this.overlayList = overlayList;
            return this;
        }

        @Override
        public Command build() {

            StringBuilder builder = new StringBuilder();
            String split = FFmpegNative.SPLIT;
            builder.append("ffmpeg").append(split);
            builder.append("-y").append(split);

            builder.append("-i").append(split);
            builder.append(inputVideoFilePath).append(split);

            for (Overlay overlay : overlayList) {
                builder.append("-i").append(split);
                builder.append(overlay.getVideoPath()).append(split);
            }

            builder.append("-filter_complex").append(split);

            final String vov = "vov";
            final String vov2 = "vov2";

            int size = overlayList.size();
            for (int i = 0; i < size; i++) {
                builder.append("[" + (i + 1) + "]setpts=PTS-STARTPTS+" + overlayList.get(i).getInsertTime() + "/TB[" + vov + i + "];");
            }

            String VideoOut = "[0:v]";
            for (int i = 0; i < size; i++) {
                builder.append(VideoOut).append("[" + vov + i + "]").append("overlay=x=" + overlayList.get(i).getLocationX() + ":y=" + overlayList.get(i).getLocationY() + ":repeatlast=0:enable='gte(t\\,").append(overlayList.get(i).getInsertTime()).append(")'");
                VideoOut = "[" + (vov2 + i) + "]";
                builder.append(VideoOut).append(";");
            }

            final String aoa = "aoa";
            final String AudioOut = "[aout]";

            for (int i = 0; i < size; i++) {
                builder.append("[" + (i + 1) + "]adelay=")

                        //设置多个音道延迟:可多不可少
                        .append(overlayList.get(i).getInsertTime() * 1000)
                        .append("|")
                        .append(overlayList.get(i).getInsertTime() * 1000)
                        .append("|")
                        .append(overlayList.get(i).getInsertTime() * 1000)
                        .append("|")
                        .append(overlayList.get(i).getInsertTime() * 1000)

                        .append("[" + aoa + i + "]").append(";");
            }

            builder.append("[0:a]");
            for (int i = 0; i < size; i++) {
                builder.append("[" + (aoa + i) + "]");
            }

            builder.append("amix=inputs=").append(size + 1).append(":duration=first:dropout_transition=2").append(AudioOut).append(split);

            builder.append("-map").append(split).append(VideoOut).append(split);
            builder.append("-map").append(split).append(AudioOut).append(split);

            builder.append("-c:v").append(split);
            builder.append("libx264").append(split);
            builder.append("-preset").append(split);
            builder.append("ultrafast").append(split);
            builder.append("-crf").append(split);
            builder.append("18").append(split);
            builder.append("-c:a").append(split);
            builder.append("aac").append(split);

            builder.append(outputVideoFilePath);

            return new OverlayCommand(builder.toString());
        }
    }
}
