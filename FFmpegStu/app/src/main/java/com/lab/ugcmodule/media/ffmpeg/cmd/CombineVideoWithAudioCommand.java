package com.lab.ugcmodule.media.ffmpeg.cmd;

import com.lab.ugcmodule.media.ffmpeg.OperatorUtils;

/**
 * 合并视频和音频
 * Created by kuaigeng01 on 2017/6/22.
 */

public class CombineVideoWithAudioCommand extends BaseCommand {
    private static final String CMD = "ffmpeg -y -i %s -i %s -shortest -codec copy %s";

    private CombineVideoWithAudioCommand(String cmd) {
        super(cmd);
    }

    public static class Builder implements IBuilder {

        String inputVideoFilePath;
        String inputAudioFilePath;
        String outputVideoPath;

        public Builder setInputVideoFilePath(String inputVideoFilePath) {
            this.inputVideoFilePath = inputVideoFilePath;
            return this;
        }

        public Builder setOutputVideoPath(String outputVideoPath) {
            this.outputVideoPath = outputVideoPath;
            return this;
        }

        public Builder setInputAudioFilePath(String inputAudioFilePath) {
            this.inputAudioFilePath = inputAudioFilePath;
            return this;
        }

        @Override
        public Command build() {

            String cmd = OperatorUtils.cmdFormat(CMD, inputVideoFilePath, inputAudioFilePath, outputVideoPath);

            return new CombineVideoWithAudioCommand(cmd);
        }
    }
}
