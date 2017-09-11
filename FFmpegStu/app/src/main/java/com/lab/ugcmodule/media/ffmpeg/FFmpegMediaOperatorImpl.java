package com.lab.ugcmodule.media.ffmpeg;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.kg.v1.global.Global;
import com.kg.v1.tools.DebugLog;
import com.lab.ugcmodule.media.IMediaOperatorImplFace;
import com.lab.ugcmodule.media.OperatorResult;
import com.lab.ugcmodule.media.ffmpeg.cmd.AddBackgroundMusicCommand;
import com.lab.ugcmodule.media.ffmpeg.cmd.AdjustVolumeCommand;
import com.lab.ugcmodule.media.ffmpeg.cmd.CombineAudioCommand;
import com.lab.ugcmodule.media.ffmpeg.cmd.CombineVideoWithAudioCommand;
import com.lab.ugcmodule.media.ffmpeg.cmd.Command;
import com.lab.ugcmodule.media.ffmpeg.cmd.ComplexCommand;
import com.lab.ugcmodule.media.ffmpeg.cmd.CompressVideoCommand;
import com.lab.ugcmodule.media.ffmpeg.cmd.ConcatVideoCommand;
import com.lab.ugcmodule.media.ffmpeg.cmd.ExtractAudioCommand;
import com.lab.ugcmodule.media.ffmpeg.cmd.ExtractVideoCommand;
import com.lab.ugcmodule.media.ffmpeg.cmd.FastOrSlowVideoCommand;
import com.lab.ugcmodule.media.ffmpeg.cmd.OverlayCommand;
import com.lab.ugcmodule.media.ffmpeg.cmd.Overlay;
import com.lab.ugcmodule.media.ffmpeg.cmd.ScaleAndCompressCommand;
import com.lab.ugcmodule.media.ffmpeg.cmd.TrimVideoCommand;
import com.lab.ugcmodule.media.ffmpeg.cmd.Watermark;
import com.lab.ugcmodule.media.ffmpeg.cmd.WatermarkCommand;
import com.lab.ugcmodule.media.service.MediaOperatorParams;

import org.wysaid.nativePort.CGEFFmpegNativeLibrary;
import org.wysaid.nativePort.CGENativeLibrary;

import java.util.ArrayList;
import java.util.List;

/**
 * ffmpeg 实现视频编辑功能
 * Created by kuaigeng01 on 2017/6/14.
 */
public class FFmpegMediaOperatorImpl implements IMediaOperatorImplFace {
    private static final String TAG = "FFmpegMediaOperatorImpl";

    @Override
    public OperatorResult command(@NonNull MediaOperatorParams params) {
        OperatorResult result = null;

        switch (params.getCmdTypeDef()) {
            case MediaOperatorParams.CMD_CONCAT:

                result = concatVideo(params.getInputVideoFilePathList(), params.getOutputMediaFilePath());
                break;
            case MediaOperatorParams.CMD_TRIM:

                result = trimVideo(params.getInputMediaFilePath(), params.getIntArg1(), params.getIntArg2(), params.getOutputMediaFilePath());
                break;
            case MediaOperatorParams.CMD_BACKGROUND_MUSIC:

                result = addBackgroundMusic(params.getInputMediaFilePath(), params.getInputBackgroundMusicPath(), params.getOutputMediaFilePath());
                break;
            case MediaOperatorParams.CMD_WATERMARK:

                result = watermark(params.getInputMediaFilePath(), params.getWatermarks(), params.getOutputMediaFilePath());
                break;
            case MediaOperatorParams.CMD_EXTRACT_VIDEO:

                result = extractVideo(params.getInputMediaFilePath(), params.getOutputMediaFilePath());
                break;
            case MediaOperatorParams.CMD_ADJUST_VOLUME:

                result = adjustVolume(params.getInputMediaFilePath(), params.getFloatArg1(), params.getOutputMediaFilePath());
                break;
            case MediaOperatorParams.CMD_FAST_SLOW_VIDEO:

                result = fastOrSlowVideo(params.getInputMediaFilePath(), params.getFloatArg1(), params.getOutputMediaFilePath());
                break;
            case MediaOperatorParams.CMD_ADD_FILTER:

                result = addFilter(params.getInputMediaFilePath(), params.getStringArg1(), params.getOutputMediaFilePath());
                break;
            case MediaOperatorParams.CMD_COMPRESS:

                result = compress(params.getInputMediaFilePath(), params.getIntArg1(), params.getOutputMediaFilePath());
                break;
            case MediaOperatorParams.CMD_OVERLAY:

                result = overlay(params.getInputMediaFilePath(), params.getVideoOverlays(), params.getOutputMediaFilePath());
                break;
            case MediaOperatorParams.CMD_PRIVATE_COMPLEX:

                result = complex(params.getInputMediaFilePath(), params.getMultiplesOperator(), params.getOutputMediaFilePath());
                break;
            case MediaOperatorParams.CMD_SCALE_COMPRESS:

                result = scaleAndCompress(params.getInputMediaFilePath(), params.getIntArg1(), params.getIntArg2(), params.getOutputMediaFilePath());
                break;
            default:

                throw new IllegalArgumentException("not implements cmd: " + params.getCmdTypeDef());
        }

        return result == null ? new OperatorResult(false, params.getOutputMediaFilePath()) : result;
    }

    @Override
    public OperatorResult concatVideo(@NonNull List<String> inputVideoFilePathList, @NonNull String outputVideoPath) {
        String concatFile = OperatorUtils.createFileForConcat(Global.getGlobalContext(), inputVideoFilePathList);

        Command concatCommand = new ConcatVideoCommand.Builder().setConcatVideoListFilePath(concatFile)
                .setOutputVideoPath(outputVideoPath)
                .build();

        String cmd = concatCommand.getCommand();
        boolean nativeExecuteResult = FFmpegNative.execute(cmd);

        return new OperatorResult(nativeExecuteResult, outputVideoPath);
    }

    @Override
    public OperatorResult trimVideo(@NonNull String inputVideoFilePath, int startTime, int duration, @NonNull String outputVideoPath) {

        Command clipCommand = new TrimVideoCommand.Builder().setDuration(duration)
                .setStartTime(startTime)
                .setInputFile(inputVideoFilePath)
                .setOutputFile(outputVideoPath)
                .build();


        String cmd = clipCommand.getCommand();
        boolean nativeExecuteResult = FFmpegNative.execute(cmd);

        return new OperatorResult(nativeExecuteResult, outputVideoPath);
    }

    @Override
    public OperatorResult extractAudio(@NonNull String inputVideoFilePath, @NonNull String outputAudioPath) {
        Command command = new ExtractAudioCommand.Builder()
                .setInputVideoFilePath(inputVideoFilePath)
                .setOutputAudioPath(outputAudioPath)
                .build();


        String cmd = command.getCommand();
        boolean nativeExecuteResult = FFmpegNative.execute(cmd);

        return new OperatorResult(nativeExecuteResult, outputAudioPath);
    }

    @Override
    public OperatorResult extractVideo(@NonNull String inputVideoFilePath, @NonNull String outputVideoPath) {
        Command command = new ExtractVideoCommand.Builder()
                .setInputVideoFilePath(inputVideoFilePath)
                .setOutputVideoPath(outputVideoPath)
                .build();


        String cmd = command.getCommand();
        boolean nativeExecuteResult = FFmpegNative.execute(cmd);

        return new OperatorResult(nativeExecuteResult, outputVideoPath);
    }

    @Override
    public OperatorResult adjustVolume(@NonNull String inputVideoFilePath, float volumePercent, @NonNull String outputVideoPath) {
        Command command = new AdjustVolumeCommand.Builder()
                .setInputVideoFilePath(inputVideoFilePath)
                .setOutputVideoPath(outputVideoPath)
                .setVolumePercent(volumePercent)
                .build();


        String cmd = command.getCommand();
        boolean nativeExecuteResult = FFmpegNative.execute(cmd);

        return new OperatorResult(nativeExecuteResult, outputVideoPath);
    }

    @Override
    public OperatorResult fastOrSlowVideo(@NonNull String inputVideoFilePath, float rate, @NonNull String outputVideoPath) {
        Command command = new FastOrSlowVideoCommand.Builder()
                .setInputVideoFilePath(inputVideoFilePath)
                .setOutputVideoPath(outputVideoPath)
                .setMultiple(rate)
                .build();


        String cmd = command.getCommand();
        boolean nativeExecuteResult = FFmpegNative.execute(cmd);

        return new OperatorResult(nativeExecuteResult, outputVideoPath);
    }

    @Override
    public OperatorResult watermark(@NonNull String inputVideoFilePath, @NonNull List<Watermark> watermarks, @NonNull String outputVideoPath) {
        Command command = new WatermarkCommand.Builder()
                .setInputVideoFilePath(inputVideoFilePath)
                .setWatermarks(watermarks)
                .setOutputVideoPath(outputVideoPath)
                .build();


        String cmd = command.getCommand();
        boolean nativeExecuteResult = FFmpegNative.execute(cmd);

        return new OperatorResult(nativeExecuteResult, outputVideoPath);
    }

    @Override
    public OperatorResult overlay(@NonNull String inputVideoFilePath, @NonNull List<Overlay> overlays, @NonNull String outputVideoPath) {
        Command command = new OverlayCommand.Builder()
                .setInputVideoFilePath(inputVideoFilePath)
                .setOverlayList(overlays)
                .setOutputVideoPath(outputVideoPath)
                .build();


        String cmd = command.getCommand();
        boolean nativeExecuteResult = FFmpegNative.execute(cmd);

        return new OperatorResult(nativeExecuteResult, outputVideoPath);
    }

    @Override
    public OperatorResult combineAudioWithBackgroundMusic(@NonNull String inputAudioFilePath, @NonNull String inputBackgroundMusicPath, @NonNull String outputAudioPath) {
        Command command = new CombineAudioCommand.Builder()
                .setInputAudioFilePath(inputAudioFilePath)
                .setInputBackgroundMusicFilePath(inputBackgroundMusicPath)
                .setOutputAudioPath(outputAudioPath)
                .build();


        String cmd = command.getCommand();
        boolean nativeExecuteResult = FFmpegNative.execute(cmd);

        return new OperatorResult(nativeExecuteResult, outputAudioPath);
    }

    @Override
    public OperatorResult combineVideoWithAudio(@NonNull String inputVideoFilePath, @NonNull String inputAudioFilePath, @NonNull String outputVideoPath) {
        Command command = new CombineVideoWithAudioCommand.Builder()
                .setInputVideoFilePath(inputVideoFilePath)
                .setInputAudioFilePath(inputAudioFilePath)
                .setOutputVideoPath(outputVideoPath)
                .build();


        String cmd = command.getCommand();
        boolean nativeExecuteResult = FFmpegNative.execute(cmd);

        return new OperatorResult(nativeExecuteResult, outputVideoPath);
    }

    @Override
    public OperatorResult addBackgroundMusic(@NonNull String inputVideoPath, @NonNull String inputBackgroundMusicPath, @NonNull String outputVideoPath) {
        boolean allowNetStep = true;

        long videoDuration = OperatorUtils.getMediaFileDuration(inputVideoPath);
        long backgroundMusicDuration = OperatorUtils.getMediaFileDuration(inputBackgroundMusicPath);

        //step 1: 生成一个足够大的循环背景音乐
        int repeatCount = 1;
        if (videoDuration > 0 && backgroundMusicDuration > 0) {
            repeatCount = (int) Math.ceil((videoDuration * 1f / backgroundMusicDuration));
            repeatCount = Math.max(repeatCount, 1);
        }

        if (DebugLog.isDebug()) {
            Log.d(TAG, "step 0 : videoDuration = " + videoDuration + "; backgroundMusicDuration = " + backgroundMusicDuration + "; repeatCount = " + repeatCount);
        }

        String tmpFileForBackgroundMusic = null;
        if (repeatCount > 1) {
            List<String> backgroundMusic = new ArrayList<>();
            for (int i = 0; i < repeatCount; i++) {
                backgroundMusic.add(inputBackgroundMusicPath);
            }

            tmpFileForBackgroundMusic = OperatorUtils.getFfmpegCacheTmpDir(Global.getGlobalContext()) + "/tmp_loop_bg_music.aac";
            OperatorResult firstStepResult = concatVideo(backgroundMusic, tmpFileForBackgroundMusic);

            if (!firstStepResult.isSuccess()) {
                //error
                allowNetStep = false;
            }

            if (DebugLog.isDebug()) {
                Log.d(TAG, "step 1 :" + firstStepResult);
            }
        }

        if (TextUtils.isEmpty(tmpFileForBackgroundMusic)) {
            tmpFileForBackgroundMusic = inputBackgroundMusicPath;
        }


        /*
        //step 2: 剥离掉原来视频的视频获的纯声音文件
        String tmpFilePureAudioFile = null;
        if (allowNetStep) {
            tmpFilePureAudioFile = OperatorUtils.getFfmpegCacheTmpDir(Global.getGlobalContext()) + "/tmp_pure_audio.aac";
            OperatorResult extractAudioResult = extractAudio(inputVideoPath, tmpFilePureAudioFile);
            if (!extractAudioResult.isSuccess()) {
                //error
                allowNetStep = false;
            }

            if (DebugLog.isDebug()) {
                Log.d(TAG, "step 2 :" + extractAudioResult);
            }
        }

        //step 3: 剥离掉原来视频的声音获的纯视频文件
        String tmpFilePureVideoFile = null;
        if (allowNetStep) {
            tmpFilePureVideoFile = OperatorUtils.getFfmpegCacheTmpDir(Global.getGlobalContext()) + "/tmp_pure_video.mp4";
            OperatorResult extractVideoResult = extractVideo(inputVideoPath, tmpFilePureVideoFile);
            if (!extractVideoResult.isSuccess()) {
                //error
                allowNetStep = false;
            }

            if (DebugLog.isDebug()) {
                Log.d(TAG, "step 3 :" + extractVideoResult);
            }
        }

        //step 4: 合并背景音乐和原视频声音
        String tmpFileCombineAudioFile = null;
        if (allowNetStep) {
            tmpFileCombineAudioFile = OperatorUtils.getFfmpegCacheTmpDir(Global.getGlobalContext()) + "/tmp_combine_audio.aac";
            OperatorResult combineAudioFileResult = combineAudioWithBackgroundMusic(tmpFilePureAudioFile, tmpFileForBackgroundMusic, tmpFileCombineAudioFile);
            if (!combineAudioFileResult.isSuccess()) {
                //error
                allowNetStep = false;
            }

            if (DebugLog.isDebug()) {
                Log.d(TAG, "step 4 :" + combineAudioFileResult);
            }
        }

        //step 5: 合并声音和纯视频
        if (allowNetStep) {
            OperatorResult combineVideoWithAudioFileResult = combineVideoWithAudio(tmpFilePureVideoFile, tmpFileCombineAudioFile, outputVideoPath);
            if (!combineVideoWithAudioFileResult.isSuccess()) {
                //error
                allowNetStep = false;
            }

            if (DebugLog.isDebug()) {
                Log.d(TAG, "step 5 :" + combineVideoWithAudioFileResult);
            }
        }

        */

        Command command = new AddBackgroundMusicCommand.Builder()
                .setInputVideoFilePath(inputVideoPath)
                .setInputBackgroundMusicFilePath(tmpFileForBackgroundMusic)
                .setOutputVideoPath(outputVideoPath)
                .setOriginVideoVolume(1f)
                .setBackgroundMusicVolume(0.8f)
                .build();


        String cmd = command.getCommand();
        allowNetStep = FFmpegNative.execute(cmd);

        //step 6: 清除中间文件
        OperatorUtils.cleanFfmpegCacheTmpDir(Global.getGlobalContext());

        return new OperatorResult(allowNetStep, outputVideoPath);
    }

    @Override
    public OperatorResult addFilter(@NonNull String inputVideoPath, @NonNull String filterConfig, @NonNull String outputVideoPath) {

        boolean result = CGEFFmpegNativeLibrary.generateVideoWithFilter(outputVideoPath, inputVideoPath, filterConfig, 1.0f, null, CGENativeLibrary.TextureBlendMode.CGE_BLEND_ADDREV, 1.0f, false);

        return new OperatorResult(result, outputVideoPath);
    }

    @Override
    public OperatorResult compress(@NonNull String inputVideoPath, int quality, @NonNull String outputVideoPath) {
        Command command = new CompressVideoCommand.Builder()
                .setInputVideoFilePath(inputVideoPath)
                .setQuality(quality)
                .setOutputVideoPath(outputVideoPath)
                .build();


        String cmd = command.getCommand();
        boolean nativeExecuteResult = FFmpegNative.execute(cmd);

        return new OperatorResult(nativeExecuteResult, outputVideoPath);
    }


    private OperatorResult complex(@NonNull String inputVideoPath, @NonNull List<MediaOperatorParams> operatorParams, @NonNull String outputVideoPath) {

        Command command = new ComplexCommand.Builder()
                .setInputVideoFilePath(inputVideoPath)
                .setOperatorParamsList(operatorParams)
                .setOutputVideoPath(outputVideoPath)
                .build();


        String cmd = command.getCommand();
        boolean nativeExecuteResult = FFmpegNative.execute(cmd);

        return new OperatorResult(nativeExecuteResult, outputVideoPath);

    }

    @Override
    public OperatorResult scaleAndCompress(@NonNull String inputVideoPath, int width, int height, @NonNull String outputVideoPath) {
        Command command = new ScaleAndCompressCommand.Builder()
                .setInputVideoFilePath(inputVideoPath)
                .setWidth(width)
                .setHeight(height)
                .setOutputVideoPath(outputVideoPath)
                .build();


        String cmd = command.getCommand();
        boolean nativeExecuteResult = FFmpegNative.execute(cmd);

        return new OperatorResult(nativeExecuteResult, outputVideoPath);
    }
}
