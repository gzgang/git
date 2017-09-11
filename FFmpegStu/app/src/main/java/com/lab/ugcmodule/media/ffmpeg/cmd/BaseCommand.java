package com.lab.ugcmodule.media.ffmpeg.cmd;

import android.support.annotation.NonNull;

/**
 * Created by kuaigeng01 on 2017/6/14.
 */

class BaseCommand implements Command {
    private final String command;

    BaseCommand(@NonNull String command) {
        this.command = command;
    }

    @Override
    public String getCommand() {
        return command;
    }

    interface IBuilder {

        Command build();
    }
}
