package com.lab.ugcmodule.media.ffmpeg.cmd;

import com.lab.ugcmodule.media.ffmpeg.OperatorUtils;

/**
 * 视频压缩
 * Created by kuaigeng01 on 2017/7/3.
 */
public class CompressVideoCommand extends BaseCommand {
    private static final String CMD = "ffmpeg -y -i %s -vcodec libx264 -crf %d -preset ultrafast -acodec copy %s";

    private CompressVideoCommand(String cmd) {
        super(cmd);
    }

    public static class Builder implements IBuilder {
        String inputVideoFilePath;
        String outputVideoFilePath;
        int quality = 23;

        public Builder setInputVideoFilePath(String inputVideoFilePath) {
            this.inputVideoFilePath = inputVideoFilePath;
            return this;
        }

        public Builder setQuality(int quality) {
            if (quality < 0 || quality > 51) {
                throw new IllegalArgumentException("quality must between 0 and 51");
            }

            this.quality = quality;

            return this;
        }

        public Builder setOutputVideoPath(String outputVideoPath) {
            this.outputVideoFilePath = outputVideoPath;
            return this;
        }

        @Override
        public Command build() {

            String cmd = OperatorUtils.cmdFormat(CMD, inputVideoFilePath, quality, outputVideoFilePath);
            return new CompressVideoCommand(cmd);
        }
    }
}
