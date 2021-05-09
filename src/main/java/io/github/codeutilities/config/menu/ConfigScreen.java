package io.github.codeutilities.config.menu;

import io.github.codeutilities.config.structure.ConfigGroup;
import io.github.codeutilities.config.structure.ConfigManager;
import io.github.codeutilities.config.structure.ConfigSetting;
import io.github.codeutilities.config.structure.ConfigSubGroup;
import io.github.codeutilities.config.types.*;
import io.github.codeutilities.config.types.list.ListSetting;
import io.github.codeutilities.util.translation.ITranslatable;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
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
        for (int i = 0, groupsSize = groups.size(); i < groupsSize; i++) {

            // Category
            ConfigGroup group = groups.get(i);
            String groupName = group.getName();
            ConfigCategory category = builder.getOrCreateCategory(ITranslatable.get(CATEGORY_TEXT + groupName));

            // These are group settings (the non sub-grouped ones)
            List<ConfigSetting<?>> groupSettings = group.getSettings();
            // Optimized loop
            for (int j = 0, groupSettingsSize = groupSettings.size(); j < groupSettingsSize; j++) {
                ConfigSetting<?> groupSetting = groupSettings.get(j);
                String settingKey = groupSetting.getKey();

                TranslatableText keyTranslation = ITranslatable.get(KEY_TEXT + settingKey);
                TranslatableText tooltipTranslation = ITranslatable.get(KEY_TEXT + settingKey + TOOLTIP_TEXT);
                category.addEntry(getEntry(entryBuilder, groupSetting, keyTranslation, tooltipTranslation));
            }

            List<ConfigSubGroup> subGroups = group.getRegistered();
            // Optimized loop
            for (int j = 0, subGroupsSize = subGroups.size(); j < subGroupsSize; j++) {

                // Sub Category
                ConfigSubGroup subGroup = subGroups.get(j);
                String subGroupName = subGroup.getName();
                SubCategoryBuilder subBuilder = entryBuilder.startSubCategory(ITranslatable.get(SUB_CATEGORY_TEXT + subGroupName))
                        .setExpanded(subGroup.isStartExpanded())
                        .setTooltip(ITranslatable.get(SUB_CATEGORY_TEXT + subGroupName + TOOLTIP_TEXT));

                for (ConfigSetting<?> configSetting : subGroup.getRegistered()) {
                    String settingKey = configSetting.getKey();

                    TranslatableText keyTranslation = ITranslatable.get(KEY_TEXT + settingKey);
                    TranslatableText tooltipTranslation = ITranslatable.get(KEY_TEXT + settingKey + TOOLTIP_TEXT);
                    subBuilder.add(getEntry(entryBuilder, configSetting, keyTranslation, tooltipTranslation));
                }

                // Finally add the sub group
                category.addEntry(subBuilder.build());
            }
        }
        return builder.build();
    }

    private static AbstractConfigListEntry<?> getEntry(ConfigEntryBuilder builder, ConfigSetting<?> configSetting, TranslatableText key, TranslatableText tooltip) {
        if (configSetting.isBoolean()) {
            BooleanSetting setting = configSetting.cast();
            return builder.startBooleanToggle(key, setting.getValue())
                    .setDefaultValue(setting.getDefaultValue())
                    .setTooltip(tooltip)
                    .setSaveConsumer(setting::setValue)
                    .build();
        }
        if (configSetting.isString()) {
            StringSetting setting = configSetting.cast();
            return builder.startStrField(key, setting.getValue())
                    .setDefaultValue(setting.getDefaultValue())
                    .setTooltip(tooltip)
                    .setSaveConsumer(setting::setValue)
                    .build();
        }
        if (configSetting.isInteger()) {
            IntegerSetting setting = configSetting.cast();
            return builder.startIntField(key, setting.getValue())
                    .setDefaultValue(setting.getDefaultValue())
                    .setTooltip(tooltip)
                    .setSaveConsumer(setting::setValue)
                    .build();
        }
        if (configSetting.isLong()) {
            LongSetting setting = configSetting.cast();
            return builder.startLongField(key, setting.getValue())
                    .setDefaultValue(setting.getDefaultValue())
                    .setTooltip(tooltip)
                    .setSaveConsumer(setting::setValue)
                    .build();
        }
        if (configSetting.isDouble()) {
            DoubleSetting setting = configSetting.cast();
            return builder.startDoubleField(key, setting.getValue())
                    .setDefaultValue(setting.getDefaultValue())
                    .setTooltip(tooltip)
                    .setSaveConsumer(setting::setValue)
                    .build();
        }
        if (configSetting.isFloat()) {
            FloatSetting setting = configSetting.cast();
            return builder.startFloatField(key, setting.getValue())
                    .setDefaultValue(setting.getDefaultValue())
                    .setTooltip(tooltip)
                    .setSaveConsumer(setting::setValue)
                    .build();
        }
        if (configSetting.isList()) {
            ListSetting<?> setting = configSetting.cast();
            if (setting.isString()) {
                ListSetting<String> list = setting.cast();

                List<String> strings = new ArrayList<>(list.getDefaultValue());
                String defaultValue = strings.get(0);
                strings.remove(0);

                return builder.startStringDropdownMenu(key, list.getSelected())
                        .setSelections(strings)
                        .setDefaultValue(defaultValue)
                        .setTooltip(tooltip)
                        .setSaveConsumer(list::setSelected)
                        .build();
            }
        }

        return null;
    }
}
