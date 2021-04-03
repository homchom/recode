package io.github.codeutilities.config;

import me.sargunvohra.mcmods.autoconfig1u.*;
import me.sargunvohra.mcmods.autoconfig1u.annotation.*;

@Config(name = "codeutilities")
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public boolean dfButton = true;
    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public boolean itemApi = true;
    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public boolean chestReplacement = false;
    @ConfigEntry.Category("features")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    @ConfigEntry.Gui.Tooltip()
    public int signRenderDistance = 100;
    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public boolean variableScopeView = true;
    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public boolean discordRPC = true;
    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public boolean cpuOnScreen = true;


    @ConfigEntry.Category("commands")
    @ConfigEntry.Gui.Tooltip()
    public boolean dfCommands = true;
    @ConfigEntry.Category("commands")
    @ConfigEntry.Gui.Tooltip()
    public boolean errorSound = true;
    @ConfigEntry.Category("commands")
    @ConfigEntry.BoundedDiscrete(min = 10, max = 10000)
    @ConfigEntry.Gui.Tooltip()
    public int headMenuMaxRender = 1000;
    @ConfigEntry.Category("commands")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 360)
    @ConfigEntry.Gui.Tooltip()
    public int colorMaxRender = 158;
    @ConfigEntry.Category("commands")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
    @ConfigEntry.Gui.Tooltip()
    public int colorLines = 5;

    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public boolean autoRC = false;
    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public boolean autotime = false;
    @ConfigEntry.Category("automation")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 24000)
    @ConfigEntry.Gui.Tooltip()
    public int autotimeval = 0;
    @ConfigEntry.Category("automation")
    public boolean autonightvis = false;
    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public boolean autofly = false;

    @ConfigEntry.Category("keybinds")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 1000)
    @ConfigEntry.Gui.Tooltip()
    public int fsNormal = 100;
    @ConfigEntry.Category("keybinds")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 1000)
    @ConfigEntry.Gui.Tooltip()
    public int fsMed = 350;
    @ConfigEntry.Category("keybinds")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 1000)
    @ConfigEntry.Gui.Tooltip()
    public int fsFast = 1000;

    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public boolean hideJoinLeaveMessages = false;
    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public boolean hideVarScopeMessages = false;
    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public boolean hideSessionSpy = false;
    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public boolean hideMutedChat = false;

    /*
    @ConfigEntry.Category("social")
    public boolean socialFeatures = true;
    @ConfigEntry.Category("social")
    @ConfigEntry.Gui.Tooltip()
    public boolean cosmetics = true;
    @ConfigEntry.Category("social")
    public boolean codeUtilsChat = true;
    @ConfigEntry.Category("social")
    public boolean allParty = true;
    */
    
    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}