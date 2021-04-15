package io.github.codeutilities.config;

import io.github.codeutilities.CodeUtilities;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.Level;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

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
                .setTooltip(new TranslatableText("This option is awesome!"))// Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> {
                    // code to run when saving
                })
                .build()); // Builds the option entry for cloth config

        // ============================================================================================================================

        return builder.build();
    }

    public static void cacheConfig() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configPath = configDir.resolve("codeutilities.json");
        File configFile = configPath.toFile();

        JSONObject obj = new JSONObject();
        JSONParser parser = new JSONParser();

        boolean success;

        // check if config file exists
        if (!configFile.exists()) {
            try {
                success = configFile.createNewFile();
                FileWriter configWriter = new FileWriter(String.valueOf(configPath));
                configWriter.write(obj.toJSONString());
                configWriter.flush();
                if (!success) CodeUtilities.log(Level.FATAL, "Error when parsing \"../.minecraft/config/codeutilities.json\"");
            } catch (IOException e) { e.printStackTrace(); }
        }

        // parse the file
        try { obj = (JSONObject) parser.parse(new FileReader(String.valueOf(configPath)));
        } catch (IOException | ParseException e) { e.printStackTrace(); }

        config = obj;

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