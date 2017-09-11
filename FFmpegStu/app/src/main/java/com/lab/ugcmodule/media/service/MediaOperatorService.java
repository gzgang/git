package com.lab.ugcmodule.media.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kg.v1.tools.DebugLog;
import com.lab.ugcmodule.media.IMediaOperatorImplFace;
import com.lab.ugcmodule.media.OperatorResult;
import com.lab.ugcmodule.media.ffmpeg.FFmpegMediaOperatorImpl;

import org.wysaid.common.Common;
import org.wysaid.nativePort.CGENativeLibrary;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * ffmpeg 操作服务
 * Created by kuaigeng01 on 2017/6/23.
 */
public class MediaOperatorService extends Service {
    private IMediaOperatorImplFace mIMediaOperatorImplFace;
    private JniWorkHandler mJniWorkHandler;

    @MediaOperatorParams.CmdTypeDef
    private static int mCurrentWorkForWho = -1;
    private static IFFmpegAidlCallback mCurrentWorkIFFmpegAidlCallback;
    private static int mCurrentWorkMediaDuration = -1;

    public static boolean isNeedParseFFmpegLog() {
        return mCurrentWorkForWho == MediaOperatorParams.CMD_ADJUST_VOLUME
                || mCurrentWorkForWho == MediaOperatorParams.CMD_WATERMARK
                || mCurrentWorkForWho == MediaOperatorParams.CMD_OVERLAY
                || mCurrentWorkForWho == MediaOperatorParams.CMD_COMPRESS
                || mCurrentWorkForWho == MediaOperatorParams.CMD_FAST_SLOW_VIDEO
                || mCurrentWorkForWho == MediaOperatorParams.CMD_PRIVATE_COMPLEX
                || mCurrentWorkForWho == MediaOperatorParams.CMD_SCALE_COMPRESS
                || mCurrentWorkForWho == MediaOperatorParams.CMD_BACKGROUND_MUSIC;

    }

    public static void notifyFFmpegProcess(long progress) {
        if (null != mCurrentWorkIFFmpegAidlCallback) {
            try {
                float percent = 0;

                if (mCurrentWorkForWho == MediaOperatorParams.CMD_ADD_FILTER) {
                    percent = progress;
                } else {
                    if (mCurrentWorkMediaDuration > 0) {
                        percent = progress * 100f / 1000 / mCurrentWorkMediaDuration;
                    }
                }

                if (percent >= 0 && percent <= 100) {
                    mCurrentWorkIFFmpegAidlCallback.onFFmpegOperateProcess(mCurrentWorkForWho, (int) percent);
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private final IFFmpegAidlInterface.Stub stub = new IFFmpegAidlInterface.Stub() {

        @Override
        public void kill() throws RemoteException {
            if (DebugLog.isDebug()) {
                Log.w("remote", "kill " + Thread.currentThread());
            }

            mCurrentWorkIFFmpegAidlCallback = null;
            mCurrentWorkForWho = -1;
            mCurrentWorkMediaDuration = -1;

            Process.killProcess(Process.myPid());
        }

        @Override
        public void execute(MediaOperatorParams params, IFFmpegAidlCallback callback) throws RemoteException {
            if (DebugLog.isDebug()) {
                Log.w("remote", "execute " + Thread.currentThread());
            }

            mCurrentWorkIFFmpegAidlCallback = callback;
            mCurrentWorkForWho = params.getCmdTypeDef();

            if (isNeedParseFFmpegLog()) {
                mCurrentWorkMediaDuration = (int) params.getMediaDuration();
            } else {
                mCurrentWorkMediaDuration = -1;
            }

            MediaOperatorService.this.execute(params, callback);
        }
    };

    private CGENativeLibrary.LoadImageCallback loadImageCallback = new CGENativeLibrary.LoadImageCallback() {

        //Notice: the 'name' passed in is just what you write in the rule, e.g: 1.jpg
        //注意， 这里回传的name不包含任何路径名， 仅为具体的图片文件名如 1.jpg
        @Override
        public Bitmap loadImage(String name, Object arg) {

            Log.i(Common.LOG_TAG, "Loading file: " + name);
            AssetManager am = getAssets();
            InputStream is;
            try {
                is = am.open(name);
            } catch (IOException e) {
                Log.e(Common.LOG_TAG, "Can not open file " + name);
                return null;
            }

            return BitmapFactory.decodeStream(is);
        }

        @Override
        public void loadImageOK(Bitmap bmp, Object arg) {
            Log.i(Common.LOG_TAG, "Loading bitmap over, you can choose to recycle or cache");

            //The bitmap is which you returned at 'loadImage'.
            //You can call recycle when this function is called, or just keep it for further usage.
            //唯一不需要马上recycle的应用场景为 多个不同的滤镜都使用到相同的bitmap
            //那么可以选择缓存起来。
            bmp.recycle();
        }

        @Override
        public void generateVideoCallback(int percent) {

            notifyFFmpegProcess(percent);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (DebugLog.isDebug()) {
            Log.e("remote", "onUnbind");
        }

        clearWork();

        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (DebugLog.isDebug()) {
            Log.e("remote", "onStartCommand flags = " + flags + "; startId = " + startId);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mIMediaOperatorImplFace = new FFmpegMediaOperatorImpl();

        HandlerThread handlerThread = new HandlerThread("remote_ugc");
        handlerThread.start();
        mJniWorkHandler = new JniWorkHandler(MediaOperatorService.this, handlerThread.getLooper());

        //The second param will be passed as the second arg of the callback function.
        //第二个参数根据自身需要设置， 将作为 loadImage 第二个参数回传
        CGENativeLibrary.setLoadImageCallback(loadImageCallback, "remote_ugc");

        if (DebugLog.isDebug()) {
            Log.e("remote", "onCreate");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DebugLog.isDebug()) {
            Log.e("remote", "onDestroy");
        }

        clearWork();
    }

    private void clearWork() {
        mCurrentWorkIFFmpegAidlCallback = null;
        mCurrentWorkForWho = -1;
        mCurrentWorkMediaDuration = -1;
    }

    private static class JniWorkHandler extends Handler {
        private WeakReference<MediaOperatorService> ref;

        JniWorkHandler(MediaOperatorService parent, Looper looper) {
            super(looper);
            ref = new WeakReference<>(parent);
        }
    }

    private void execute(final MediaOperatorParams params, final IFFmpegAidlCallback callback) {
        mJniWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != callback) {
                        callback.onFFmpegOperateStart(params.getCmdTypeDef());
                    }

                    OperatorResult result = mIMediaOperatorImplFace.command(params);

                    if (null != callback) {
                        callback.onFFmpegOperateFinish(params.getCmdTypeDef(), result);
                    }
                } catch (Throwable e) {

                    try {
                        if (null != callback) {
                            callback.onFFmpegOperateFinish(params.getCmdTypeDef(), null);
                        }
                    } catch (Exception ce) {

                    }
                }
            }
        });
    }
}
