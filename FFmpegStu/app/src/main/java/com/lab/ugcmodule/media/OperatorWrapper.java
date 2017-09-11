package com.lab.ugcmodule.media;

import com.lab.ugcmodule.media.service.MediaOperatorParams;

/**
 * Created by kuaigeng01 on 2017/6/14.
 */

public class OperatorWrapper {

    private OperatorResult result;
    final MediaOperatorListener listener;
    final MediaOperatorParams params;

    OperatorWrapper(OperatorResult result, MediaOperatorListener listener) {
        this.result = result;
        this.listener = listener;
        this.params = null;
    }

    OperatorWrapper(MediaOperatorParams params, MediaOperatorListener listener) {
        this.result = null;
        this.listener = listener;
        this.params = params;
    }

    public MediaOperatorParams getParams() {
        return params;
    }

    public void setResult(OperatorResult result) {
        this.result = result;
    }


    void notifyStart() {
        if (null != listener) {
            listener.onStart(params.getCmdTypeDef());
        }
    }

    void notifyFinish() {
        if (null != listener) {
            if (null == result) {
                listener.onError(params.getCmdTypeDef());

            } else {
                listener.onComplete(params.getCmdTypeDef(), result);
            }
        }
    }

    void notifyProcess(int progress) {
        if (null != listener) {
            listener.onProgressUpdate(params.getCmdTypeDef(), progress);
        }
    }

    void notifyAllTaskFinish() {
        if (null != listener) {
            listener.onAllTaskFinish();
        }
    }

    @Override
    public String toString() {
        return "OperatorWrapper{" +
                "result=" + result +
                ", listener=" + listener +
                ", params=" + params +
                '}';
    }
}
