package com.lab.ugcmodule.media.ffmpeg.cmd;

import android.support.annotation.NonNull;

/**
 * Created by kuaigeng01 on 2017/7/16.
 */

public class PureCommand extends BaseCommand {

    private PureCommand(@NonNull String command) {
        super(command);
    }

    public static class Builder implements IBuilder {

        String pureCommand;

        public Builder setPureCommand(String pureCommand) {
            this.pureCommand = pureCommand;
            return this;
        }

        @Override
        public Command build() {

            return new PureCommand(pureCommand);
        }
    }
}
