package io.github.homchom.recode.mod.config.menu;

import io.github.homchom.recode.config.Config;
import io.github.homchom.recode.mod.config.internal.ITranslatable;
import io.github.homchom.recode.mod.config.structure.ConfigGroup;
import io.github.homchom.recode.mod.config.structure.ConfigManager;
import io.github.homchom.recode.mod.config.structure.ConfigSetting;
import io.github.homchom.recode.mod.config.structure.ConfigSubGroup;
import io.github.homchom.recode.mod.config.types.*;
import io.github.homchom.recode.mod.config.types.list.ListSetting;
import io.github.homchom.recode.mod.config.types.list.StringListSetting;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.EnumSelectorBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.List;
import java.util.UUID;

public class ConfigScreen implements ITranslatable {
    private static final ConfigManager CONFIG = ConfigManager.getInstance();

    private static final String PREFIX = "config.recode.";
    // Default values
    private static final String CATEGORY_TEXT = PREFIX + "category.";
    private static final String SUB_CATEGORY_TEXT = PREFIX + "subcategory.";
    private static final String KEY_TEXT = PREFIX + "option.";
    private static final String TOOLTIP_TEXT = ".tooltip";

    // TODO: replace with Permission class
    private static final List<UUID> STREAMER_MODE_ALLOWED = List.of(
            new UUID(0x6c66947530264603L, 0xb3e752c97681ad3aL), // Jeremaster
            new UUID(0x4a60515152604ea9L, 0x92244be2d600dddfL), // Maximization
            new UUID(0x3134fb4da3454c5eL, 0x951397c2c951223eL), // RyanLand
            new UUID(0x3415ab289def4df4L, 0xb92917e7c63a25aeL), // tk2217
            new UUID(0x18c303d0aac74918L, 0x8fc3b3bc0c4fb4bcL)  // homchom
    );

    public static Screen getScreen(Screen parent) {

        // Builder
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(ITranslatable.get(PREFIX + "title"))
                .setSavingRunnable(Config.INSTANCE::save);

        // Entry builder
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        List<ConfigGroup> groups = CONFIG.getRegistered();

        UUID playerUUID = Minecraft.getInstance().getGameProfile().getId();

        // Optimized loop
        for (ConfigGroup group : groups) {
            if (group.getName().equals("streamer") && !STREAMER_MODE_ALLOWED.contains(playerUUID)) {
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
        titleText.append(Component.literal("\n\n"));

        MutableComponent description = Component.literal("» ");
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
