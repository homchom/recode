package io.github.homchom.recode.mod.config.internal;

import io.github.homchom.recode.mod.config.types.IConfigEnum;

public enum DestroyItemResetType implements IConfigEnum {
    OFF,
    STANDARD,
    COMPACT;

    @Override
    public String getKey() {
        return "destroyitemresettype";
    }
}
