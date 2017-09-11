package com.lab.ffmpegstu;

import android.content.Context;
import android.util.Log;

/**
 * Created by kuaigeng01 on 2017/6/27.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public CrashHandler() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /**
     * 使用初始化方法初始化，防止提前初始化或者重复初始化
     *
     * @param cxt
     */
    public void initCrashHandler(Context cxt) {
//        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
//        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = cxt;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e("remote", "uncaughtException " + e.getMessage());

    }
}
