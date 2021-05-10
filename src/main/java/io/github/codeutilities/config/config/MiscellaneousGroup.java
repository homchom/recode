package io.github.codeutilities.config.config;

import io.github.codeutilities.config.structure.ConfigGroup;
import io.github.codeutilities.config.structure.ConfigSubGroup;
import io.github.codeutilities.config.types.*;
import io.github.codeutilities.config.types.list.StringListSetting;

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

        // Quick Number Change
        ConfigSubGroup quickNum = new ConfigSubGroup("quicknum");
        quickNum.register(new BooleanSetting("quicknum", true));
        quickNum.register(new BooleanSetting("quicknumSound", true));
        quickNum.register(new DoubleSetting("quicknumAmount", 1.0));
        quickNum.register(new DoubleSetting("quicknumFineAmount", 0.1));
        this.register(quickNum);

    }


}
