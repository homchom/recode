package io.github.codeutilities.config;

import me.sargunvohra.mcmods.autoconfig1u.*;
import me.sargunvohra.mcmods.autoconfig1u.annotation.*;

@Config(name = "codeutilities")
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("features")
    public boolean dfButton = true;
    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip(count = 1)
    public boolean itemApi = true;
    @ConfigEntry.Category("commands")
    public boolean dfCommands = true;
    @ConfigEntry.Category("commands")
    public boolean errorSound = true;
    @ConfigEntry.Category("commands")
    public int headMenuMaxRender = 140;
    @ConfigEntry.Category("commands")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 360)
    public int colorMaxRender = 158;
    
    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
