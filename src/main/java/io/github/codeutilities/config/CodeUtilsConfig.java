package io.github.codeutilities.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

import java.io.IOException;
import java.nio.file.Files;

public class CodeUtilsConfig {

    public static Object config;

    public static Screen getScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("title.codeutils.config"));
        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.codeutils.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // ============================================================================================================================

        general.addEntry(entryBuilder.startStrField(new TranslatableText("option.codeutils.optionA"), "yea")
                .setDefaultValue("This is the default value") // Recommended: Used when user click "Reset"
                .setTooltip(new TranslatableText("This option is awesome!")) // Optional: Shown when the user hover over this option
                .build()); // Builds the option entry for cloth config

        // ============================================================================================================================

        return builder.build();
    }

    public static void cacheConfig()  {
        try {
            System.out.println(Files.readAllLines(FabricLoader.getInstance().getConfigDir().resolve("codeutilities.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        try (FileReader reader = new FileReader())
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            System.out.println(obj);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

 */
    }

    // TEMPORARY OLD ONES
    public static boolean itemApi = true;
    public static boolean autoChatLocal = false;
    public static boolean autolagslayer = false;
    public static boolean discordRPC = true;
    public static boolean cpuOnScreen = true;
    public static boolean autotime = false;
    public static int autotimeval = 69420;
    public static boolean autonightvis = false;
    public static boolean dfCommands = true;
    public static boolean autoRC = false;
    public static boolean autofly = false;
    public static boolean hideMsgMatchingRegex = false;
    public static String hideMsgRegex = "";
    public static boolean hideVarScopeMessages = false;
    public static boolean hideMutedChat = false;
    public static boolean hideSessionSpy = false;
    public static boolean hideJoinLeaveMessages = false;
    public static ConfigSounds highlightSound = ConfigSounds.ShieldBlock;
    public static boolean highlightOwnSenderSound = false;
    public static float highlightSoundVolume = 3F;
    public static String highlightPrefix = "&e";
    public static boolean highlightIgnoreSender = false;
    public static String highlightMatcher = "{name}";
    public static boolean highlight = false;
    public static boolean discordRPCShowElapsed = true;
    public static int discordRPCTimeout = 15000;
    public static int colorMaxRender = 100;
    public static int colorLines = 100;
    public static CosmeticType cosmeticType = CosmeticType.Disabled;
    public enum CosmeticType {
        Enabled(),
        No_Event_Cosmetics(),
        Disabled()
    }
    public static int headMenuMaxRender = 1000;
    public static int fsNormal = 100;
    public static int fsFast = 1000;
    public static int fsMed = 350;
    public static boolean functionProcessSearch = true;
    public static boolean variableScopeView = true;
    public static boolean quickVarScope = true;
    public static boolean chestReplacement = false;
    public static boolean f3Tps = true;
    public static boolean dfButton = true;
    public static boolean errorSound = true;
    public static int signRenderDistance = 100;

    public static Object getConfig() {
        return config;
    }
}