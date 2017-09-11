package com.lab.ugcmodule.media;

import com.lab.ugcmodule.media.service.MediaOperatorParams;

/**
 * Created by kuaigeng01 on 2017/6/14.
 */

public interface MediaOperatorListener {

    void onStart(@MediaOperatorParams.CmdTypeDef int forWho);

    void onProgressUpdate(@MediaOperatorParams.CmdTypeDef int forWho, int percent);

    void onError(@MediaOperatorParams.CmdTypeDef int forWho);

    void onComplete(@MediaOperatorParams.CmdTypeDef int forWho, OperatorResult result);

    void onAllTaskFinish();
}
