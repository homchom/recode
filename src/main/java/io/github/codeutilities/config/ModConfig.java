package io.github.codeutilities.config;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

@Config(name = "codeutilities")
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("features")
    public boolean dfButton = true;
    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public boolean itemApi = true;
    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public boolean chestReplacement = false;
    @ConfigEntry.Category("features")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int signRenderDistance = 100;
    @ConfigEntry.Category("features")
    public boolean variableScopeView = true;
    @ConfigEntry.Category("features")
    public boolean discordRPC = true;
    @ConfigEntry.Category("features")
    public boolean autoRC = false;
    @ConfigEntry.Category("features")
    public boolean autotime = false;
    @ConfigEntry.Category("features")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 24000)
    public int autotimeval = 0;

    @ConfigEntry.Category("commands")
    public boolean dfCommands = true;
    @ConfigEntry.Category("commands")
    public boolean errorSound = true;
    @ConfigEntry.Category("commands")
    @ConfigEntry.BoundedDiscrete(min = 10, max = 10000)
    public int headMenuMaxRender = 1000;
    @ConfigEntry.Category("commands")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 360)
    public int colorMaxRender = 158;
    @ConfigEntry.Category("commands")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
    public int colorLines = 5;

    @ConfigEntry.Category("social")
    public boolean socialFeatures = true;
    @ConfigEntry.Category("social")
    public boolean cosmetics = true;
    @ConfigEntry.Category("social")
    public boolean codeUtilsChat = true;
    @ConfigEntry.Category("social")
    public boolean allParty = true;

    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
