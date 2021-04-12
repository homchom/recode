package io.github.codeutilities.config;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

@Config(name = "codeutilities")
public class ModConfig implements ConfigData {

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
    @ConfigEntry.Gui.Tooltip()
    public final boolean autonightvis = false;
    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public final boolean autofly = false;
    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public final boolean autolagslayer = false;

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

    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public final boolean hideJoinLeaveMessages = false;
    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public final boolean hideVarScopeMessages = false;
    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public final boolean hideMsgMatchingRegex = false;
    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public final String hideMsgRegex = "";
    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public final boolean hideSessionSpy = false;
    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public final boolean hideMutedChat = false;

    @ConfigEntry.Category("keybinds")
    @ConfigEntry.Gui.Tooltip()
    public final boolean functionProcessSearch = true;
    @ConfigEntry.Category("keybinds")
    @ConfigEntry.Gui.Tooltip()
    public final int fsNormal = 100;
    @ConfigEntry.Category("keybinds")
    @ConfigEntry.Gui.Tooltip()
    public final int fsMed = 350;
    @ConfigEntry.Category("keybinds")
    @ConfigEntry.Gui.Tooltip()
    public final int fsFast = 1000;

    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip()
    public final boolean highlightName = false;
    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip()
    public String highlightMatcher = "{name}";
    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip()
    public final String highlightNamePrefix = "&e";
    /*
    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip()
    public NoteSounds highlightNameSound = NoteSounds.Pling;

     */
    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip()
    public boolean highlightNameSender = false;

    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    public final boolean dfButton = true;
    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    public final boolean chestReplacement = false;
    @ConfigEntry.Category("screen")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    @ConfigEntry.Gui.Tooltip()
    public final int signRenderDistance = 100;
    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    public final boolean variableScopeView = true;
    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    public final boolean cpuOnScreen = true;
    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    public final boolean f3Tps = true;
    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public final CosmeticType cosmeticType = CosmeticType.All_Cosmetics;

    @ConfigEntry.Category("misc")
    @ConfigEntry.Gui.Tooltip()
    public final boolean itemApi = true;
    @ConfigEntry.Category("misc")
    @ConfigEntry.Gui.Tooltip()
    public final boolean quickVarScope = true;
    // discord rpc
    @ConfigEntry.Category("misc")
    @ConfigEntry.Gui.Tooltip()
    public final boolean discordRPC = true;
    @ConfigEntry.Category("misc")
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100000)
    public final int discordRPCTimeout = 15000;

    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public enum CosmeticType {
        All_Cosmetics,
        No_Event_Cosmetics,
        Disabled
    }
}