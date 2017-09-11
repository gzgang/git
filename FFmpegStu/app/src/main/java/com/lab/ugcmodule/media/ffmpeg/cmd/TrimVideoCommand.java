package com.lab.ugcmodule.media.ffmpeg.cmd;

import com.lab.ugcmodule.media.ffmpeg.OperatorUtils;

/**
 * 音视频裁剪
 * Created by kuaigeng01 on 2017/6/14.
 */
public class TrimVideoCommand extends BaseCommand {

    private static final String CMD = "ffmpeg -y -ss %d -t %d -accurate_seek -i %s -codec copy -avoid_negative_ts 1 %s";

    private TrimVideoCommand(String command) {
        super(command);
    }

    public static class Builder implements IBuilder {

        int startTime;
        int duration;
        String inputFile;
        String outputFile;

        public Builder setStartTime(int startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder setInputFile(String inputFile) {
            this.inputFile = inputFile;
            return this;
        }

        public Builder setOutputFile(String outputFile) {
            this.outputFile = outputFile;
            return this;
        }


        @Override
        public Command build() {
            String cmd = OperatorUtils.cmdFormat(CMD, startTime, duration, inputFile, outputFile);

            return new TrimVideoCommand(cmd);
        }
    }
}
