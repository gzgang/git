package com.lab.ugcmodule.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kg.v1.global.Global;
import com.kg.v1.tools.DebugLog;
import com.lab.ugcmodule.media.ffmpeg.cmd.Overlay;
import com.lab.ugcmodule.media.ffmpeg.cmd.Watermark;
import com.lab.ugcmodule.media.service.IFFmpegAidlCallback;
import com.lab.ugcmodule.media.service.IFFmpegAidlInterface;
import com.lab.ugcmodule.media.service.MediaOperatorParams;
import com.lab.ugcmodule.media.service.MediaOperatorService;
import com.lab.ugcmodule.media.service.MediaOperatorTaskBuilder;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 媒体文件编辑
 * Created by kuaigeng01 on 2017/6/14.
 */
public class MediaOperator implements IMediaOperatorFace {

    private static final String TAG = "MediaOperator";

    private final static int MSG_NOTIFY_START = 0x1;
    private final static int MSG_NOTIFY_FINISH = 0x2;
    private final static int MSG_CHECK_TASK_EXECUTE = 0x3;
    private final static int MSG_NOTIFY_PROCESS = 0x4;

    private Queue<OperatorWrapper> mWaitingTask = new LinkedList<>();
    private IFFmpegAidlInterface mMediaOperatorService;
    private MediaOperator.CallbackUIHandler mCallbackUIHandler;
    private OperatorWrapper mCurrentOperatorWrapper;


    private MediaOperator() {

        mCallbackUIHandler = new CallbackUIHandler(this);
    }

    private static class MediaOperatorSingleInstance {
        static MediaOperator instance = new MediaOperator();
    }

    public static MediaOperator getInstance() {
        return MediaOperator.MediaOperatorSingleInstance.instance;
    }

    private void killRemoteService() {
        if (DebugLog.isDebug()) {
            Log.w(TAG, "stop self execute ");
        }

        mCurrentOperatorWrapper = null;
        try {
            if (null != mMediaOperatorService) {
                Global.getGlobalContext().unbindService(mMediaOperatorServiceConnection);
            } else {
                if (DebugLog.isDebug()) {
                    Log.w(TAG, "service has already disconnection");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != mMediaOperatorService) {
            try {
                mMediaOperatorService.kill();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            mMediaOperatorService = null;
        }
    }

    private ServiceConnection mMediaOperatorServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (DebugLog.isDebug()) {
                Log.w(TAG, "onServiceConnected");
            }

            mMediaOperatorService = IFFmpegAidlInterface.Stub.asInterface(service);

            mCallbackUIHandler.removeMessages(MSG_CHECK_TASK_EXECUTE);
            mCallbackUIHandler.sendEmptyMessage(MSG_CHECK_TASK_EXECUTE);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (DebugLog.isDebug()) {
                Log.w(TAG, "onServiceDisconnected");
            }

            mMediaOperatorService = null;

            if (null != mCurrentOperatorWrapper) {
                notifyProcessFinish(mCurrentOperatorWrapper);
            }
        }
    };

    private void checkExecuteCmd() {

        if (mWaitingTask.isEmpty() && null != mCurrentOperatorWrapper) {
            //notify no more task waiting to execute
            mCurrentOperatorWrapper.notifyAllTaskFinish();
        }

        mCurrentOperatorWrapper = mWaitingTask.poll();
        final OperatorWrapper wrapper = mCurrentOperatorWrapper;

        if (DebugLog.isDebug()) {
            Log.w(TAG, "checkExecuteCmd " + wrapper);
        }

        if (wrapper == null) {
            if (DebugLog.isDebug()) {
                Log.w(TAG, "onServiceConnected no more task");
            }
//            任务完成后不自动关闭，服务的开启和关闭让外部控制，避免进程启动慢的问题
//            killRemoteService();
            return;
        }

        try {
            mMediaOperatorService.execute(wrapper.params, new IFFmpegAidlCallback.Stub() {

                @Override
                public void onFFmpegOperateStart(int forWho) throws RemoteException {
                    if (DebugLog.isDebug()) {
                        Log.w(TAG, "onFFmpegOperateStart " + Thread.currentThread());
                    }

                    notifyProcessStart(wrapper);
                }

                @Override
                public void onFFmpegOperateProcess(int who, int percent) throws RemoteException {

                    notifyProcessing(wrapper, percent);
                }

                @Override
                public void onFFmpegOperateFinish(int forWho, OperatorResult result) throws RemoteException {

                    if (DebugLog.isDebug()) {
                        Log.w(TAG, "onFFmpegOperateFinish " + Thread.currentThread());
                        Log.w(TAG, "onFFmpegOperateFinish " + result);
                    }

                    wrapper.setResult(result);

                    notifyProcessFinish(wrapper);

                    //check is have next task need process
                    mCallbackUIHandler.sendEmptyMessage(MSG_CHECK_TASK_EXECUTE);
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void notifyProcessStart(OperatorWrapper wrapper) {
        Message message = mCallbackUIHandler.obtainMessage();
        message.obj = wrapper;
        message.what = MSG_NOTIFY_START;

        mCallbackUIHandler.sendMessage(message);
    }

    private void notifyProcessing(OperatorWrapper wrapper, int second) {
        Message message = mCallbackUIHandler.obtainMessage();
        message.obj = wrapper;
        message.arg1 = second;
        message.what = MSG_NOTIFY_PROCESS;

        mCallbackUIHandler.sendMessage(message);
    }

    private void requestExecuteCommand() {
        if (null != mMediaOperatorService) {
            if (null == mCurrentOperatorWrapper) {
                mCallbackUIHandler.removeMessages(MSG_CHECK_TASK_EXECUTE);
                mCallbackUIHandler.sendEmptyMessage(MSG_CHECK_TASK_EXECUTE);
            } else {
                //wait remote execute
                if (DebugLog.isDebug()) {
                    Log.i(TAG, "wait remote execute");
                }
            }
        } else {
            Context context = Global.getGlobalContext();
            Intent intent = new Intent(context, MediaOperatorService.class);
            context.bindService(intent, mMediaOperatorServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void notifyProcessFinish(OperatorWrapper wrapper) {
        Message message = mCallbackUIHandler.obtainMessage();
        message.obj = wrapper;
        message.what = MSG_NOTIFY_FINISH;

        mCallbackUIHandler.sendMessage(message);
    }

    private static class CallbackUIHandler extends Handler {
        WeakReference<MediaOperator> ref;

        CallbackUIHandler(MediaOperator parent) {
            super(Looper.getMainLooper());
            ref = new WeakReference<>(parent);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MediaOperator parent = ref.get();
            if (null == parent) {
                return;
            }

            switch (msg.what) {
                case MSG_NOTIFY_START:

                    if (msg.obj instanceof OperatorWrapper) {
                        OperatorWrapper wrapper = (OperatorWrapper) msg.obj;
                        wrapper.notifyStart();
                    }

                    break;
                case MSG_NOTIFY_FINISH:
                    if (msg.obj instanceof OperatorWrapper) {
                        OperatorWrapper wrapper = (OperatorWrapper) msg.obj;
                        wrapper.notifyFinish();
                    }

                    break;
                case MSG_CHECK_TASK_EXECUTE:
                    parent.checkExecuteCmd();

                    break;

                case MSG_NOTIFY_PROCESS:
                    if (msg.obj instanceof OperatorWrapper) {
                        OperatorWrapper wrapper = (OperatorWrapper) msg.obj;
                        wrapper.notifyProcess(msg.arg1);
                    }

                    break;
            }
        }
    }

    @Override
    public void initRemoteService() {

        Context context = Global.getGlobalContext();
        Intent intent = new Intent(context, MediaOperatorService.class);
        context.bindService(intent, mMediaOperatorServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void cancelAllTask() {
        if (DebugLog.isDebug()) {
            Log.w(TAG, "cancelAllTask");
        }
        mWaitingTask.clear();

        killRemoteService();
    }

    @Override
    public void commandMultipleTask(MediaOperatorTaskBuilder builder, @Nullable MediaOperatorListener listener) {
        List<MediaOperatorParams> mediaOperatorParamsList = builder.getTaskList();

        if (mediaOperatorParamsList.isEmpty()) {
            if (DebugLog.isDebug()) {
                Log.w(TAG, "commandMultipleTask but no task add");
            }

            if (null != listener) {
                listener.onAllTaskFinish();
            }
            return;
        }

        OperatorWrapper wrapper;
        for (MediaOperatorParams params : mediaOperatorParamsList) {
            wrapper = new OperatorWrapper(params, listener);
            mWaitingTask.add(wrapper);
        }

        requestExecuteCommand();
    }

    private void command(MediaOperatorParams params, @Nullable MediaOperatorListener listener) {
        if (DebugLog.isDebug()) {
            Log.w(TAG, "command");
        }

        OperatorWrapper wrapper = new OperatorWrapper(params, listener);
        mWaitingTask.add(wrapper);

        requestExecuteCommand();
    }

    @Override
    public void concatVideo(@NonNull List<String> inputVideoFilePathList, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener) {
        MediaOperatorParams params = new MediaOperatorParams.Builder()
                .setInputVideoFilePathList(inputVideoFilePathList)
                .setOutputMediaFilePath(outputVideoPath)
                .build(MediaOperatorParams.CMD_CONCAT);


        command(params, listener);
    }

    @Override
    public void trimVideo(@NonNull String inputVideoFilePath, int startTime, int duration, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener) {
        MediaOperatorParams params = new MediaOperatorParams.Builder()
                .setInputMediaFilePath(inputVideoFilePath)
                .setOutputMediaFilePath(outputVideoPath)
                .setIntArg1(startTime)
                .setIntArg2(duration)
                .build(MediaOperatorParams.CMD_TRIM);

        command(params, listener);
    }

    @Override
    public void closeVolume(@NonNull String inputVideoFilePath, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener) {
        MediaOperatorParams params = new MediaOperatorParams.Builder()
                .setInputMediaFilePath(inputVideoFilePath)
                .setOutputMediaFilePath(outputVideoPath)
                .build(MediaOperatorParams.CMD_EXTRACT_VIDEO);


        command(params, listener);
    }

    @Override
    public void adjustVolume(@NonNull String inputVideoFilePath, float volumePercent, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener) {
        MediaOperatorParams params = new MediaOperatorParams.Builder()
                .setInputMediaFilePath(inputVideoFilePath)
                .setOutputMediaFilePath(outputVideoPath)
                .setFloatArg1(volumePercent)
                .build(MediaOperatorParams.CMD_ADJUST_VOLUME);


        command(params, listener);
    }

    @Override
    public void fastOrSlowVideo(@NonNull String inputVideoFilePath, float rate, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener) {
        MediaOperatorParams params = new MediaOperatorParams.Builder()
                .setInputMediaFilePath(inputVideoFilePath)
                .setOutputMediaFilePath(outputVideoPath)
                .setFloatArg1(rate)
                .build(MediaOperatorParams.CMD_FAST_SLOW_VIDEO);


        command(params, listener);
    }

    @Override
    public void watermark(@NonNull String inputVideoFilePath, @NonNull List<Watermark> watermarks, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener) {
        MediaOperatorParams params = new MediaOperatorParams.Builder()
                .setInputMediaFilePath(inputVideoFilePath)
                .setOutputMediaFilePath(outputVideoPath)
                .setWatermarks(watermarks)
                .build(MediaOperatorParams.CMD_WATERMARK);


        command(params, listener);
    }

    @Override
    public void overlay(@NonNull String inputVideoFilePath, @NonNull List<Overlay> overlays, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener) {
        MediaOperatorParams params = new MediaOperatorParams.Builder()
                .setInputMediaFilePath(inputVideoFilePath)
                .setOutputMediaFilePath(outputVideoPath)
                .setVideoOverlays(overlays)
                .build(MediaOperatorParams.CMD_OVERLAY);


        command(params, listener);
    }

    @Override
    public void addBackgroundMusic(@NonNull String inputVideoPath, @NonNull String inputBackgroundMusicPath, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener) {
        MediaOperatorParams params = new MediaOperatorParams.Builder()
                .setInputMediaFilePath(inputVideoPath)
                .setInputBackgroundMusicPath(inputBackgroundMusicPath)
                .setOutputMediaFilePath(outputVideoPath)
                .build(MediaOperatorParams.CMD_BACKGROUND_MUSIC);


        command(params, listener);
    }

    @Override
    public void addFilter(@NonNull String inputVideoPath, @NonNull String filterConfig, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener) {
        MediaOperatorParams params = new MediaOperatorParams.Builder()
                .setInputMediaFilePath(inputVideoPath)
                .setOutputMediaFilePath(outputVideoPath)
                .setStringArg1(filterConfig)
                .build(MediaOperatorParams.CMD_ADD_FILTER);


        command(params, listener);
    }

    @Override
    public void compress(@NonNull String inputVideoPath, int quality, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener) {
        MediaOperatorParams params = new MediaOperatorParams.Builder()
                .setInputMediaFilePath(inputVideoPath)
                .setOutputMediaFilePath(outputVideoPath)
                .setIntArg1(quality)
                .build(MediaOperatorParams.CMD_COMPRESS);

        command(params, listener);
    }

    @Override
    public void scaleAndCompress(@NonNull String inputVideoPath, int width, int height, @NonNull String outputVideoPath, @Nullable MediaOperatorListener listener) {
        MediaOperatorParams params = new MediaOperatorParams.Builder()
                .setInputMediaFilePath(inputVideoPath)
                .setOutputMediaFilePath(outputVideoPath)
                .setIntArg1(width)
                .setIntArg2(height)
                .build(MediaOperatorParams.CMD_SCALE_COMPRESS);

        command(params, listener);
    }
}
