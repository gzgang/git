package com.lab.ffmpegstu;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.kg.v1.global.Global;
import com.lab.ugcmodule.media.ffmpeg.FFmpegNative;

import java.util.Iterator;
import java.util.List;

/**
 * Created by kuaigeng01 on 2017/6/27.
 */

public class FFmpegApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("remote", "application onCreate " + getCurrentProcessName(this) + ": isAvailable = " + FFmpegNative.isAvailable());
        Global.setGlobalContext(this);

//        CrashHandler handler = new CrashHandler();
////        handler.initCrashHandler(this);
//        Thread.setDefaultUncaughtExceptionHandler(handler);
//
//        FFmpegNative.isAvailable();
    }


    static String sCurrentProcessName;

    public static String getCurrentProcessName(Context context) {
        if (context == null) {
            return sCurrentProcessName;
        } else {
            try {
                if (sCurrentProcessName == null) {
                    ActivityManager e = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                    List infos = e.getRunningAppProcesses();
                    Iterator i$ = infos.iterator();

                    ActivityManager.RunningAppProcessInfo info;
                    do {
                        if (!i$.hasNext()) {
                            return null;
                        }

                        info = (ActivityManager.RunningAppProcessInfo) i$.next();
                    } while (info.pid != android.os.Process.myPid());

                    sCurrentProcessName = info.processName;
                    return sCurrentProcessName;
                }
            } catch (Exception var5) {
                ;
            }

            return sCurrentProcessName;
        }
    }
}
