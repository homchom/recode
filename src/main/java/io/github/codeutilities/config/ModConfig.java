package io.github.codeutilities.config;

import it.unimi.dsi.fastutil.ints.IntLists;
import me.sargunvohra.mcmods.autoconfig1u.*;
import me.sargunvohra.mcmods.autoconfig1u.annotation.*;

import java.util.Collections;
import java.util.List;

@Config(name = "codeutilities")
public class ModConfig implements ConfigData {

    // =================================================================================================

    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.Gui.CollapsibleObject
    Automation_Time automation_time = new Automation_Time();
    public static class Automation_Time implements ConfigData {
        @ConfigEntry.Gui.Tooltip()
        public boolean autotime = false;
        @ConfigEntry.Gui.Tooltip()
        public int autotimeval = 0;
    }

    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public boolean autoRC = false;

    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean autonightvis = false;

    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip()
    public boolean autofly = false;

    @ConfigEntry.Category("automation")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean autolagslayer = false;

    // =================================================================================================

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
    @ConfigEntry.Gui.CollapsibleObject
    Commands_Color commands_color = new Commands_Color();
    public static class Commands_Color implements ConfigData {
        @ConfigEntry.Gui.Tooltip()
        public int colorMaxRender = 158;
        @ConfigEntry.Gui.Tooltip()
        @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
        public int colorLines = 5;
    }

    // =================================================================================================

    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean hideJoinLeaveMessages = false;

    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    public boolean hideVarScopeMessages = false;

    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.CollapsibleObject
    Hiding_Regex hiding_regex = new Hiding_Regex();
    public static class Hiding_Regex implements ConfigData {
        @ConfigEntry.Gui.Tooltip()
        public boolean hideMsgMatchingRegex = false;
        @ConfigEntry.Gui.Tooltip()
        public String hideMsgRegex = "";
    }

    @ConfigEntry.Category("hiding")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip(count = 2)
    Hiding_Staff hiding_staff = new Hiding_Staff();
    public static class Hiding_Staff implements ConfigData {
        @ConfigEntry.Gui.Tooltip()
        public boolean hideSessionSpy = false;
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean hideMutedChat = false;
    }

    // =================================================================================================

    @ConfigEntry.Category("keybinds")
    @ConfigEntry.Gui.Tooltip()
    public boolean functionProcessSearch = true;

    @ConfigEntry.Category("keybinds")
    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.Gui.CollapsibleObject
    Keybinds_Flightspeed keybinds_flightspeed = new Keybinds_Flightspeed();
    public static class Keybinds_Flightspeed implements ConfigData {
        @ConfigEntry.Gui.Tooltip()
        public int fsNormal = 100;
        @ConfigEntry.Gui.Tooltip()
        public int fsMed = 350;
        @ConfigEntry.Gui.Tooltip()
        public int fsFast = 1000;
    }

    // =================================================================================================

    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean highlight = false;

    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean highlightIgnoreSender = false;

    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.CollapsibleObject
    Highlight_Text highlight_text = new Highlight_Text();
    public static class Highlight_Text implements ConfigData {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public String highlightMatcher = "{name}";
        @ConfigEntry.Gui.Tooltip(count = 2)
        public String highlightPrefix = "&e";
    }

    @ConfigEntry.Category("highlight")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip(count = 2)
    Highlight_Sound highlight_sound = new Highlight_Sound();
    public static class Highlight_Sound implements ConfigData {
        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        public ConfigSounds highlightSound = ConfigSounds.ShieldBlock;
        @ConfigEntry.Gui.Tooltip()
        public float highlightSoundVolume = 3F;
        @ConfigEntry.Gui.Tooltip(count = 3)
        public boolean highlightOwnSenderSound = false;
    }

    // =================================================================================================

    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.CollapsibleObject
    Screen_Rendering screen_rendering = new Screen_Rendering();
    public static class Screen_Rendering implements ConfigData {
        @ConfigEntry.Gui.Tooltip()
        public boolean chestReplacement = false;
        @ConfigEntry.Gui.Tooltip()
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        public int signRenderDistance = 100;
    }

    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    public boolean dfButton = true;

    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean variableScopeView = true;

    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean cpuOnScreen = true;

    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip()
    public boolean f3Tps = true;
    @ConfigEntry.Category("screen")
    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public CosmeticType cosmeticType = CosmeticType.All_Cosmetics;
    public enum CosmeticType {
        All_Cosmetics,
        No_Event_Cosmetics,
        Disabled
    }

    // =================================================================================================

    @ConfigEntry.Category("misc")
    @ConfigEntry.Gui.Tooltip()
    public boolean itemApi = true;

    @ConfigEntry.Category("misc")
    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean quickVarScope = true;

    @ConfigEntry.Category("misc")
    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.Gui.CollapsibleObject
    Discord_RPC discord_rpc = new Discord_RPC();
    public static class Discord_RPC implements ConfigData {
        @ConfigEntry.Category("misc")
        @ConfigEntry.Gui.Tooltip()
        public boolean discordRPC = true;
        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100000)
        public int discordRPCTimeout = 15000;
    }

    // =================================================================================================

    public static <T extends ConfigData> T getConfig(Class<T> subclass) {
        return AutoConfig.getConfigHolder(subclass).getConfig();
    }
}