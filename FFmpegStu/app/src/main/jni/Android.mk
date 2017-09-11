LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := ffmpeg_ugc
LOCAL_SRC_FILES := libffmpeg_ugc.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := ffmpeginvoke
LOCAL_SRC_FILES := ffmpeginvoke.c ffmpeg.c ffmpeg_opt.c cmdutils.c ffmpeg_filter.c ffmpeg_hw.c
# 这里的地址改成自己的 FFmpeg 源码目录
LOCAL_C_INCLUDES := /Users/guozhigang/Documents/ffmpegStudy/source/code002/ffmpeg
LOCAL_LDLIBS := -llog -lz -ldl -latomic
LOCAL_SHARED_LIBRARIES := ffmpeg_ugc
include $(BUILD_SHARED_LIBRARY)
APP_UNIFIED_HEADERS := true