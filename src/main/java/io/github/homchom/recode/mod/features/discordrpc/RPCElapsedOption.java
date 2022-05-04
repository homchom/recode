package io.github.homchom.recode.mod.features.discordrpc;

import io.github.homchom.recode.mod.config.types.IConfigEnum;

public enum RPCElapsedOption implements IConfigEnum {

    STARTUP,
    SERVER_JOIN,
    PLOT,
    MODE;

    @Override
    public String getKey() {
        return "discordRPCElapsed";
    }
}
