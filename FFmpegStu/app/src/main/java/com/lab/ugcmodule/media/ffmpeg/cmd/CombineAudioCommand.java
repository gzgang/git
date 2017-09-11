package com.lab.ugcmodule.media.ffmpeg.cmd;

import com.lab.ugcmodule.media.ffmpeg.OperatorUtils;

/**
 * 合并两个音频：相当于给音乐再添加背景音乐
 * Created by kuaigeng01 on 2017/6/22.
 */
public class CombineAudioCommand extends BaseCommand {
    private static final String CMD = "ffmpeg -y -i %s -i %s -filter_complex amix=inputs=2:duration=first:dropout_transition=2 -vn %s";

    private CombineAudioCommand(String cmd) {
        super(cmd);
    }

    public static class Builder implements IBuilder {
        String inputAudioFilePath;
        String inputBackgroundMusicFilePath;
        String outputAudioPath;

        public Builder setInputAudioFilePath(String inputAudioFilePath) {
            this.inputAudioFilePath = inputAudioFilePath;
            return this;
        }

        public Builder setOutputAudioPath(String outputAudioPath) {
            this.outputAudioPath = outputAudioPath;
            return this;
        }

        public Builder setInputBackgroundMusicFilePath(String inputBackgroundMusicFilePath) {
            this.inputBackgroundMusicFilePath = inputBackgroundMusicFilePath;
            return this;
        }

        @Override
        public Command build() {

            String cmd = OperatorUtils.cmdFormat(CMD, inputAudioFilePath, inputBackgroundMusicFilePath, outputAudioPath);

            return new CombineAudioCommand(cmd);
        }
    }
}
