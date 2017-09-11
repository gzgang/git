package com.lab.ugcmodule.media.ffmpeg.cmd;

import com.lab.ugcmodule.media.ffmpeg.OperatorUtils;

/**
 * 将多个视频拼接成一个
 * Created by kuaigeng01 on 2017/6/15.
 */

public class ConcatVideoCommand extends BaseCommand {
    private static final String CMD = "ffmpeg -y -f concat -safe 0 -i %s -c copy %s";

    private ConcatVideoCommand(String command) {
        super(command);
    }

    public static class Builder implements IBuilder {
        String concatVideoListFilePath;
        String outputVideoPath;

        public Builder setConcatVideoListFilePath(String concatVideoListFilePath) {
            this.concatVideoListFilePath = concatVideoListFilePath;
            return this;
        }

        public Builder setOutputVideoPath(String outputVideoPath) {
            this.outputVideoPath = outputVideoPath;
            return this;
        }

        @Override
        public Command build() {
            String cmd = OperatorUtils.cmdFormat(CMD, concatVideoListFilePath, outputVideoPath);

            return new ConcatVideoCommand(cmd);
        }
    }
}
