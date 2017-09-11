package com.lab.ugcmodule.media;

import android.support.annotation.NonNull;

import com.lab.ugcmodule.media.ffmpeg.cmd.Overlay;
import com.lab.ugcmodule.media.ffmpeg.cmd.Watermark;
import com.lab.ugcmodule.media.service.MediaOperatorParams;

import java.util.List;

/**
 * Created by kuaigeng01 on 2017/6/14.
 */

public interface IMediaOperatorImplFace {

    /**
     * 执行ffmpeg 命令
     *
     * @param params 参数
     * @return 返回执行结果
     */
    OperatorResult command(@NonNull MediaOperatorParams params);

    /**
     * 拼接视频
     * 【很快】
     *
     * @param inputVideoFilePathList 待拼接待视频列表
     * @param outputVideoPath        调整后的视频路径
     */
    OperatorResult concatVideo(@NonNull List<String> inputVideoFilePathList, @NonNull String outputVideoPath);

    /**
     * 裁剪视频
     * 【很快】
     *
     * @param inputVideoFilePath 待调整待视频路径
     * @param startTime          起始时间
     * @param duration           截取的时长
     * @param outputVideoPath    调整后的视频路径
     */
    OperatorResult trimVideo(@NonNull String inputVideoFilePath, int startTime, int duration, @NonNull String outputVideoPath);

    /**
     * 从视频中分离出视频:删除音频
     * 【很快】
     *
     * @param inputVideoFilePath 待调整待视频路径
     * @param outputVideoPath    调整后的视频路径
     */
    OperatorResult extractVideo(@NonNull String inputVideoFilePath, @NonNull String outputVideoPath);

    /**
     * 从视频中分离出音频:删除视频
     * 【很快】
     *
     * @param inputVideoFilePath 待调整待视频路径
     * @param outputAudioPath    调整后的音频路径
     */
    OperatorResult extractAudio(@NonNull String inputVideoFilePath, @NonNull String outputAudioPath);

    /**
     * 两个音频合并
     * 【很慢】
     *
     * @param inputAudioFilePath       待调整待音频文件
     * @param inputBackgroundMusicPath 音频背景音乐
     * @param outputAudioPath          调整后的音频路径
     */
    OperatorResult combineAudioWithBackgroundMusic(@NonNull String inputAudioFilePath, @NonNull String inputBackgroundMusicPath, @NonNull String outputAudioPath);

    /**
     * 合并音视频
     * 【很慢】
     *
     * @param inputVideoFilePath 待调整的视频文件路径，要求这个视频没有声音才行
     * @param inputAudioFilePath 待添加待音频文件路径
     * @param outputAudioPath    调整后的视频路径
     */
    OperatorResult combineVideoWithAudio(@NonNull String inputVideoFilePath, String inputAudioFilePath, @NonNull String outputAudioPath);

    /**
     * 调节声音大小
     * 【很慢】
     *
     * @param inputVideoFilePath 待调整待视频路径
     * @param volumePercent      调节的百分百 [0.1 ~ 10]
     * @param outputVideoPath    调整后的视频路径
     */
    OperatorResult adjustVolume(@NonNull String inputVideoFilePath, float volumePercent, @NonNull String outputVideoPath);

    /**
     * 视频快放／慢放
     * 【很慢】
     *
     * @param inputVideoFilePath 待调整待视频路径
     * @param rate               调节的倍率 [0.5 ~ 2]
     * @param outputVideoPath    调整后的视频路径
     */
    OperatorResult fastOrSlowVideo(@NonNull String inputVideoFilePath, float rate, @NonNull String outputVideoPath);


    /**
     * 添加涂鸦／贴纸／字幕
     * 【很慢】
     *
     * @param inputVideoFilePath 待调整待视频路径
     * @param watermarks         涂鸦／贴纸／字幕 文件png
     * @param outputVideoPath    调整后的视频路径
     */
    OperatorResult watermark(@NonNull String inputVideoFilePath, @NonNull List<Watermark> watermarks, @NonNull String outputVideoPath);

    /**
     * 视频叠加
     * 【很慢】
     *
     * @param inputVideoFilePath 待调整待视频路径
     * @param overlays           待叠加的效果视频
     * @param outputVideoPath    调整后的视频路径
     */
    OperatorResult overlay(@NonNull String inputVideoFilePath, @NonNull List<Overlay> overlays, @NonNull String outputVideoPath);

    /**
     * 给视频添加背景音乐
     * 【很慢】
     *
     * @param inputVideoPath           待调整待视频路径
     * @param inputBackgroundMusicPath 待添加待背景音乐路径
     * @param outputVideoPath          调整后的视频路径
     */
    OperatorResult addBackgroundMusic(@NonNull String inputVideoPath, @NonNull String inputBackgroundMusicPath, @NonNull String outputVideoPath);

    /**
     * 给视频添加滤镜
     * 【非常慢】
     *
     * @param inputVideoPath  待调整待视频路径
     * @param filterConfig    滤镜配置
     * @param outputVideoPath 调整后的视频路径
     */
    OperatorResult addFilter(@NonNull String inputVideoPath, @NonNull String filterConfig, @NonNull String outputVideoPath);

    /**
     * 压缩视频
     * 【很慢】
     *
     * @param inputVideoPath  待调整待视频路径
     * @param quality         品质[0 - 51] 默认 23
     * @param outputVideoPath 调整后的视频路径
     */
    OperatorResult compress(@NonNull String inputVideoPath, int quality, @NonNull String outputVideoPath);

    OperatorResult scaleAndCompress(@NonNull String inputVideoPath, int width, int height, @NonNull String outputVideoPath);
}

