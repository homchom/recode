package io.github.codeutilities.mod.config.internal;

import io.github.codeutilities.mod.config.types.IConfigEnum;

public enum DestroyItemResetType implements IConfigEnum {
    OFF,
    STANDARD,
    COMPACT;

    @Override
    public String getKey() {
        return "destroyitemresettype";
    }
}
