package io.github.codeutilities.config;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

@Config(name = "codeutilities")
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public final boolean dfButton = true;
    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public final boolean itemApi = true;
    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public final boolean chestReplacement = false;
    @ConfigEntry.Category("features")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    @ConfigEntry.Gui.Tooltip()
    public final int signRenderDistance = 100;
    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public final boolean variableScopeView = true;
    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public final boolean discordRPC = true;
    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.Tooltip()
    public final boolean cpuOnScreen = true;


    @ConfigEntry.Category("commands")
    @ConfigEntry.Gui.Tooltip()
    public final boolean dfCommands = true;
    @ConfigEntry.Category("commands")
    @ConfigEntry.Gui.Tooltip()
    public final boolean errorSound = true;
    @ConfigEntry.Category("commands")
    @ConfigEntry.Gui.Tooltip()
    public final int headMenuMaxRender = 1000;
    @ConfigEntry.Category("commands")
    @ConfigEntry.Gui.Tooltip()
    public int colorMaxRender = 158;
    @ConfigEntry.Category("commands")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
    @ConfigEntry.Gui.Tooltip()
    public int colorLines = 5;

    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public final boolean autoRC = false;
    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public final boolean autotime = false;
    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public final int autotimeval = 0;
    @ConfigEntry.Category("automation")
    public final boolean autonightvis = false;
    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public final boolean autofly = false;

    @ConfigEntry.Category("keybinds")
    @ConfigEntry.Gui.Tooltip()
    public final int fsNormal = 100;
    @ConfigEntry.Category("keybinds")
    @ConfigEntry.Gui.Tooltip()
    public final int fsMed = 350;
    @ConfigEntry.Category("keybinds")
    @ConfigEntry.Gui.Tooltip()
    public final int fsFast = 1000;

    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public final boolean hideJoinLeaveMessages = false;
    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public boolean hideVarScopeMessages = false;
    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public boolean hideMsgMatchingRegex = false;
    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public String hideMsgRegex = "";
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