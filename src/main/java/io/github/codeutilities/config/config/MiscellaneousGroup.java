package io.github.codeutilities.config.config;

import io.github.codeutilities.config.structure.ConfigGroup;
import io.github.codeutilities.config.structure.ConfigSubGroup;
import io.github.codeutilities.config.types.BooleanSetting;
import io.github.codeutilities.config.types.LongSetting;
import io.github.codeutilities.config.types.StringSetting;

public class MiscellaneousGroup extends ConfigGroup {
    public MiscellaneousGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        // Non sub-grouped
        this.register(new BooleanSetting("itemApi", true));
        this.register(new BooleanSetting("quickVarScope", true));

        // Discord
        ConfigSubGroup discord = new ConfigSubGroup("discordrpc");
        discord.register(new BooleanSetting("discordRPC", true));
        discord.register(new LongSetting("discordRPCTimeout", 15000L));
        discord.register(new BooleanSetting("discordRPCShowElapsed", true));
        this.register(discord);

        // Audio
        ConfigSubGroup audio = new ConfigSubGroup("audio");
        audio.register(new BooleanSetting("audio", false));
        audio.register(new StringSetting("audioUrl", "https://audio.tomoli.me/"));
        audio.register(new BooleanSetting("audioAlerts", false));
        this.register(audio);
    }


}
