package com.kg.v1.tools;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DebugLog {
    public static final String TAG = "innlab";
    public static final String PLAY_TAG = "playerControlLogic";
    private static boolean isDebug = true;

    public static boolean isDebug() {
        return isDebug /*|| EngineerCache.isDebugMode()*/;
    }

//	public static void checkIsOpenDebug() {
//		Log.d(NATIVIE_LOG_TAG, "checkIsOpenDebug > isDebug = " + isDebug());
//
//		if (!isDebug()) {
//			if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//				File path = Environment.getExternalStorageDirectory();
//				if(null != path) {
//					String logFileName = path.getPath() + "/poly.log";
//
//					File debugFile 	= new File(logFileName);
//					isDebug			=  debugFile.exists();
//				}
//				Log.d(NATIVIE_LOG_TAG, "log file exist  = " + isDebug());
//			}
//		}
//	}


    public static void i(String tag, String message) {
        if (isDebug()) {
            StackTraceElement stack[] = Thread.currentThread().getStackTrace();
            message = stack[3].getClassName() + "." + stack[3].getMethodName() + "()<" + stack[3].getLineNumber() + "> : " + message;

            Log.i(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (isDebug()) {
            StackTraceElement stack[] = Thread.currentThread().getStackTrace();
            message = stack[3].getClassName() + "." + stack[3].getMethodName() + "()<" + stack[3].getLineNumber() + "> : " + message;

            Log.v(tag, message);
        }
    }

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
    private static final int CHUNK_SIZE = 4000;

    public static void d(String tag, String message) {
        if (isDebug()) {
//            StackTraceElement stack[] = Thread.currentThread().getStackTrace();
//            message = stack[3].getClassName() + "." + stack[3].getMethodName() + "()<" + stack[3].getLineNumber() + "> : " + message;
//
//            if (message.length() > CHUNK_SIZE) {
//                byte[] bytes = message.getBytes();
//                int length = bytes.length;
//                for (int i = 0; i < length; i += CHUNK_SIZE) {
//                    int count = Math.min(length - i, CHUNK_SIZE);
//                    //create a new String with system's default charset (which is UTF-8 for Android)
//                    Log.d(tag, new String(bytes, i, count));
//                }
//            } else {
                Log.d(tag, message);
//            }
        }
    }

    public static void w(String tag, String message) {
        if (isDebug()) {
//            StackTraceElement stack[] = Thread.currentThread().getStackTrace();
//            message = stack[3].getClassName() + "." + stack[3].getMethodName() + "()<" + stack[3].getLineNumber() + "> : " + message;

            Log.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (isDebug()) {
//            StackTraceElement stack[] = Thread.currentThread().getStackTrace();
//            message = stack[3].getClassName() + "." + stack[3].getMethodName() + "()<" + stack[3].getLineNumber() + "> : " + message;

            Log.e(tag, message);
        }
    }

    public static void i(String tag, String category, String message) {
        if (isDebug()) {
            StackTraceElement stack[] = Thread.currentThread().getStackTrace();
            message = stack[3].getClassName() + "." + stack[3].getMethodName() + "()<" + stack[3].getLineNumber() + "> : " + category + ">> " + message;

            Log.i(tag, message);
        }
    }

    public static void v(String tag, String category, String message) {
        if (isDebug()) {
            StackTraceElement stack[] = Thread.currentThread().getStackTrace();
            message = stack[3].getClassName() + "." + stack[3].getMethodName() + "()<" + stack[3].getLineNumber() + "> : " + category + ">> " + message;

            Log.v(tag, message);
        }
    }

    public static void d(String tag, String category, String message) {
        if (isDebug()) {
            StackTraceElement stack[] = Thread.currentThread().getStackTrace();
            message = stack[3].getClassName() + "." + stack[3].getMethodName() + "()<" + stack[3].getLineNumber() + "> : " + category + ">> " + message;

            Log.d(tag, message);
        }
    }

    public static void w(String tag, String category, String message) {
        if (isDebug()) {
            StackTraceElement stack[] = Thread.currentThread().getStackTrace();
            message = stack[3].getClassName() + "." + stack[3].getMethodName() + "()<" + stack[3].getLineNumber() + "> : " + category + ">> " + message;

            Log.w(tag, message);
        }
    }

    public static void e(String tag, String category, String message) {
        if (isDebug()) {
            StackTraceElement stack[] = Thread.currentThread().getStackTrace();
            message = stack[3].getClassName() + "." + stack[3].getMethodName() + "()<" + stack[3].getLineNumber() + "> : " + category + ">> " + message;

            Log.e(tag, message);
        }
    }

    /**
     * set the value of isDebug. default is false;
     *
     * @param b
     */
    public static void setIsDebug(boolean b) {
        isDebug = b;
        // 新浪微博开启调试信息
//		LogUtil.enableLog();
    }

    private static final int JSON_INDENT = 2;

    public static void json(String tag, String json) {
        if (TextUtils.isEmpty(json)) {
            return;
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(JSON_INDENT);
                d(tag, message);
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(JSON_INDENT);
                d(tag, message);
                return;
            }
            e(tag, "Invalid Json");
        } catch (JSONException e) {
            e(tag, "Invalid Json");
        }
    }

}