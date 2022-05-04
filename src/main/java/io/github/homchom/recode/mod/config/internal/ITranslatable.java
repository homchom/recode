package io.github.homchom.recode.mod.config.internal;

import net.minecraft.network.chat.TranslatableComponent;

public interface ITranslatable {
    default TranslatableComponent getTranslation(String key) {
        return ITranslatable.get(key);
    }

    static TranslatableComponent get(String key) {
        return new TranslatableComponent(key);
    }
}
