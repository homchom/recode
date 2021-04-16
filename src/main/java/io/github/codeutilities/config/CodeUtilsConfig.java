package io.github.codeutilities.config;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.color.MinecraftColors;
import io.github.codeutilities.util.file.FileUtil;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;
import sun.security.krb5.Config;

import javax.print.DocFlavor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class CodeUtilsConfig {

    public static JSONObject config;
    private final static ConfigEntries[] entryValues = ConfigEntries.values();

    private final static Path configPath = FabricLoader.getInstance().getConfigDir().resolve("codeutilities.json");
    private final static String configPathString = String.valueOf(configPath);

    // ============================================================================================================================
    //
    // Define config categories and entries here
    //
    // ============================================================================================================================

    public enum ConfigEntries {
        Example_String("general", "examplekey", "Default");

        final String category;
        final String key;
        final Object defaultValue;
        ConfigEntries(String category, String key, Object defaultValue) {
            this.category = category;
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public static ConfigEntries fromKey(String key) {
            for (ConfigEntries entry : values()) {
                if (key.equals(entry.key)) return entry;
            }
            return null;
        }
    }

    public enum ConfigCategories {
        General("general");

        final String category;
        ConfigCategories(String category) {
            this.category = category;
        }
    }

    // ============================================================================================================================
    //
    // ============================================================================================================================

    public static Screen getScreen(Screen parent) {
        // Init builder
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("config.title"));
        HashMap<String, ConfigCategory> categories = new HashMap<>();
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Category builder
        for (ConfigCategories category : ConfigCategories.values()) {
            categories.put(category.category, builder.getOrCreateCategory(new TranslatableText("config.category." + category.category)));
        }

        // Entry builder
        for (ConfigEntries entry : entryValues) {
            ConfigCategory category = categories.get(entry.category);

             // Boolean
             if (entry.defaultValue instanceof Boolean) {
                 category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.option." + entry.key), config.getBoolean(entry.key))
                         .setDefaultValue((Boolean) entry.defaultValue)
                         .setTooltip(new TranslatableText("config.option." + entry.key + ".tooltip"))
                         .setSaveConsumer(newValue -> config.put(entry.key, newValue))
                         .build());
             // String
             } else if (entry.defaultValue instanceof String) {
                category.addEntry(entryBuilder.startStrField(new TranslatableText("config.option." + entry.key), config.getString(entry.key))
                        .setDefaultValue((String) entry.defaultValue)
                        .setTooltip(new TranslatableText("config.option." + entry.key + ".tooltip"))
                        .setSaveConsumer(newValue -> config.put(entry.key, newValue))
                        .build());
             // Integer
             } else if (entry.defaultValue instanceof Integer) {
                 category.addEntry(entryBuilder.startIntField(new TranslatableText("config.option." + entry.key), config.getInt(entry.key))
                         .setDefaultValue((Integer) entry.defaultValue)
                         .setTooltip(new TranslatableText("config.option." + entry.key + ".tooltip"))
                         .setSaveConsumer(newValue -> config.put(entry.key, newValue))
                         .build());
            }

        }

        builder.setSavingRunnable(() -> {
           updConfig(config);
           cacheConfig();
        });

        return builder.build();
    }

    public static void cacheConfig() {
        File configFile = configPath.toFile();

        boolean success;

        // check if config file exists
        if (!configFile.exists()) {
            try {
                success = configFile.createNewFile();
                updConfig(new JSONObject());
                if (!success) CodeUtilities.log(Level.FATAL, "Error when parsing \"../.minecraft/config/codeutilities.json\"");
            } catch (IOException e) { e.printStackTrace(); }
        }

        // parse the file
        String jsonString = "";
        try { jsonString = FileUtil.readFile(configPathString, Charset.defaultCharset());
        } catch (IOException e) { e.printStackTrace(); }

        config = new JSONObject(jsonString);

        // add default values to var
        for (ConfigEntries entry : entryValues) if (!config.has(entry.key)) config.put(entry.key, entry.defaultValue);

    }

    public static void updConfig(JSONObject obj) {

        for (Iterator<String> it = obj.keys(); it.hasNext(); ) {
            String key = it.next();
            if (obj.get(key).equals(Objects.requireNonNull(ConfigEntries.fromKey(key)).defaultValue)) {
                obj.remove(key);
            }
        }

            try {
                FileWriter configWriter = new FileWriter(configPathString);
                configWriter.write(obj.toString());
                configWriter.flush();
            } catch (IOException e) { e.printStackTrace(); }

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