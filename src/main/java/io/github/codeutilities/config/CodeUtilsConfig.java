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
        // automation ---------------------------------------------------------------------
        autotime("automation", "autotime", false),
        autotimeval("automation", "autotimeval",0),

        autoRC("automation", "autoRC", false),
        autonightvis("automation", "autonightvis", false),
        autofly("automation", "autofly", false),
        autolagslayer("automation", "autolagslayer", false),

        // commands ------------------------------------------------------------------------
        dfCommands("commands", "dfCommands", true),
        errorSound("commands", "errorSound", true),
        headMenuMaxRender("commands", "headMenuMaxRender", 1000),

        colorMaxRender("commands", "colorMaxRender", 158),
        colorLines("commands", "colorLines", 5),

        // hiding -------------------------------------------------------------------------
        hideJoinLeaveMessages("hiding", "hideJoinLeaveMessages", false),
        hideVarScopeMessages("hiding", "hideVarScopeMessages", false),

        hideMsgMatchingRegex("hiding", "hideMsgMatchingRegex", false),
        hideMsgRegex("hiding", "hideMsgRegex", ""),

        hideSessionSpy("hiding", "hideSessionSpy", false),
        hideMutedChat("hiding", "hideMutedChat", false),

        // keybinds -----------------------------------------------------------------------
        functionProcessSearch("keybinds", "functionProcessSearch", true),

        fsNormal("keybinds", "fsNormal", 100),
        fsMed("keybinds", "fsMed", 350),
        fsFast("keybinds", "fsFast", 1000),

        // highlight ----------------------------------------------------------------------
        highlight("highlight", "highlight", false),
        highlightIgnoreSender("highlight", "highlightIgnoreSender", false),

        highlightMatcher("highlight", "highlightMatcher", "{name}"),
        highlightPrefix("highlight", "highlightPrefix", "&e"),

        highlightSound("highlight", "highlightSound", null),
        highlightSoundVolume("highlight", "highlightSoundVolume", 3F),
        highlightOwnSenderSound("highlight", "highlightOwnSenderSound", false),

        // screen ------------------------------------------------------------------------
        chestReplacement("screen", "chestReplacement", false),
        signRenderDistance("screen", "signRenderDistance", 100),

        dfButton("screen", "dfButton", true),
        variableScopeView("screen", "variableScopeView", true),
        cpuOnScreen("screen", "cpuOnScreen", true),
        f3Tps("screen", "f3Tps", true),
        cosmeticType("screen", "cosmeticType", null),

        // misc ----------------------------------------------------------------------------
        itemApi("misc", "itemApi", true),
        quickVarScope("misc", "quickVarScope", true),

        discordRPC("misc", "discordRPC", true),
        discordRPCTimeout("misc", "discordRPCTimeout", 15000),
        discordRPCShowElapsed("misc", "discordRPCShowElapsed", true);

        // --------------------------------------------------------------------------------------

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
        Automation("automation"),
        Commands("commands"),
        Hiding("hiding"),
        Keybinds("keybinds"),
        Highlight("highlight"),
        Screen("screen"),
        Misc("misc");

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
                .setTitle(new TranslatableText("config.codeutilities.title"));
        HashMap<String, ConfigCategory> categories = new HashMap<>();
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Category builder
        for (ConfigCategories category : ConfigCategories.values()) {
            categories.put(category.category, builder.getOrCreateCategory(new TranslatableText("config.codeutilities.category." + category.category)));
        }

        System.out.println("BUILDING CONFIG " + config);

        // Entry builder
        for (ConfigEntries entry : entryValues) {
            ConfigCategory category = categories.get(entry.category);

             // Boolean
             if (entry.defaultValue instanceof Boolean) {
                 category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.codeutilities.option." + entry.key), config.getBoolean(entry.key))
                         .setDefaultValue((Boolean) entry.defaultValue)
                         .setTooltip(new TranslatableText("config.codeutilities.option." + entry.key + ".tooltip"))
                         .setSaveConsumer(newValue -> config.put(entry.key, newValue))
                         .build());
             // String
             } else if (entry.defaultValue instanceof String) {
                category.addEntry(entryBuilder.startStrField(new TranslatableText("config.codeutilities.option." + entry.key), config.getString(entry.key))
                        .setDefaultValue((String) entry.defaultValue)
                        .setTooltip(new TranslatableText("config.codeutilties.option." + entry.key + ".tooltip"))
                        .setSaveConsumer(newValue -> config.put(entry.key, newValue))
                        .build());
             // Integer
             } else if (entry.defaultValue instanceof Integer) {
                 category.addEntry(entryBuilder.startIntField(new TranslatableText("config.codeutilities.option." + entry.key), config.getInt(entry.key))
                         .setDefaultValue((Integer) entry.defaultValue)
                         .setTooltip(new TranslatableText("config.codeutilities.option." + entry.key + ".tooltip"))
                         .setSaveConsumer(newValue -> config.put(entry.key, newValue))
                         .build());
             // Float
             } else if (entry.defaultValue instanceof Float) {
                 category.addEntry(entryBuilder.startFloatField(new TranslatableText("config.codeutilities.option." + entry.key), config.getFloat(entry.key))
                         .setDefaultValue((Float) entry.defaultValue)
                         .setTooltip(new TranslatableText("config.codeutilities.option." + entry.key + ".tooltip"))
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

        System.out.println("CONFIG " + obj);

        for (Object objKey : obj.keySet().toArray()) {
            String key = objKey.toString();

            boolean check = false;
            if (obj.get(key) instanceof String) {
                if (obj.getString(key).equals(ConfigEntries.fromKey(key).defaultValue.toString())) check = true;
            } else if (obj.get(key) instanceof Boolean) {
                if (obj.getBoolean(key) == Boolean.parseBoolean(ConfigEntries.fromKey(key).defaultValue.toString())) check = true;
            } else if (obj.get(key) instanceof Integer) {
                if (obj.getInt(key) == Integer.parseInt(ConfigEntries.fromKey(key).defaultValue.toString())) check = true;
            } else if (obj.get(key) instanceof Float) {
                if (obj.getFloat(key) == Float.parseFloat(ConfigEntries.fromKey(key).defaultValue.toString())) check = true;
            }

            if (check) obj.remove(key);
        }

        try {
            FileWriter configWriter = new FileWriter(configPathString);
            configWriter.write(obj.toString());
            configWriter.flush();
        } catch (IOException e) { e.printStackTrace(); }

    }

    public static boolean getBool(String key) {
        return config.getBoolean(key);
    }

    public static String getStr(String key) {
        return config.getString(key);
    }

    public static int getInt(String key) {
        return config.getInt(key);
    }

    public static float getFloat(String key) {
        return config.getFloat(key);
    }

    // ---------------------------------------------------------------------------

    public static ConfigSounds getConfigSounds(String key) {
        return ConfigSounds.None;
    }

    public static CosmeticType getCosmeticType(String key) {
        return CosmeticType.Enabled;
    }

    public enum CosmeticType {
        Enabled,
        No_Event_Cosmetics,
        Disabled;
    }
}