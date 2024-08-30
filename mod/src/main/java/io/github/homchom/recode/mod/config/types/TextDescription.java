package io.github.homchom.recode.mod.config.types;

import io.github.homchom.recode.mod.config.structure.ConfigSetting;
import net.minecraft.network.chat.Component;

public class TextDescription extends ConfigSetting<Component> {

    public TextDescription(String key) {
        super(key, Component.literal(""));
    }
}
