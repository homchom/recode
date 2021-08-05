package io.github.codeutilities.mod.config.types;

import io.github.codeutilities.mod.config.structure.ConfigSetting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class TextDescription extends ConfigSetting<Text> {

    public TextDescription(String key) {
        super(key, new LiteralText(""));
    }
}
