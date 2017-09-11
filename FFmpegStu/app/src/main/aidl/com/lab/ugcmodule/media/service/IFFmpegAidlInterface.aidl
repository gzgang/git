// IFFmpegAidlInterface.aidl
package com.lab.ugcmodule.media.service;
import com.lab.ugcmodule.media.service.MediaOperatorParams;
import com.lab.ugcmodule.media.service.IFFmpegAidlCallback;

interface IFFmpegAidlInterface {

     void kill();
     void execute(in MediaOperatorParams params,in IFFmpegAidlCallback callback);
}
