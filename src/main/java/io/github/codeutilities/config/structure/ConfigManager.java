package io.github.codeutilities.config.structure;

import io.github.codeutilities.commands.sys.IManager;
import io.github.codeutilities.config.config.*;
import io.github.codeutilities.config.internal.ConfigFile;
import io.github.codeutilities.config.internal.ConfigInstruction;
import io.github.codeutilities.config.types.*;
import io.github.codeutilities.config.types.hud.PositionSetting;
import io.github.codeutilities.config.types.list.StringListSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigManager implements IManager<ConfigGroup> {
    private final List<ConfigGroup> groups = new ArrayList<>();
    private static ConfigManager instance;

    public ConfigManager() {
        instance = this;
    }

    @Override
    public void initialize() {
        // Initial settings and creation of memory placements
        //this.register(new ModulesGroup("modules"));
        this.register(new AutomationGroup("automation"));
        this.register(new CommandsGroup("commands"));
        this.register(new HidingGroup("hiding"));
        this.register(new KeybindsGroup("keybinds"));
        this.register(new HighlightGroup("highlight"));
        this.register(new ScreenGroup("screen"));
        this.register(new MiscellaneousGroup("misc"));

        // Ignore this
        this.getRegistered().forEach(IManager::initialize);

        // Getting deserialized instructions from the file
        ConfigFile configFile = ConfigFile.getInstance();
        ConfigInstruction instruction = configFile.getConfigInstruction();
        this.readInstruction(instruction);
    }

    private void readInstruction(ConfigInstruction configInstruction) {
        if (configInstruction.isEmpty()) {
            return;
        }

        // Update the settings
        for (Map.Entry<String, ConfigSetting<?>> entry : configInstruction.getSettingMap().entrySet()) {
            String key = entry.getKey();

            // Deserialized settings
            ConfigSetting<?> instruction = entry.getValue();

            // In-memory
            ConfigSetting<?> memory = this.find(key, true);

            // This is only for lists
            if (memory.isList()) {
                if (memory.isString() && instruction.isString()) {
                    StringListSetting setting = memory.cast();
                    StringSetting cast = instruction.cast();
                    setting.setSelected(cast.getValue());
                }
                continue;
            }

            // More advanced settings
            if (memory.isAdvanced()) {

                // Hud positions
                if (memory instanceof PositionSetting) {
                    PositionSetting setting = memory.cast();
                    PositionSetting cast = instruction.cast();
                    setting.setValue(cast.getValue());
                }
                continue;
            }

            // Primitives
            if (memory.isString()) {
                StringSetting setting = memory.cast();
                StringSetting cast = instruction.cast();
                setting.setValue(cast.getValue());
                continue;
            }
            if (memory.isInteger()) {
                IntegerSetting setting = memory.cast();
                IntegerSetting cast = instruction.cast();
                setting.setValue(cast.getValue());
                continue;
            }
            if (memory.isDouble()) {
                DoubleSetting setting = memory.cast();
                DoubleSetting cast = instruction.cast();
                setting.setValue(cast.getValue());
                continue;
            }
            if (memory.isFloat()) {
                FloatSetting setting = memory.cast();
                FloatSetting cast = instruction.cast();
                setting.setValue(cast.getValue());
                continue;
            }
            if (memory.isLong()) {
                LongSetting setting = memory.cast();
                LongSetting cast = instruction.cast();
                setting.setValue(cast.getValue());
                continue;
            }
            if (memory.isBoolean()) {
                BooleanSetting setting = memory.cast();
                BooleanSetting cast = instruction.cast();
                setting.setValue(cast.getValue());
            }
        }
    }

    @Override
    public void register(ConfigGroup object) {
        this.groups.add(object);
    }

    @Override
    public List<ConfigGroup> getRegistered() {
        return groups;
    }

    public ConfigSetting<?> find(String key) {
        return this.find(key, false);
    }

    public ConfigSetting<?> find(String key, boolean customKeyNames) {
        for (ConfigGroup group : groups) {
            for (ConfigSetting<?> setting : group.getSettings()) {
                String customKey = setting.getCustomKey();
                if (customKeyNames && setting.getKeyName().orElse(customKey).equalsIgnoreCase(key)) {
                    return setting;
                }
                if (customKey.equalsIgnoreCase(key)) {
                    return setting;
                }
            }
            for (ConfigSubGroup configSubGroup : group.getRegistered()) {
                for (ConfigSetting<?> configSetting : configSubGroup.getRegistered()) {
                    String customKey = configSetting.getCustomKey();
                    if (customKeyNames && configSetting.getKeyName().orElse(customKey).equalsIgnoreCase(key)) {
                        return configSetting;
                    }

                    if (customKey.equalsIgnoreCase(key)) {
                        return configSetting;
                    }
                }
            }
        }
        return null;
    }

    public static ConfigManager getInstance() {
        return instance;
    }

    public ConfigGroup findGroup(String groupName) {
        return groups.stream()
                .filter(group -> group.getName().equalsIgnoreCase(groupName))
                .findFirst().orElse(null);
    }
}
