package io.github.codeutilities.config;

import me.sargunvohra.mcmods.autoconfig1u.*;
import me.sargunvohra.mcmods.autoconfig1u.annotation.*;

@Config(name = "codeutilities")
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("features")
    public boolean dfButton = true;
    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public boolean itemApi = true;
    @ConfigEntry.Category("features")
    public boolean variableScopeView = true;
    @ConfigEntry.Category("commands")
    public boolean dfCommands = true;
    @ConfigEntry.Category("commands")
    public boolean errorSound = true;
    @ConfigEntry.Category("commands")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 300)
    public int headMenuMaxRender = 140;
    @ConfigEntry.Category("commands")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 360)
    public int colorMaxRender = 158;
    @ConfigEntry.Category("commands")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
    public int colorLines = 5;
    
    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
