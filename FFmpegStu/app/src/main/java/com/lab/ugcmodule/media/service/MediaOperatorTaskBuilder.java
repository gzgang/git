package com.lab.ugcmodule.media.service;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lab.ugcmodule.media.ffmpeg.Mini;
import com.lab.ugcmodule.media.ffmpeg.cmd.Watermark;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuaigeng01 on 2017/6/30.
 */

public class MediaOperatorTaskBuilder {

    private List<MediaOperatorParams> mTaskList;

    private MediaOperatorTaskBuilder(List<MediaOperatorParams> taskList) {
        this.mTaskList = taskList;
    }


    public List<MediaOperatorParams> getTaskList() {
        return mTaskList;
    }

    public static class TaskBuilder implements IMediaOperatorBuilderFace {
        private String inputVideoFilePath;
        private String outputVideoFilePath;

        private List<MediaOperatorParams> mTaskList = new ArrayList<>();

        private void checkParamsValid() {
            if (TextUtils.isEmpty(inputVideoFilePath) || TextUtils.isEmpty(outputVideoFilePath)) {
                throw new IllegalArgumentException("please call prepare first");
            }
        }


        @Override
        public IMediaOperatorBuilderFace prepare(@NonNull String inputVideoFilePath, @NonNull String outputVideoFilePath) {
            mTaskList.clear();

            this.inputVideoFilePath = inputVideoFilePath;
            this.outputVideoFilePath = outputVideoFilePath;

            return this;
        }

        @Override
        public MediaOperatorTaskBuilder build() {
            mTaskList = Mini.mini(mTaskList);
            int totalTask = mTaskList.size();

            if (totalTask == 0) {
                throw new IllegalStateException("please build task first");
            }

            MediaOperatorParams item, previous = null;

            for (int i = 0; i < totalTask; i++) {
                item = mTaskList.get(i);

                if (i == 0) {
                    item.setInputMediaFilePath(inputVideoFilePath);
                }

                if (i == totalTask - 1) {
                    item.setOutputMediaFilePath(outputVideoFilePath);
                }

                if (TextUtils.isEmpty(item.getInputMediaFilePath()) && null != previous) {
                    //当前的输入等于上一个的输出
                    item.setInputMediaFilePath(TextUtils.isEmpty(previous.getOutputMediaFilePath())
                            ? previous.generateDefaultOutputFilePath()
                            : previous.getOutputMediaFilePath());
                }

                if (TextUtils.isEmpty(item.getOutputMediaFilePath())) {
                    //没有指定输出的时候，自动产生默认输出路径
                    item.setOutputMediaFilePath(item.generateDefaultOutputFilePath());
                }

                previous = item;
            }


            return new MediaOperatorTaskBuilder(mTaskList);
        }

        @Override
        public IMediaOperatorBuilderFace extractVideo() {
            checkParamsValid();

            MediaOperatorParams params = new MediaOperatorParams.Builder()
                    .build(MediaOperatorParams.CMD_EXTRACT_VIDEO);

            mTaskList.add(params);

            return this;
        }

        @Override
        public IMediaOperatorBuilderFace adjustVolume(float volumePercent) {
            checkParamsValid();

            MediaOperatorParams params = new MediaOperatorParams.Builder()
                    .setFloatArg1(volumePercent)
                    .build(MediaOperatorParams.CMD_ADJUST_VOLUME);

            mTaskList.add(params);

            return this;
        }

        @Override
        public IMediaOperatorBuilderFace watermark(@NonNull List<Watermark> watermarks) {
            checkParamsValid();

            MediaOperatorParams params = new MediaOperatorParams.Builder()
                    .setWatermarks(watermarks)
                    .build(MediaOperatorParams.CMD_WATERMARK);

            mTaskList.add(params);

            return this;
        }

        @Override
        public IMediaOperatorBuilderFace addFilter(@NonNull String filterConfig) {
            checkParamsValid();

            MediaOperatorParams params = new MediaOperatorParams.Builder()
                    .setStringArg1(filterConfig)
                    .build(MediaOperatorParams.CMD_ADD_FILTER);

            mTaskList.add(params);

            return this;
        }

        @Override
        public IMediaOperatorBuilderFace compress(int quality) {
            checkParamsValid();

            MediaOperatorParams params = new MediaOperatorParams.Builder()
                    .setIntArg1(quality)
                    .build(MediaOperatorParams.CMD_COMPRESS);

            mTaskList.add(params);

            return this;
        }
    }
}
