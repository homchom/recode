package io.github.codeutilities.mod.config.menu;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.structure.ConfigGroup;
import io.github.codeutilities.mod.config.structure.ConfigManager;
import io.github.codeutilities.mod.config.structure.ConfigSetting;
import io.github.codeutilities.mod.config.structure.ConfigSubGroup;
import io.github.codeutilities.mod.config.types.*;
import io.github.codeutilities.mod.config.types.list.ListSetting;
import io.github.codeutilities.mod.config.types.list.StringListSetting;
import io.github.codeutilities.mod.config.internal.ITranslatable;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.List;

public class ConfigScreen implements ITranslatable {
    private static final ConfigManager CONFIG = ConfigManager.getInstance();

    private static final String PREFIX = "config.codeutilities.";
    // Default values
    private static final String CATEGORY_TEXT = PREFIX + "category.";
    private static final String SUB_CATEGORY_TEXT = PREFIX + "subcategory.";
    private static final String KEY_TEXT = PREFIX + "option.";
    private static final String TOOLTIP_TEXT = ".tooltip";

    public static Screen getScreen(Screen parent) {

        // Builder
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(ITranslatable.get(PREFIX + "title"));

        // Entry builder
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        List<ConfigGroup> groups = CONFIG.getRegistered();
        // Optimized loop
        for (ConfigGroup group : groups) {

            if (!((CodeUtilities.PLAYER_UUID.equals(CodeUtilities.JEREMASTER_UUID) ||
                    CodeUtilities.PLAYER_UUID.equals(CodeUtilities.JEREMASTER_UUID.replaceAll("-", ""))) ||
                    (CodeUtilities.PLAYER_UUID.equals(CodeUtilities.RYANLAND_UUID) ||
                            CodeUtilities.PLAYER_UUID.equals(CodeUtilities.RYANLAND_UUID.replaceAll("-", ""))))
            && group.getName().equals("streamer")) {
                continue;
            }

                // Category
            String groupName = group.getName();

            // Group translation
            Text groupTranslation;
            if (group.getRawKey().isPresent()) {
                groupTranslation = group.getRawKey().get();
            } else {
                groupTranslation = ITranslatable.get(CATEGORY_TEXT + groupName);
            }
            ConfigCategory category = builder.getOrCreateCategory(groupTranslation);

            // These are group settings (the non sub-grouped ones)
            List<ConfigSetting<?>> groupSettings = group.getSettings();
            // Optimized loop
            for (ConfigSetting<?> groupSetting : groupSettings) {
                String settingKey = groupSetting.getCustomKey();

                // Get custom translations or standard ones
                Text keyTranslation;
                if (groupSetting.getRawKey().isPresent()) {
                    keyTranslation = groupSetting.getRawKey().get();
                } else {
                    keyTranslation = ITranslatable.get(KEY_TEXT + settingKey);
                }

                Text tooltipTranslation;
                if (groupSetting.getRawTooltip().isPresent()) {
                    tooltipTranslation = groupSetting.getRawTooltip().get();
                } else {
                    tooltipTranslation = ITranslatable.get(KEY_TEXT + settingKey + TOOLTIP_TEXT);
                }
                category.addEntry(
                    getEntry(entryBuilder, groupSetting, keyTranslation, tooltipTranslation));
            }

            List<ConfigSubGroup> subGroups = group.getRegistered();
            // Optimized loop
            for (ConfigSubGroup subGroup : subGroups) {

                // Sub Category
                String subGroupName = subGroup.getName();

                Text groupKey;
                if (subGroup.getRawKey().isPresent()) {
                    groupKey = subGroup.getRawKey().get();
                } else {
                    groupKey = ITranslatable
                        .get(SUB_CATEGORY_TEXT + groupName + "_" + subGroupName);
                }

                Text groupTooltip;
                if (subGroup.getRawTooltip().isPresent()) {
                    groupTooltip = subGroup.getRawTooltip().get();
                } else {
                    groupTooltip = ITranslatable
                        .get(SUB_CATEGORY_TEXT + groupName + "_" + subGroupName + TOOLTIP_TEXT);
                }

                SubCategoryBuilder subBuilder = entryBuilder.startSubCategory(groupKey)
                    .setExpanded(subGroup.isStartExpanded())
                    .setTooltip(groupTooltip);

                for (ConfigSetting<?> configSetting : subGroup.getRegistered()) {
                    String settingKey = configSetting.getCustomKey();

                    Text keyTranslation;
                    if (configSetting.getRawKey().isPresent()) {
                        keyTranslation = configSetting.getRawKey().get();
                    } else {
                        keyTranslation = ITranslatable.get(KEY_TEXT + settingKey);
                    }

                    Text tooltipTranslation;
                    if (configSetting.getRawTooltip().isPresent()) {
                        tooltipTranslation = configSetting.getRawTooltip().get();
                    } else {
                        tooltipTranslation = ITranslatable
                            .get(KEY_TEXT + settingKey + TOOLTIP_TEXT);
                    }

                    subBuilder.add(
                        getEntry(entryBuilder, configSetting, keyTranslation, tooltipTranslation));
                }

                // Finally add the sub group
                category.addEntry(subBuilder.build());
            }
        }
        return builder.build();
    }

    private static AbstractConfigListEntry<?> getEntry(ConfigEntryBuilder builder, ConfigSetting<?> configSetting, Text title, Text tooltip) {

        if (configSetting.isList()) {
            ListSetting<?> setting = configSetting.cast();
            if (setting.isString()) {
                StringListSetting list = setting.cast();

                return builder.startStringDropdownMenu(title, list.getSelected())
                        .setSelections(list.getValue())
                        .setDefaultValue(list.getSelected())
                        .setTooltip(tooltip)
                        .setSaveConsumer(list::setSelected)
                        .build();
            }
        }
        if (configSetting.isBoolean()) {
            BooleanSetting setting = configSetting.cast();
            return builder.startBooleanToggle(title, setting.getValue())
                    .setDefaultValue(setting.getDefaultValue())
                    .setTooltip(tooltip)
                    .setSaveConsumer(setting::setValue)
                    .build();
        }
        if (configSetting.isString()) {
            StringSetting setting = configSetting.cast();
            return builder.startStrField(title, setting.getValue())
                    .setDefaultValue(setting.getDefaultValue())
                    .setTooltip(tooltip)
                    .setSaveConsumer(setting::setValue)
                    .build();
        }
        if (configSetting.isInteger()) {
            IntegerSetting setting = configSetting.cast();
            return builder.startIntField(title, setting.getValue())
                    .setDefaultValue(setting.getDefaultValue())
                    .setTooltip(tooltip)
                    .setSaveConsumer(setting::setValue)
                    .build();
        }
        if (configSetting.isLong()) {
            LongSetting setting = configSetting.cast();
            return builder.startLongField(title, setting.getValue())
                    .setDefaultValue(setting.getDefaultValue())
                    .setTooltip(tooltip)
                    .setSaveConsumer(setting::setValue)
                    .build();
        }
        if (configSetting.isDouble()) {
            DoubleSetting setting = configSetting.cast();
            return builder.startDoubleField(title, setting.getValue())
                    .setDefaultValue(setting.getDefaultValue())
                    .setTooltip(tooltip)
                    .setSaveConsumer(setting::setValue)
                    .build();
        }
        if (configSetting.isFloat()) {
            FloatSetting setting = configSetting.cast();
            return builder.startFloatField(title, setting.getValue())
                    .setDefaultValue(setting.getDefaultValue())
                    .setTooltip(tooltip)
                    .setSaveConsumer(setting::setValue)
                    .build();
        }

        return null;
    }

    private static Text getTitle(Text origin) {
        Style style = origin.getStyle();
        style.withColor(TextColor.fromFormatting(Formatting.YELLOW));
        MutableText copy = origin.copy();
        copy.setStyle(style);

        return origin;
    }

    private static Text getTooltip(Text title, Text origin) {
        MutableText titleText = title.copy();
        titleText.append(new LiteralText("\n\n"));

        LiteralText description = new LiteralText("» ");
        Style style = description.getStyle();
        style.withColor(TextColor.fromFormatting(Formatting.AQUA));
        description.setStyle(style);

        MutableText copy = origin.copy();
        Style style1 = copy.getStyle();
        style1.withColor(TextColor.fromFormatting(Formatting.GRAY));
        copy.setStyle(style1);
        description.append(copy);

        titleText.append(description);
        return titleText;
    }
}
