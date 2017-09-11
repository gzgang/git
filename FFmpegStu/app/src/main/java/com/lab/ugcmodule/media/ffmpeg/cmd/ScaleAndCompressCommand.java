package com.lab.ugcmodule.media.ffmpeg.cmd;

import com.lab.ugcmodule.media.ffmpeg.OperatorUtils;

/**
 * 视频压缩
 * Created by kuaigeng01 on 2017/7/3.
 */
public class ScaleAndCompressCommand extends BaseCommand {
    private static final String CMD = "ffmpeg -y -i %s -vf scale=%s -c:a copy -c:v libx264 -preset veryfast %s";

    private ScaleAndCompressCommand(String cmd) {
        super(cmd);
    }

    public static class Builder implements IBuilder {
        String inputVideoFilePath;
        String outputVideoFilePath;
        int width, height;

        public Builder setInputVideoFilePath(String inputVideoFilePath) {
            this.inputVideoFilePath = inputVideoFilePath;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setOutputVideoPath(String outputVideoPath) {
            this.outputVideoFilePath = outputVideoPath;
            return this;
        }

        @Override
        public Command build() {

            String scale = width + "x" + height;
            String cmd = OperatorUtils.cmdFormat(CMD, inputVideoFilePath, scale, outputVideoFilePath);
            return new ScaleAndCompressCommand(cmd);
        }
    }
}
