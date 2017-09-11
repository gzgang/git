package com.lab.ugcmodule.media;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lab.ugcmodule.media.ffmpeg.cmd.Overlay;
import com.lab.ugcmodule.media.ffmpeg.cmd.Watermark;
import com.lab.ugcmodule.media.service.MediaOperatorTaskBuilder;

import java.util.List;

/**
 * Created by kuaigeng01 on 2017/6/14.
 */

interface IMediaOperatorFace {


    /**
     * 初始化服务
     */
    void initRemoteService();

    /**
     * 取消所有任务
     */
    void cancelAllTask();

    /**
     * 执行ffmpeg 命令
     *
     * @param builder 参数
     * @return 返回执行结果
     */
    void commandMultipleTask(MediaOperatorTaskBuilder builder, @Nullable MediaOperatorListener listener);

    /**
     * 拼接视频
     * 【很快】
     *
     * @param inputVideoFilePathList 待拼接待视频列表
     * @param outputVideoPath        调整后的视频路径
     * @param listener               回调监听
     */
    void concatVideo(@NonNull List<String> inputVideoFilePathList, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener);

    /**
     * 裁剪视频
     * 【很快】
     *
     * @param inputVideoFilePath 待调整待视频路径
     * @param startTime          起始时间
     * @param duration           截取的时长
     * @param outputVideoPath    调整后的视频路径
     * @param listener           回调监听
     */
    void trimVideo(@NonNull String inputVideoFilePath, int startTime, int duration, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener);

    /**
     * 关闭视频声音
     * 【很快】
     *
     * @param inputVideoFilePath 待调整待视频路径
     * @param outputVideoPath    调整后的视频路径
     * @param listener           回调监听
     */
    void closeVolume(@NonNull String inputVideoFilePath, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener);

    /**
     * 调节声音大小
     * 【很慢】
     *
     * @param inputVideoFilePath 待调整待视频路径
     * @param volumePercent      调节的百分百 [0.1 ~ 10]
     * @param outputVideoPath    调整后的视频路径
     * @param listener           回调监听
     */
    void adjustVolume(@NonNull String inputVideoFilePath, float volumePercent, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener);

    /**
     * 视频快放／慢放
     * 【很慢】
     *
     * @param inputVideoFilePath 待调整待视频路径
     * @param rate               调节的倍率 [0.5 ~ 2]
     * @param outputVideoPath    调整后的视频路径
     * @param listener           回调监听
     */
    void fastOrSlowVideo(@NonNull String inputVideoFilePath, float rate, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener);


    /**
     * 添加涂鸦／贴纸／字幕
     * 【比较慢】
     *
     * @param inputVideoFilePath 待调整待视频路径
     * @param watermarks         涂鸦／贴纸／字幕 文件png
     * @param outputVideoPath    调整后的视频路径
     * @param listener           回调监听
     */
    void watermark(@NonNull String inputVideoFilePath, @NonNull List<Watermark> watermarks, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener);

    /**
     * 视频叠加
     * 【很慢】
     *
     * @param inputVideoFilePath 待调整待视频路径
     * @param overlays           待叠加的效果视频
     * @param outputVideoPath    调整后的视频路径
     * @param listener           回调监听
     */
    void overlay(@NonNull String inputVideoFilePath, @NonNull List<Overlay> overlays, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener);


    /**
     * 给视频添加背景音乐
     * 【很慢】
     *
     * @param inputVideoPath           待调整待视频路径
     * @param inputBackgroundMusicPath 待添加的背景音乐路径
     * @param outputVideoPath          调整后的视频路径
     * @param listener                 回调监听
     */
    void addBackgroundMusic(@NonNull String inputVideoPath, @NonNull String inputBackgroundMusicPath, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener);

    /**
     * 给视频添加滤镜
     * 【非常慢】
     *
     * @param inputVideoPath  待调整待视频路径
     * @param filterConfig    滤镜配置
     * @param outputVideoPath 调整后的视频路径
     * @param listener        回调监听
     */
    void addFilter(@NonNull String inputVideoPath, @NonNull String filterConfig, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener);

    /**
     * 压缩视频
     * 【很慢】
     *
     * @param inputVideoPath  待调整待视频路径
     * @param quality         品质[0 - 51] 默认 23
     * @param outputVideoPath 调整后的视频路径
     * @param listener        回调监听
     */
    void compress(@NonNull String inputVideoPath, int quality, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener);

    void scaleAndCompress(@NonNull String inputVideoPath, int width, int height, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener);

}
