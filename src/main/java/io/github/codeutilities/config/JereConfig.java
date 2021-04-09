package io.github.codeutilities.config;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

@Config(name = "jeremaster")
public class JereConfig implements ConfigData {

    @ConfigEntry.Category("jeremaster")
    @ConfigEntry.Gui.Tooltip()
    public final boolean streamerMode = false;

    public static JereConfig getConfig() {
        return AutoConfig.getConfigHolder(JereConfig.class).getConfig();
    }
}
