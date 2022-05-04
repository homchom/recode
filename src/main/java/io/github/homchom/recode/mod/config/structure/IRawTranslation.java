package io.github.homchom.recode.mod.config.structure;


import net.minecraft.network.chat.TextComponent;

import java.util.Optional;

public interface IRawTranslation<T> extends IRawKey<T> {
    default T setRawKey(String key) {
        return null;
    }

    default Optional<TextComponent> getRawKey() {
        return Optional.empty();
    }

    default T setRawTooltip(String key) {
        return null;
    }

    default Optional<TextComponent> getRawTooltip() {
        return Optional.empty();
    }
}
