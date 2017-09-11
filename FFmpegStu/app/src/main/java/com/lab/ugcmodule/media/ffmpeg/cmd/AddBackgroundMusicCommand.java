package com.lab.ugcmodule.media.ffmpeg.cmd;

import com.lab.ugcmodule.media.ffmpeg.OperatorUtils;

/**
 * 添加背景音乐
 * Created by kuaigeng01 on 2017/7/3.
 */
public class AddBackgroundMusicCommand extends BaseCommand {
    private static final String CMD = "ffmpeg -y -i %s -i %s -filter_complex [0:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=%.2f[a0];[1:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=%.2f[a1];[a0][a1]amix=inputs=2:duration=first[aout] -map [aout] -ac 2 -c:v copy -map 0:v:0 %s";

    private AddBackgroundMusicCommand(String cmd) {
        super(cmd);
    }

    public static class Builder implements IBuilder {
        String inputVideoFilePath;
        float originVideoVolume;
        float backgroundMusicVolume;
        String outputVideoPath;
        String inputBackgroundMusicFilePath;

        public Builder setInputVideoFilePath(String inputVideoFilePath) {
            this.inputVideoFilePath = inputVideoFilePath;
            return this;
        }

        public Builder setOriginVideoVolume(float originVideoVolume) {
            this.originVideoVolume = originVideoVolume;
            return this;
        }

        public Builder setBackgroundMusicVolume(float backgroundMusicVolume) {
            this.backgroundMusicVolume = backgroundMusicVolume;
            return this;
        }

        public Builder setInputBackgroundMusicFilePath(String inputBackgroundMusicFilePath) {
            this.inputBackgroundMusicFilePath = inputBackgroundMusicFilePath;
            return this;
        }

        public Builder setOutputVideoPath(String outputVideoPath) {
            this.outputVideoPath = outputVideoPath;
            return this;
        }

        @Override
        public Command build() {

            String cmd = OperatorUtils.cmdFormat(CMD, inputVideoFilePath, inputBackgroundMusicFilePath, originVideoVolume, backgroundMusicVolume, outputVideoPath);
            return new AddBackgroundMusicCommand(cmd);
        }
    }
}
