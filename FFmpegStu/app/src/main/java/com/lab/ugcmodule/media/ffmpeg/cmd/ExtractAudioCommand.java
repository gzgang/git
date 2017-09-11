package com.lab.ugcmodule.media.ffmpeg.cmd;

import com.lab.ugcmodule.media.ffmpeg.OperatorUtils;

import java.util.Locale;


/**
 * 从视频中提取音频：删除视频
 * Created by kuaigeng01 on 2017/6/22.
 */
public class ExtractAudioCommand extends BaseCommand {
    private static final String CMD = "ffmpeg -y -i %s -vn %s";
    private static final String CMD_accelerate_aac = "ffmpeg -y -i %s -acodec copy -vn %s";

    private ExtractAudioCommand(String cmd) {
        super(cmd);
    }

    public static class Builder implements IBuilder {

        String inputVideoFilePath;
        String outputAudioPath;

        public Builder setInputVideoFilePath(String inputVideoFilePath) {
            this.inputVideoFilePath = inputVideoFilePath;
            return this;
        }

        public Builder setOutputAudioPath(String outputAudioPath) {
            this.outputAudioPath = outputAudioPath;
            return this;
        }

        @Override
        public Command build() {

            String command;
            String lowCase = outputAudioPath.toLowerCase(Locale.US);

            if (lowCase.endsWith(".aac")) {
                command = CMD_accelerate_aac;
            } else {
                command = CMD;
            }

            String cmd = OperatorUtils.cmdFormat(command, inputVideoFilePath, outputAudioPath);

            return new ExtractAudioCommand(cmd);
        }
    }
}
