package com.kg.v1.global;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * 全局
 * Created by gzg on 2016/3/29.
 */
public class Global {

    public static String PACKAGE_NAME = "com.perfect.video";
    public static String CHANNEL_ID = "";
    public static String API_KEY = "ANDROID";

    private static Context mGlobalContext;
    public static boolean IS_LIBRARY_LOAD_OK;


    public static Context getGlobalContext() {
        return mGlobalContext;
    }

    public static void setGlobalContext(Context mGlobalContext) {
        Global.mGlobalContext = mGlobalContext;
    }

    public static boolean isInit() {
        return mGlobalContext != null;
    }

    public static Handler getUiHandler() {
        return UiHandlerHolder.INSTANCE;
    }

    static class UiHandlerHolder {
        static final Handler INSTANCE = new Handler(Looper.getMainLooper());
    }


    public static String getKey() {
        return API_KEY;
    }


}
