package io.github.codeutilities.config.structure;


import net.minecraft.text.LiteralText;

import java.util.Optional;

public interface IRawTranslation<T> extends IRawKey<T> {
    default T setRawKey(String key) {
        return null;
    }

    default Optional<LiteralText> getRawKey() {
        return Optional.empty();
    }

    default T setRawTooltip(String key) {
        return null;
    }

    default Optional<LiteralText> getRawTooltip() {
        return Optional.empty();
    }
}
