package io.github.codeutilities.config.idea.config;

import io.github.codeutilities.config.idea.structure.ConfigGroup;
import io.github.codeutilities.config.idea.structure.ConfigSubGroup;
import io.github.codeutilities.config.idea.types.BooleanSetting;
import io.github.codeutilities.config.idea.types.FloatSetting;
import io.github.codeutilities.config.idea.types.ListSetting;
import io.github.codeutilities.config.idea.types.StringSetting;

public class Highlight extends ConfigGroup {
    public Highlight(String name) {
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
        sound.register(new ListSetting<>("highlightSound",
                "1", "None", "Shield Block", "Bass Drum", "Banjo",
                "Bass", "Bell", "Bit", "Chime", "Cow Bell", "Didgeridoo", "Flute",
                "Guitar", "Harp", "Pling", "Hat", "Snare", "Iron Xylophone", "Xylophone",
                "Experience Orb Pickup", "Item Pickup"));
        sound.register(new FloatSetting("highlightSoundVolume", 3F));
        sound.register(new BooleanSetting("highlightOwnSenderSound", false));
        this.register(sound);
    }
}
