package com.lab.ugcmodule.media.ffmpeg.cmd;

import com.lab.ugcmodule.media.ffmpeg.FFmpegNative;
import com.lab.ugcmodule.media.ffmpeg.OperatorUtils;

/**
 * 快放／慢放
 * Created by kuaigeng01 on 2017/6/29.
 */
public class FastOrSlowVideoCommand extends BaseCommand {

    //视频和音频都加速4倍播放
    //fmpeg -y -i %s -filter_complex [0:v]setpts=0.25*PTS[v];[0:a]atempo=2.0,atempo=2.0[a] -map [v] -map [a] %s;

    private FastOrSlowVideoCommand(String cmd) {
        super(cmd);
    }

    public static class Builder implements IBuilder {
        String inputVideoFilePath;
        float multiple;
        String outputVideoPath;

        public Builder setInputVideoFilePath(String inputVideoFilePath) {
            this.inputVideoFilePath = inputVideoFilePath;
            return this;
        }

        public Builder setMultiple(float multiple) {
            if (multiple < 0.5 || multiple > 2) {
                throw new IllegalArgumentException("multiple value is illegal : " + multiple + "; it's must be >= 0.5 and <= 2");
            }
            this.multiple = multiple;

            return this;
        }

        public Builder setOutputVideoPath(String outputVideoPath) {
            this.outputVideoPath = outputVideoPath;
            return this;
        }

        @Override
        public Command build() {
            boolean isFast = multiple > 1;

            StringBuilder cmdBuilder = new StringBuilder();
            String split = FFmpegNative.SPLIT;

            cmdBuilder.append("ffmpeg").append(split);
            cmdBuilder.append("-y").append(split);
            cmdBuilder.append("-i").append(split);
            cmdBuilder.append(inputVideoFilePath).append(split);

            cmdBuilder.append("-filter_complex").append(split);
            cmdBuilder.append("[0:v]setpts=");

//            if (isFast) {
                cmdBuilder.append(OperatorUtils.formatDecimal(1.0 / multiple, 3));
//            } else {
//                cmdBuilder.append(OperatorUtils.formatDecimal(multiple, 3));
//            }

            cmdBuilder.append("*PTS[v];");

//            if (isFast) {
                cmdBuilder.append("atempo=").append(OperatorUtils.formatDecimal(multiple, 3));
//            } else {
//                cmdBuilder.append("atempo=").append(OperatorUtils.formatDecimal(1.0 / multiple, 3));
//            }

            cmdBuilder.append("[a]").append(split);
            cmdBuilder.append("-map").append(split);
            cmdBuilder.append("[v]").append(split);
            cmdBuilder.append("-map").append(split);
            cmdBuilder.append("[a]").append(split);
            cmdBuilder.append("-c:v libx264 -preset ultrafast -crf 18").append(split);
            cmdBuilder.append(outputVideoPath);

            return new FastOrSlowVideoCommand(cmdBuilder.toString());
        }
    }
}
