package io.github.homchom.recode.mod.config.menu;

import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.internal.ITranslatable;
import io.github.homchom.recode.mod.config.structure.*;
import io.github.homchom.recode.mod.config.types.*;
import io.github.homchom.recode.mod.config.types.list.*;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.impl.builders.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;

import java.util.List;

public class ConfigScreen implements ITranslatable {
    private static final ConfigManager CONFIG = ConfigManager.getInstance();

    private static final String PREFIX = "config.recode.";
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

        String playerUUID = Minecraft.getInstance().getUser().getUuid();

        // Optimized loop
        for (ConfigGroup group : groups) {
            if (!((playerUUID.equals(Recode.JEREMASTER_UUID) ||
                    playerUUID.equals(Recode.JEREMASTER_UUID.replaceAll("-", ""))) ||
                    (playerUUID.equals(Recode.RYANLAND_UUID) ||
                            playerUUID.equals(Recode.RYANLAND_UUID.replaceAll("-", ""))))
            && group.getName().equals("streamer")) {
                continue;
            }

                // Category
            String groupName = group.getName();

            // Group translation
            Component groupTranslation;
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
                Component keyTranslation;
                if (groupSetting.getRawKey().isPresent()) {
                    keyTranslation = groupSetting.getRawKey().get();
                } else {
                    keyTranslation = ITranslatable.get(KEY_TEXT + settingKey);
                }

                Component tooltipTranslation;
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

                Component groupKey;
                if (subGroup.getRawKey().isPresent()) {
                    groupKey = subGroup.getRawKey().get();
                } else {
                    groupKey = ITranslatable
                        .get(SUB_CATEGORY_TEXT + groupName + "_" + subGroupName);
                }

                Component groupTooltip;
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

                    Component keyTranslation;
                    if (configSetting.getRawKey().isPresent()) {
                        keyTranslation = configSetting.getRawKey().get();
                    } else {
                        keyTranslation = ITranslatable.get(KEY_TEXT + settingKey);
                    }

                    Component tooltipTranslation;
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

    private static AbstractConfigListEntry<?> getEntry(ConfigEntryBuilder builder, ConfigSetting<?> configSetting, Component title, Component tooltip) {

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
        if (configSetting.isText()) {
            return builder.startTextDescription(title)
                    .build();
        }
        if (configSetting.isEnum()) {
            EnumSetting<?> setting = configSetting.cast();

            return setupEnumSelector(builder,title,setting)
                    .setTooltip(tooltip)
                    .build();
        }

        return null;
    }

    private static <E extends Enum<E> & IConfigEnum> EnumSelectorBuilder<E> setupEnumSelector(ConfigEntryBuilder builder, Component title, EnumSetting<E> enumList) {
        return builder
                .startEnumSelector(title, enumList.getEnumClass(), enumList.getValue())
                .setEnumNameProvider(ConfigScreen::getEnumName)
                .setDefaultValue(enumList.getValue())
                .setSaveConsumer(enumList::setValue);
    }

    private static Component getEnumName(Enum<?> anEnum) {
        if (!(anEnum instanceof IConfigEnum)) {
            throw new IllegalStateException("Enum must implement IConfigEnum");
        }

        String key = "." + anEnum.toString().toLowerCase();
        return ITranslatable.get(KEY_TEXT + ((IConfigEnum) anEnum).getKey() + key);
    }

    private static Component getTitle(Component origin) {
        Style style = origin.getStyle();
        style.withColor(TextColor.fromLegacyFormat(ChatFormatting.YELLOW));
        MutableComponent copy = origin.plainCopy();
        copy.setStyle(style);

        return origin;
    }

    private static Component getTooltip(Component title, Component origin) {
        MutableComponent titleText = title.plainCopy();
        titleText.append(new TextComponent("\n\n"));

        TextComponent description = new TextComponent("» ");
        Style style = description.getStyle();
        style.withColor(TextColor.fromLegacyFormat(ChatFormatting.AQUA));
        description.setStyle(style);

        MutableComponent copy = origin.plainCopy();
        Style style1 = copy.getStyle();
        style1.withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY));
        copy.setStyle(style1);
        description.append(copy);

        titleText.append(description);
        return titleText;
    }
}
