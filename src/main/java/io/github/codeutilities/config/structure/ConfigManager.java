package io.github.codeutilities.config.structure;

import io.github.codeutilities.commands.sys.IManager;
import io.github.codeutilities.config.config.*;
import io.github.codeutilities.config.internal.ConfigFile;
import io.github.codeutilities.config.internal.ConfigInstruction;
import io.github.codeutilities.config.types.*;
import io.github.codeutilities.config.types.hud.PositionSetting;
import io.github.codeutilities.config.types.list.ListSetting;
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

    private void readInstruction(ConfigInstruction instruction) {
        if (instruction.isEmpty()) {
            return;
        }
        // Update the values
        for (Map.Entry<String, ConfigSetting<?>> entry : instruction.getSettingMap().entrySet()) {
            String key = entry.getKey();
            ConfigSetting<?> instructionSetting = entry.getValue();
            ConfigSetting<?> configSetting = find(key);

            // This is only for lists
            if (configSetting.isList()) {
                ListSetting<?> listSetting = configSetting.cast();
                if (listSetting.isString() && instructionSetting.isString()) {
                    StringListSetting stringListSetting = listSetting.cast();

                    StringSetting stringSetting = instructionSetting.cast();
                    stringListSetting.setSelected(stringSetting.getValue());
                }
                continue;
            }

            // More advanced settings
            if (configSetting.isAdvanced()) {

                // Hud positions
                if (configSetting instanceof PositionSetting) {
                    PositionSetting setting = configSetting.cast();
                    PositionSetting cast = instructionSetting.cast();
                    setting.setValue(cast.getValue());
                    continue;
                }
            }

            // Primitives
            if (configSetting.isString()) {
                StringSetting setting = configSetting.cast();
                StringSetting cast = instructionSetting.cast();
                setting.setValue(cast.getValue());
                continue;
            }
            if (configSetting.isInteger()) {
                IntegerSetting setting = configSetting.cast();
                IntegerSetting cast = instructionSetting.cast();
                setting.setValue(cast.getValue());
                continue;
            }
            if (configSetting.isDouble()) {
                DoubleSetting setting = configSetting.cast();
                DoubleSetting cast = instructionSetting.cast();
                setting.setValue(cast.getValue());
                continue;
            }
            if (configSetting.isFloat()) {
                FloatSetting setting = configSetting.cast();
                FloatSetting cast = instructionSetting.cast();
                setting.setValue(cast.getValue());
                continue;
            }
            if (configSetting.isLong()) {
                LongSetting setting = configSetting.cast();
                LongSetting cast = instructionSetting.cast();
                setting.setValue(cast.getValue());
                continue;
            }
            if (configSetting.isBoolean()) {
                BooleanSetting setting = configSetting.cast();
                BooleanSetting cast = instructionSetting.cast();
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
        for (ConfigGroup group : groups) {
            for (ConfigSetting<?> setting : group.getSettings()) {
                if (setting.getKey().equalsIgnoreCase(key)) {
                    return setting;
                }
            }
            for (ConfigSubGroup configSubGroup : group.getRegistered()) {
                for (ConfigSetting<?> configSetting : configSubGroup.getRegistered()) {
                    if (configSetting.getKey().equalsIgnoreCase(key)) {
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
