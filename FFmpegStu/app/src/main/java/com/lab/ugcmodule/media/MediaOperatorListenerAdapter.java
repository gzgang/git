package com.lab.ugcmodule.media;

import com.lab.ugcmodule.media.service.MediaOperatorParams;

/**
 * Created by kuaigeng01 on 2017/6/14.
 */

public class MediaOperatorListenerAdapter implements MediaOperatorListener {
    @Override
    public void onStart(@MediaOperatorParams.CmdTypeDef int forWho) {

    }

    @Override
    public void onProgressUpdate(@MediaOperatorParams.CmdTypeDef int forWho, int percent) {

    }

    @Override
    public void onError(@MediaOperatorParams.CmdTypeDef int forWho) {

    }

    @Override
    public void onComplete(@MediaOperatorParams.CmdTypeDef int forWho, OperatorResult result) {

    }

    @Override
    public void onAllTaskFinish() {

    }
}
