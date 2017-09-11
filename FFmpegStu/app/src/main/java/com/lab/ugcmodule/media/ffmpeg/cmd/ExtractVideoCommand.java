package com.lab.ugcmodule.media.ffmpeg.cmd;

import com.lab.ugcmodule.media.ffmpeg.OperatorUtils;

/**
 * 从视频中提取纯视频：删除音频
 * Created by kuaigeng01 on 2017/6/15.
 */
public class ExtractVideoCommand extends BaseCommand {
    private static final String CMD = "ffmpeg -y -i %s -vcodec copy -an %s";

    private ExtractVideoCommand(String cmd) {
        super(cmd);
    }

    public static class Builder implements IBuilder {

        String inputVideoFilePath;
        String outputVideoPath;

        public Builder setInputVideoFilePath(String inputVideoFilePath) {
            this.inputVideoFilePath = inputVideoFilePath;
            return this;
        }

        public Builder setOutputVideoPath(String outputVideoPath) {
            this.outputVideoPath = outputVideoPath;
            return this;
        }

        @Override
        public Command build() {
            String cmd = OperatorUtils.cmdFormat(CMD, inputVideoFilePath, outputVideoPath);

            return new ExtractVideoCommand(cmd);
        }
    }
}
