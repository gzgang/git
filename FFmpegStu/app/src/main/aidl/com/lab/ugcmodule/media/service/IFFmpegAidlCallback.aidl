// IFFmpegAidlCallback.aidl
package com.lab.ugcmodule.media.service;
import com.lab.ugcmodule.media.OperatorResult;

interface IFFmpegAidlCallback {
    void onFFmpegOperateFinish(int who,in OperatorResult result);
    void onFFmpegOperateStart(int who);
    void onFFmpegOperateProcess(int who,int progress);
}
