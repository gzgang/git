package com.lab.ugcmodule.media.ffmpeg;

import android.util.Log;

import com.lab.ugcmodule.media.service.MediaOperatorService;

import org.wysaid.nativePort.CGEFFmpegNativeLibrary;

/**
 * Created by kuaigeng01 on 2017/6/8.
 */

public class FFmpegNative {
    private static boolean IS_UGC_LIBRARY_LOAD_OK = false;
    public static final String SPLIT = " ";

    static {
        try {
            System.loadLibrary("ffmpeg_ugc");
            System.loadLibrary("CGE");
            System.loadLibrary("CGEExt");
            System.loadLibrary("ffmpeginvoke");

            registerCallbackForFFmpeg();
            CGEFFmpegNativeLibrary.avRegisterAll();

            IS_UGC_LIBRARY_LOAD_OK = true;
        } catch (UnsatisfiedLinkError error) {
            error.printStackTrace();

            IS_UGC_LIBRARY_LOAD_OK = false;
        }

        Log.e("remote","IS_UGC_LIBRARY_LOAD_OK = " + IS_UGC_LIBRARY_LOAD_OK);
    }

    public static boolean isAvailable() {
        return IS_UGC_LIBRARY_LOAD_OK;
    }

    public static native int run(String[] commands);

    public static native void registerCallbackForFFmpeg();

    //call from native
    public static void ffmpegLogCallback(String log) {
        String extracTime = OperatorUtils.extractTimeFromFFmpegLog(log);
        if (null != extracTime) {
            long secondTime = OperatorUtils.convertTimeString2Millisecond(extracTime);

            MediaOperatorService.notifyFFmpegProcess(secondTime);


//            Log.d("FFmpegNative", log);
//            Log.d("FFmpegNative", "secondTime = " + secondTime);
        }
    }


    public static boolean execute(String cmd) {
        Log.d("FFmpegNative", "execute cmd start: " + cmd);
        long start = System.currentTimeMillis();

        boolean result = false;
        String[] command = cmd.split(SPLIT);

        try {
            int run = FFmpegNative.run(command);
            Log.d("FFmpegNative", "execute cmd run: " + run);
            result = run != -1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("FFmpegNative", "execute cmd finish: use time = " + (System.currentTimeMillis() - start) + "ms");

        return result;
    }
}
