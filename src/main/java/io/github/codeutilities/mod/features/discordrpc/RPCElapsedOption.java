package io.github.codeutilities.mod.features.discordrpc;

import io.github.codeutilities.mod.config.types.IConfigEnum;

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
