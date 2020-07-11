package io.github.codeutilities.config;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

@Config(name = "codeutilities")
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("features")
    public boolean playDiamondFire = true;
    @ConfigEntry.Category("features")
    public boolean discordRPC = false;
    @ConfigEntry.Category("commands")
    public boolean improvedLoreCmd = true;

    public static ModConfig getConfig() {
    	return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
