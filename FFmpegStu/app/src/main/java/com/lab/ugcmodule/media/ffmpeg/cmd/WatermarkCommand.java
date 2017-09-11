package com.lab.ugcmodule.media.ffmpeg.cmd;

import com.lab.ugcmodule.media.ffmpeg.FFmpegNative;

import java.util.List;

/**
 * 给视频加水印
 * Created by kuaigeng01 on 2017/6/15.
 */
public class WatermarkCommand extends BaseCommand {
    /**
     * 1 选择一个CRF值
     * 量化比例的范围为0~51，其中0为无损模式，23为缺省值，51可能是最差的。该数字越小，图像质量越好。从主观上讲，18~28是一个合理的范围。18往往被认为从视觉上看是无损的，它的输出视频质量和输入视频一模一样或者说相差无几。但从技术的角度来讲，它依然是有损压缩。
     * 若Crf值加6，输出码率大概减少一半；若Crf值减6，输出码率翻倍。通常是在保证可接受视频质量的前提下选择一个最大的Crf值，如果输出视频质量很好，那就尝试一个更大的值，如果看起来很糟，那就尝试一个小一点值。
     * 注释：本文所提到的量化比例只适用于8-bitx264（10-bit x264的量化比例 为0~63），你可以使用x264 --help命令在Output bit depth选项查看输出位深，在各种版本中，8bit是最常见的。
     * <p>
     * 2 选择一个预设
     * 预设是一系列参数的集合，这个集合能够在编码速度和压缩率之间做出一个权衡。一个编码速度稍慢的预设会提供更高的压缩效率（压缩效率是以文件大小来衡量的)。这就是说，假如你想得到一个指定大小的文件或者采用恒定比特率编码模式，你可以采用一个较慢的预设来获得更好的质量。同样的，对于恒定质量编码模式，你可以通过选择一个较慢的预设轻松地节省比特率。
     * 如果你很有耐心，通常的建议是使用最慢的预设。目前所有的预设按照编码速度降序排列为：                      ultrafast,superfast,veryfast,faster,fast,medium,slow,slower,veryslow,placebo
     */

    private WatermarkCommand(String cmd) {
        super(cmd);
    }

    public static class Builder implements IBuilder {
        String inputVideoFilePath;
        List<Watermark> watermarks;
        String outputVideoPath;

        float volumePercent;

        protected Builder setVolumePercent(float volumePercent) {
            this.volumePercent = volumePercent;

            return this;
        }

        public Builder setInputVideoFilePath(String inputVideoFilePath) {
            this.inputVideoFilePath = inputVideoFilePath;
            return this;
        }

        public Builder setWatermarks(List<Watermark> watermarks) {
            this.watermarks = watermarks;
            return this;
        }

        public Builder setOutputVideoPath(String outputVideoPath) {
            this.outputVideoPath = outputVideoPath;
            return this;
        }

        @Override
        public Command build() {
            //http://www.jianshu.com/p/b30f07055e2e

            StringBuilder cmdBuilder = new StringBuilder();
            String split = FFmpegNative.SPLIT;

            cmdBuilder.append("ffmpeg").append(split);
            cmdBuilder.append("-y").append(split);
            cmdBuilder.append("-i").append(split);
            cmdBuilder.append(inputVideoFilePath).append(split);

            for (Watermark watermark : watermarks) {
                if (watermark.isGif()) {
                    cmdBuilder.append("-ignore_loop").append(split);
                    cmdBuilder.append("0").append(split);
                }

                cmdBuilder.append("-i").append(split);
                cmdBuilder.append(watermark.getImagePath()).append(split);
            }

            cmdBuilder.append("-filter_complex").append(split);

            Watermark watermarkItem;
            int size = watermarks.size();
            String inputAndOutput = "[0:v]";
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

            cmdBuilder.append(paramsBuilder).append(split);
            cmdBuilder.append("-c:v").append(split);
            cmdBuilder.append("libx264").append(split);
            cmdBuilder.append("-preset").append(split);
            cmdBuilder.append("ultrafast").append(split);
            cmdBuilder.append("-crf").append(split);
            cmdBuilder.append("18").append(split);
//            cmdBuilder.append("[0:a] -af volume=400").append(split);
            cmdBuilder.append("-c:a").append(split);
            cmdBuilder.append("copy").append(split);

            cmdBuilder.append(outputVideoPath);

            return new WatermarkCommand(cmdBuilder.toString());
        }
    }
}
