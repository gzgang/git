package com.lab.ugcmodule.media.service;

import android.support.annotation.NonNull;

import com.lab.ugcmodule.media.ffmpeg.cmd.Watermark;

import java.util.List;

/**
 * Created by kuaigeng01 on 2017/6/14.
 */

public interface IMediaOperatorBuilderFace {

    /**
     * 准备
     *
     * @param inputVideoFilePath  待调整待视频路径
     * @param outputVideoFilePath 调整后的视频路径
     */
    IMediaOperatorBuilderFace prepare(@NonNull String inputVideoFilePath, @NonNull String outputVideoFilePath);

    /**
     * 构建
     */
    MediaOperatorTaskBuilder build();

    /**
     * 从视频中分离出视频:删除音频
     * 【很快】
     */
    IMediaOperatorBuilderFace extractVideo();

    /**
     * 调节声音大小
     * 【很慢】
     *
     * @param volumePercent 调节的百分百 [0.1 ~ 10]
     */
    IMediaOperatorBuilderFace adjustVolume(float volumePercent);


    /**
     * 添加涂鸦／贴纸／字幕
     * 【很慢】
     *
     * @param watermarks 涂鸦／贴纸／字幕 文件png
     */
    IMediaOperatorBuilderFace watermark(@NonNull List<Watermark> watermarks);


    /**
     * 给视频添加滤镜
     * 【很慢】
     *
     * @param filterConfig 滤镜配置
     */
    IMediaOperatorBuilderFace addFilter(@NonNull String filterConfig);

    /**
     * 压缩视频
     * 【比较慢】
     *
     * @param quality 品质[0 ~ 51] default is 23
     */
    IMediaOperatorBuilderFace compress(int quality);
}
