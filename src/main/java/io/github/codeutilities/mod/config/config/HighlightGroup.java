package io.github.codeutilities.mod.config.config;

import io.github.codeutilities.mod.config.structure.ConfigGroup;
import io.github.codeutilities.mod.config.structure.ConfigSubGroup;
import io.github.codeutilities.mod.config.types.BooleanSetting;
import io.github.codeutilities.mod.config.types.FloatSetting;
import io.github.codeutilities.mod.config.types.StringSetting;
import io.github.codeutilities.mod.config.types.list.StringListSetting;

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
        sound.register(new StringListSetting("highlightSound",
                "None", "Shield Block", "Bass Drum", "Banjo",
                "Bass", "Bell", "Bit", "Chime", "Cow Bell", "Didgeridoo", "Flute",
                "Guitar", "Harp", "Pling", "Hat", "Snare", "Iron Xylophone", "Xylophone",
                "Experience Orb Pickup", "Item Pickup")
                .setSelected("Shield Block")
        );
        sound.register(new FloatSetting("highlightSoundVolume", 3F));
        sound.register(new BooleanSetting("highlightOwnSenderSound", false));
        this.register(sound);
    }
}
