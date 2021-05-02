package io.github.codeutilities.config;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.file.FileUtil;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        autotime("automation", "time", "autotime", false),
        autotimeval("automation", "time", "autotimeval",0),

        autoRC("automation", null,"autoRC", false),
        autonightvis("automation", null,"autonightvis", false),
        autofly("automation", null,"autofly", false),
        autolagslayer("automation", null, "autolagslayer", false),
        autoChatLocal("automation", null, "autoChatLocal", false),

        // commands ------------------------------------------------------------------------
        dfCommands("commands", null, "dfCommands", true),
        errorSound("commands", null, "errorSound", true),
        headsEnabled("commands", "heads", "headsEnabled", false),
        headMenuMaxRender("commands", "heads", "headMenuMaxRender", 1000),

        colorMaxRender("commands", "colors", "colorMaxRender", 158),
        colorLines("commands", "colors", "colorLines", 5),
        plotCmdLoading("commands",null,"cmdLoadPlots", true),

        // hiding -------------------------------------------------------------------------
        hideJoinLeaveMessages("hiding", null, "hideJoinLeaveMessages", false),
        hideVarScopeMessages("hiding", null, "hideVarScopeMessages", false),

        hideMsgMatchingRegex("hiding", "regex", "hideMsgMatchingRegex", false),
        hideMsgRegex("hiding", "regex", "hideMsgRegex", ""),

        hideSessionSpy("hiding", "staff", "hideSessionSpy", false),
        hideMutedChat("hiding", "staff", "hideMutedChat", false),

        // keybinds -----------------------------------------------------------------------
        fsNormal("keybinds", "flightspeed", "fsNormal", 100),
        fsMed("keybinds", "flightspeed", "fsMed", 350),
        fsFast("keybinds", "flightspeed", "fsFast", 1000),

        // highlight ----------------------------------------------------------------------
        highlight("highlight", null, "highlight", false),
        highlightIgnoreSender("highlight", null, "highlightIgnoreSender", false),

        highlightMatcher("highlight", "text", "highlightMatcher", "{name}"),
        highlightPrefix("highlight", "text", "highlightPrefix", "&e"),

        highlightSound("highlight", "sound", "highlightSound", new String[]{"1", "None", "Shield Block", "Bass Drum", "Banjo", "Bass", "Bell", "Bit", "Chime", "Cow Bell", "Didgeridoo", "Flute", "Guitar", "Harp", "Pling", "Hat", "Snare", "Iron Xylophone", "Xylophone", "Experience Orb Pickup", "Item Pickup"}),
        highlightSoundVolume("highlight", "sound", "highlightSoundVolume", 3F),
        highlightOwnSenderSound("highlight", "sound", "highlightOwnSenderSound", false),

        // screen ------------------------------------------------------------------------
        chestReplacement("screen", "world_rendering", "chestReplacement", false),
        signRenderDistance("screen", "world_rendering", "signRenderDistance", 100),

        dfButton("screen", null, "dfButton", true),
        variableScopeView("screen", null, "variableScopeView", true),
        cpuOnScreen("screen", null, "cpuOnScreen", true),
        f3Tps("screen", null, "f3Tps", true),
        cosmeticsEnabled("screen", null, "cosmeticsEnabled", true),
        //previewHeadSkin("screen",null,"previewHeadSkin", false),
        loadTabStars("screen",null,"loadTabStars", true),
        // misc ----------------------------------------------------------------------------
        itemApi("misc", null, "itemApi", true),
        quickVarScope("misc", null, "quickVarScope", true),

        discordRPC("misc", "discordrpc", "discordRPC", true),
        discordRPCTimeout("misc", "discordrpc", "discordRPCTimeout", 15000),
        discordRPCShowElapsed("misc", "discordrpc", "discordRPCShowElapsed", true),

        audio("misc", "audio", "audio", true),
        audioUrl("misc", "audio", "audioUrl", "https://audio.tomoli.me/"),
        audioAlerts("misc", "audio", "audioAlerts", false);
        // --------------------------------------------------------------------------------------

        final String category;
        final String subcategory;
        final String key;
        final Object defaultValue;
        ConfigEntries(String category, String subcategory, String key, Object defaultValue) {
            this.category = category;
            this.subcategory = category + "_" + subcategory;
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

    public enum ConfigSubcategories {
        Automation_Time("automation_time", true),

        Commands_Colors("commands_colors", true),
        Commands_Heads("commands_heads", true),

        Hiding_Regex("hiding_regex", true),
        Hiding_Staff("hiding_staff", true),

        Keybinds_Flightspeed("keybinds_flightspeed", true),

        Highlight_Text("highlight_text", true),
        Highlight_Sound("highlight_sound", true),

        Screen_World_Rendering("screen_world_rendering", true),

        Misc_DiscordRPC("misc_discordrpc", true),

        Misc_Audio("misc_audio", true);

        final String subcategory;
        final boolean startExpanded;
        ConfigSubcategories(String subcategory, Boolean startExpanded) { this.subcategory = subcategory; this.startExpanded = startExpanded; }
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
        HashMap<String, SubCategoryBuilder> subcategories = new HashMap<>();
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        final String optionKeyText = "config.codeutilities.option.";
        final String optionTooltipText = ".tooltip";

        // Category builder
        for (ConfigCategories category : ConfigCategories.values()) {
            categories.put(category.category, builder.getOrCreateCategory(new TranslatableText("config.codeutilities.category." + category.category)));
        }

        // Subcategory builder
        for (ConfigSubcategories subcategory : ConfigSubcategories.values()) {
            subcategories.put(subcategory.subcategory,
                    entryBuilder.startSubCategory(new TranslatableText("config.codeutilities.subcategory." + subcategory.subcategory)).setExpanded(subcategory.startExpanded).setTooltip(new TranslatableText("config.codeutilities.subcategory." + subcategory.subcategory + optionTooltipText)));
        }

        // Entry builder
        int i = 0;
        for (ConfigEntries entry : entryValues) {
            i++;
            ConfigCategory category = categories.get(entry.category);
            SubCategoryBuilder subcategory = subcategories.get(entry.subcategory);
            SubCategoryBuilder nextSubcategory;
            if (i == entryValues.length) nextSubcategory = null; else nextSubcategory = subcategories.get(entryValues[i].subcategory);

            if (subcategory == null) {
                // Boolean
                if (entry.defaultValue instanceof Boolean) {
                    category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText(optionKeyText + entry.key), config.getBoolean(entry.key))
                            .setDefaultValue((Boolean) entry.defaultValue)
                            .setTooltip(new TranslatableText(optionKeyText + entry.key + optionTooltipText)).setSaveConsumer(newValue -> config.put(entry.key, newValue)).build());
                    // String
                } else if (entry.defaultValue instanceof String) {
                    category.addEntry(entryBuilder.startStrField(new TranslatableText(optionKeyText + entry.key), config.getString(entry.key))
                            .setDefaultValue((String) entry.defaultValue)
                            .setTooltip(new TranslatableText(optionKeyText + entry.key + optionTooltipText)).setSaveConsumer(newValue -> config.put(entry.key, newValue)).build());
                    // Integer
                } else if (entry.defaultValue instanceof Integer) {
                    category.addEntry(entryBuilder.startIntField(new TranslatableText(optionKeyText + entry.key), config.getInt(entry.key))
                            .setDefaultValue((Integer) entry.defaultValue)
                            .setTooltip(new TranslatableText(optionKeyText + entry.key + optionTooltipText)).setSaveConsumer(newValue -> config.put(entry.key, newValue)).build());
                    // Float
                } else if (entry.defaultValue instanceof Float) {
                    category.addEntry(entryBuilder.startFloatField(new TranslatableText(optionKeyText + entry.key), config.getFloat(entry.key))
                            .setDefaultValue((Float) entry.defaultValue)
                            .setTooltip(new TranslatableText(optionKeyText + entry.key + optionTooltipText)).setSaveConsumer(newValue -> config.put(entry.key, newValue)).build());
                    // String Iterable
                } else if (entry.defaultValue instanceof String[]) {
                    String[] defaultValue = (String[]) entry.defaultValue;
                    int index = Integer.parseInt(defaultValue[0]);
                    List<String> asList = new ArrayList<>(Arrays.asList(defaultValue));
                    asList.remove(0);
                    category.addEntry(entryBuilder.startStringDropdownMenu(new TranslatableText(optionKeyText + entry.key), config.getString(entry.key))
                            .setSelections(asList)
                            .setDefaultValue(asList.get(index))
                            .setTooltip(new TranslatableText(optionKeyText + entry.key + optionTooltipText)).setSaveConsumer(newValue -> config.put(entry.key, newValue)).build());
                }

                 } else {
                // Boolean
                if (entry.defaultValue instanceof Boolean) {
                    subcategory.add(entryBuilder.startBooleanToggle(new TranslatableText(optionKeyText + entry.key), config.getBoolean(entry.key))
                            .setDefaultValue((Boolean) entry.defaultValue)
                            .setTooltip(new TranslatableText(optionKeyText + entry.key + optionTooltipText)).setSaveConsumer(newValue -> config.put(entry.key, newValue)).build());
                    // String
                } else if (entry.defaultValue instanceof String) {
                    subcategory.add(entryBuilder.startStrField(new TranslatableText(optionKeyText + entry.key), config.getString(entry.key))
                            .setDefaultValue((String) entry.defaultValue)
                            .setTooltip(new TranslatableText(optionKeyText + entry.key + optionTooltipText)).setSaveConsumer(newValue -> config.put(entry.key, newValue)).build());
                    // Integer
                } else if (entry.defaultValue instanceof Integer) {
                    subcategory.add(entryBuilder.startIntField(new TranslatableText(optionKeyText + entry.key), config.getInt(entry.key))
                            .setDefaultValue((Integer) entry.defaultValue)
                            .setTooltip(new TranslatableText(optionKeyText + entry.key + optionTooltipText)).setSaveConsumer(newValue -> config.put(entry.key, newValue)).build());
                    // Float
                } else if (entry.defaultValue instanceof Float) {
                    subcategory.add(entryBuilder.startFloatField(new TranslatableText(optionKeyText + entry.key), config.getFloat(entry.key))
                            .setDefaultValue((Float) entry.defaultValue)
                            .setTooltip(new TranslatableText(optionKeyText + entry.key + optionTooltipText)).setSaveConsumer(newValue -> config.put(entry.key, newValue)).build());
                // String Iterable
                } else if (entry.defaultValue instanceof String[]) {
                    String[] defaultValue = (String[]) entry.defaultValue;
                    int index = Integer.parseInt(defaultValue[0]);
                    List<String> asList = new ArrayList<>(Arrays.asList(defaultValue));
                    asList.remove(0);
                    subcategory.add(entryBuilder.startStringDropdownMenu(new TranslatableText(optionKeyText + entry.key), config.getString(entry.key))
                            .setSelections(asList)
                            .setDefaultValue(asList.get(index))
                        .setTooltip(new TranslatableText(optionKeyText + entry.key + optionTooltipText)).setSaveConsumer(newValue -> config.put(entry.key, newValue)).build());
                }
                if (subcategory != nextSubcategory) category.addEntry(subcategory.build());
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
        for (ConfigEntries entry : entryValues) {
            if (!config.has(entry.key)) {
                if (entry.defaultValue instanceof String[]) {
                    String[] defaultValue = (String[]) entry.defaultValue;
                    int index = Integer.parseInt(defaultValue[0]);
                    List<String> asList = new ArrayList<>(Arrays.asList(defaultValue));
                    config.put(entry.key, asList.get(index + 1));
                } else config.put(entry.key, entry.defaultValue);
            }
        }

    }

    public static void updConfig(JSONObject obj) {

        for (Object objKey : obj.keySet().toArray()) {
            String key = objKey.toString();
            ConfigEntries fromKey = ConfigEntries.fromKey(key);

            boolean check = false;
            if (obj.get(key) instanceof String && fromKey.defaultValue instanceof String) {
                if (obj.getString(key).equals(fromKey.defaultValue.toString())) check = true;
            } else if (obj.get(key) instanceof Boolean) {
                if (obj.getBoolean(key) == Boolean.parseBoolean(fromKey.defaultValue.toString())) check = true;
            } else if (obj.get(key) instanceof Integer) {
                if (obj.getInt(key) == Integer.parseInt(fromKey.defaultValue.toString())) check = true;
            } else if (obj.get(key) instanceof Float) {
                if (obj.getFloat(key) == Float.parseFloat(fromKey.defaultValue.toString())) check = true;
            } else if (fromKey.defaultValue instanceof String[]) {
                String[] defaultValue = (String[]) fromKey.defaultValue;
                int index = Integer.parseInt(defaultValue[0]);
                List<String> asList = new ArrayList<>(Arrays.asList(defaultValue));
                String defaultSel = asList.get(index + 1);
                if (obj.getString(key).equals(defaultSel)) check = true;
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

}