package io.github.codeutilities.mod.config.internal;

import net.minecraft.text.TranslatableText;

public interface ITranslatable {
    default TranslatableText getTranslation(String key) {
        return ITranslatable.get(key);
    }

    static TranslatableText get(String key) {
        return new TranslatableText(key);
    }
}
