package io.github.codeutilities.config;

import me.sargunvohra.mcmods.autoconfig1u.*;
import me.sargunvohra.mcmods.autoconfig1u.annotation.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

@Config(name = "codeutilities")
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public boolean autoRC = false;
    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public boolean autotime = false;
    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public int autotimeval = 0;
    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public boolean autonightvis = false;
    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public boolean autofly = false;
    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public boolean autolagslayer = false;

    @ConfigEntry.Category("commands")
    @ConfigEntry.Gui.Tooltip()
    public boolean dfCommands = true;
    @ConfigEntry.Category("commands")
    @ConfigEntry.Gui.Tooltip()
    public boolean errorSound = true;
    @ConfigEntry.Category("commands")
    @ConfigEntry.Gui.Tooltip()
    public int headMenuMaxRender = 1000;
    @ConfigEntry.Category("commands")
    @ConfigEntry.Gui.Tooltip()
    public int colorMaxRender = 158;
    @ConfigEntry.Category("commands")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
    @ConfigEntry.Gui.Tooltip()
    public int colorLines = 5;

    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public boolean hideJoinLeaveMessages = false;
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

    @ConfigEntry.Category("keybinds")
    @ConfigEntry.Gui.Tooltip()
    public boolean functionProcessSearch = true;
    @ConfigEntry.Category("keybinds")
    @ConfigEntry.Gui.Tooltip()
    public int fsNormal = 100;
    @ConfigEntry.Category("keybinds")
    @ConfigEntry.Gui.Tooltip()
    public int fsMed = 350;
    @ConfigEntry.Category("keybinds")
    @ConfigEntry.Gui.Tooltip()
    public int fsFast = 1000;

    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean highlight = false;
    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip()
    public String highlightMatcher = "{name}";
    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip()
    public String highlightPrefix = "&e";
    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
    public ConfigSounds highlightSound = ConfigSounds.ShieldBlock;
    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip()
    public float highlightSoundVolume = 3F;
    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean highlightOwnSenderSound = false;
    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip()
    public boolean highlightIgnoreSender = false;

    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    public boolean dfButton = true;
    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    public boolean chestReplacement = false;
    @ConfigEntry.Category("screen")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    @ConfigEntry.Gui.Tooltip()
    public int signRenderDistance = 100;
    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    public boolean variableScopeView = true;
    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    public boolean cpuOnScreen = true;
    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    public boolean f3Tps = true;
    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public CosmeticType cosmeticType = CosmeticType.All_Cosmetics;

    @ConfigEntry.Category("misc")
    @ConfigEntry.Gui.Tooltip()
    public boolean itemApi = true;
    @ConfigEntry.Category("misc")
    @ConfigEntry.Gui.Tooltip()
    public boolean quickVarScope = true;
    // discord rpc
    @ConfigEntry.Category("misc")
    @ConfigEntry.Gui.Tooltip()
    public boolean discordRPC = true;
    @ConfigEntry.Category("misc")
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100000)
    public int discordRPCTimeout = 15000;

    public enum CosmeticType {
        All_Cosmetics,
        No_Event_Cosmetics,
        Disabled
    }

    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}