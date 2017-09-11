package com.lab.ugcmodule.media.ffmpeg;

import com.lab.ugcmodule.media.service.MediaOperatorParams;

import java.util.ArrayList;
import java.util.List;

/**
 * mini operation
 * Created by kuaigeng01 on 2017/7/16.
 */
public class Mini {

    public static List<MediaOperatorParams> mini(List<MediaOperatorParams> paramses) {

        if (paramses.size() < 2) {
            return paramses;
        }

        MediaOperatorParams closeVolume = query(MediaOperatorParams.CMD_EXTRACT_VIDEO, paramses);
        MediaOperatorParams adjustVolume = query(MediaOperatorParams.CMD_ADJUST_VOLUME, paramses);
        MediaOperatorParams watermark = query(MediaOperatorParams.CMD_WATERMARK, paramses);
        MediaOperatorParams compress = query(MediaOperatorParams.CMD_COMPRESS, paramses);

        if ((watermark != null || null != compress)
                && (null != closeVolume || null != adjustVolume)) {

            List<MediaOperatorParams> paramsList = new ArrayList<>();
            if (null != watermark) {
                paramsList.add(watermark);
            }

            if (null != compress) {
                paramsList.add(compress);
            }

            if (null != closeVolume) {
                paramsList.add(closeVolume);
            }

            if (null != adjustVolume) {
                paramsList.add(adjustVolume);
            }

            MediaOperatorParams complexOperatorParams = new MediaOperatorParams.Builder().setMultiplesOperator(paramsList)
                    .build(MediaOperatorParams.CMD_PRIVATE_COMPLEX);

            //删除被合并的命令
            int indexWater = -1;
            if (null != watermark) {
                indexWater = paramses.indexOf(watermark);
            } else if (null != compress) {
                indexWater = paramses.indexOf(compress);
            }

            paramses.set(indexWater, complexOperatorParams);//保持顺序

            if (null != compress) {
                paramses.remove(compress);
            }

            if (null != closeVolume) {
                paramses.remove(closeVolume);
            }

            if (null != adjustVolume) {
                paramses.remove(adjustVolume);
            }
        }

        return paramses;
    }

    public static MediaOperatorParams query(@MediaOperatorParams.CmdTypeDef int type, List<MediaOperatorParams> paramses) {
        for (MediaOperatorParams p : paramses) {
            if (p.getCmdTypeDef() == type) {
                return p;
            }
        }

        return null;
    }
}
