package com.lab.ugcmodule.media.ffmpeg.cmd;

import android.text.TextUtils;

import com.lab.ugcmodule.media.ffmpeg.FFmpegNative;
import com.lab.ugcmodule.media.ffmpeg.Mini;
import com.lab.ugcmodule.media.service.MediaOperatorParams;

import java.util.List;
import java.util.Locale;

/**
 * 合并ffmpeg命令
 * Created by kuaigeng01 on 2017/7/16.
 */
public class ComplexCommand extends BaseCommand {

    private ComplexCommand(String cmd) {
        super(cmd);
    }

    public static class Builder implements IBuilder {

        String inputVideoFilePath;
        String outputVideoFilePath;

        List<MediaOperatorParams> operatorParamsList;

        public Builder setInputVideoFilePath(String inputVideoFilePath) {
            this.inputVideoFilePath = inputVideoFilePath;
            return this;
        }

        public Builder setOutputVideoPath(String outputVideoFilePath) {
            this.outputVideoFilePath = outputVideoFilePath;
            return this;
        }

        public Builder setOperatorParamsList(List<MediaOperatorParams> operatorParamsList) {
            this.operatorParamsList = operatorParamsList;
            return this;
        }

        @Override
        public Command build() {
            //TODO 目前这里有个前提条件，要有加水印命令，后面再增加 加音乐，加视频吧

            StringBuilder cmdBuilder = new StringBuilder();
            String split = FFmpegNative.SPLIT;

            cmdBuilder.append("ffmpeg").append(split);
            cmdBuilder.append("-y").append(split);
            cmdBuilder.append("-i").append(split);
            cmdBuilder.append(inputVideoFilePath).append(split);

            String otherInput = getOtherInput(operatorParamsList);

            if (!TextUtils.isEmpty(otherInput)) {
                cmdBuilder.append(otherInput);
            }


            //============================= vCodec
            MediaOperatorParams watermarkCmd = Mini.query(MediaOperatorParams.CMD_WATERMARK, operatorParamsList);
            MediaOperatorParams compressCmd = Mini.query(MediaOperatorParams.CMD_COMPRESS, operatorParamsList);

            //是否使用滤镜链
            boolean needFilterComplex = null != watermarkCmd;
            //是否需要重新编码视频
            boolean needReVcodec = null != watermarkCmd || null != compressCmd;

            if (needFilterComplex) {
                cmdBuilder.append("-filter_complex").append(split);
            }

            //1.水印
            String vCodecInputAndOutput = "[0:v]";
            if (null != watermarkCmd) {
                String[] filter = filterComplex_watermark(vCodecInputAndOutput, watermarkCmd.getWatermarks());

                cmdBuilder.append(filter[0]).append(split);
                vCodecInputAndOutput = filter[1];
            }

            if (needReVcodec) {
                cmdBuilder.append("-c:v").append(split);
                cmdBuilder.append("libx264").append(split);
                cmdBuilder.append("-preset").append(split);
                cmdBuilder.append("ultrafast").append(split);
                cmdBuilder.append("-crf").append(split);

                if (null != compressCmd) {
                    cmdBuilder.append(compressCmd.getIntArg1()).append(split);
                } else {
                    cmdBuilder.append("18").append(split);
                }
            } else {
                cmdBuilder.append("-c:v").append(split);
                cmdBuilder.append("copy").append(split);
            }

            //============================= aCodec
            String aCodecInputAndOutput = "[0:a]";
            MediaOperatorParams closeVolume = Mini.query(MediaOperatorParams.CMD_EXTRACT_VIDEO, operatorParamsList);
            MediaOperatorParams adjustVolume = Mini.query(MediaOperatorParams.CMD_ADJUST_VOLUME, operatorParamsList);

            if (null != closeVolume) {//有关闭声音，就不要有调节声音

                cmdBuilder.append("-an").append(split);
            } else if (null != adjustVolume) {

                float volumePercent = adjustVolume.getFloatArg1();
                cmdBuilder.append("-af").append(split);
                cmdBuilder.append(String.format(Locale.ENGLISH, "volume=%.2f", volumePercent)).append(split);
            } else {

                cmdBuilder.append("-c:a").append(split);
                cmdBuilder.append("copy").append(split);
            }

            cmdBuilder.append(outputVideoFilePath);

            return new ComplexCommand(cmdBuilder.toString());
        }


        private String getOtherInput(List<MediaOperatorParams> paramses) {
            String split = FFmpegNative.SPLIT;

            MediaOperatorParams watermarkCommand = Mini.query(MediaOperatorParams.CMD_WATERMARK, paramses);
            StringBuilder cmdBuilder = new StringBuilder();

            if (null != watermarkCommand) {
                List<Watermark> watermarks = watermarkCommand.getWatermarks();
                for (Watermark watermark : watermarks) {
                    if (watermark.isGif()) {
                        cmdBuilder.append("-ignore_loop").append(split);
                        cmdBuilder.append("0").append(split);
                    }

                    cmdBuilder.append("-i").append(split);
                    cmdBuilder.append(watermark.getImagePath()).append(split);
                }
            }

            return cmdBuilder.toString();
        }

    }


    private static String[] filterComplex_watermark(String inputVCodec, List<Watermark> watermarks) {
        Watermark watermarkItem;
        int size = watermarks.size();
        String inputAndOutput = inputVCodec;

        StringBuilder paramsBuilder = new StringBuilder();

        for (int i = 0; i < size; i++) {
            watermarkItem = watermarks.get(i);

            paramsBuilder.append(inputAndOutput + "[" + (i + 1) + ":v]overlay=" + watermarkItem.getLocationX() + ":" + watermarkItem.getLocationY());

            if (watermarkItem.isGif()) {
                paramsBuilder.append(":shortest=1");
            }

            if (i < size - 1) {
                inputAndOutput = "[bkg" + i + "]";
                paramsBuilder.append(inputAndOutput);
                paramsBuilder.append(";");
            }
        }

        //返回filter命令 和输出
        return new String[]{paramsBuilder.toString(), inputAndOutput};
    }
}
