package io.github.homchom.recode.mod.config.impl;

import io.github.homchom.recode.mod.config.ConfigSounds;
import io.github.homchom.recode.mod.config.structure.ConfigGroup;
import io.github.homchom.recode.mod.config.structure.ConfigSubGroup;
import io.github.homchom.recode.mod.config.types.BooleanSetting;
import io.github.homchom.recode.mod.config.types.FloatSetting;
import io.github.homchom.recode.mod.config.types.SoundSetting;
import io.github.homchom.recode.mod.config.types.StringSetting;

public class HighlightGroup extends ConfigGroup {
    public HighlightGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        // Non sub-grouped
        this.register(new BooleanSetting("highlight", false));
        this.register(new BooleanSetting("highlightIgnoreSender", false));

        // Text
        ConfigSubGroup text = new ConfigSubGroup("text");
        text.register(new StringSetting("highlightMatcher", "{name}"));
        text.register(new StringSetting("highlightPrefix", "&e"));
        this.register(text);

        // Sound
        ConfigSubGroup sound = new ConfigSubGroup("sound");
        sound.register(new SoundSetting("highlightSound")
                .setSelected(ConfigSounds.SHIELD_BLOCK));
        sound.register(new FloatSetting("highlightSoundVolume", 3F));
        sound.register(new BooleanSetting("highlightOwnSenderSound", false));
        this.register(sound);
    }
}
