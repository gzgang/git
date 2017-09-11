package com.lab.ugcmodule.media.ffmpeg.cmd;

import com.kg.v1.global.CommonUtils;
import com.lab.ugcmodule.media.ffmpeg.OperatorUtils;

/**
 * 调节音量
 * Created by kuaigeng01 on 2017/6/15.
 */
public class AdjustVolumeCommand extends BaseCommand {
    private static final String CMD = "ffmpeg -y -i %s -vol %d -strict -2 -vcodec copy %s";

    private AdjustVolumeCommand(String cmd) {
        super(cmd);
    }

    public static class Builder implements IBuilder {
        String inputVideoFilePath;
        float volumePercent;
        String outputVideoPath;

        public Builder setInputVideoFilePath(String inputVideoFilePath) {
//            this.inputVideoFilePath = CommonUtils.encode(inputVideoFilePath);
            this.inputVideoFilePath = inputVideoFilePath;
            return this;
        }

        public Builder setVolumePercent(float volumePercent) {
            if (volumePercent < 0.01f) {

                volumePercent = 0.01f;
            } else if (volumePercent > 10) {

                volumePercent = 10;
            }

            this.volumePercent = volumePercent;

            return this;
        }

        public Builder setOutputVideoPath(String outputVideoPath) {
            this.outputVideoPath = outputVideoPath;
            return this;
        }

        @Override
        public Command build() {

            int intVolumePercent = (int) (volumePercent * 100);
            String cmd = OperatorUtils.cmdFormat(CMD, inputVideoFilePath, intVolumePercent, outputVideoPath);

            return new AdjustVolumeCommand(cmd);
        }
    }
}
