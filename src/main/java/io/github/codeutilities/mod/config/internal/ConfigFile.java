package io.github.codeutilities.mod.config.internal;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.structure.ConfigGroup;
import io.github.codeutilities.mod.config.structure.ConfigManager;
import io.github.codeutilities.mod.config.structure.ConfigSetting;
import io.github.codeutilities.mod.config.structure.ConfigSubGroup;
import io.github.codeutilities.sys.file.ILoader;
import io.github.codeutilities.sys.file.ISave;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.Optional;

public class ConfigFile implements ILoader, ISave {
    private static final FabricLoader FABRIC_LOADER = FabricLoader.getInstance();
    private static ConfigFile instance;

    private Path configPath;
    private ConfigInstruction configInstruction;

    public ConfigFile() {
        instance = this;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void load() {
        this.configPath = FABRIC_LOADER.getConfigDir().resolve("codeutilities.json");
        File file = configPath.toFile();
        JsonObject jsonObject = null;

        if (!file.exists()) {
            try {
                jsonObject = new JsonObject();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject == null) {
            try {
                JsonReader reader = new JsonReader(new FileReader(file));
                jsonObject = CodeUtilities.JSON_PARSER.parse(reader).getAsJsonObject();
            } catch (FileNotFoundException | RuntimeException e) {
                e.printStackTrace();
            }
        }
        // Deserialize all the values from the config
        this.configInstruction = CodeUtilities.GSON.fromJson(jsonObject, ConfigInstruction.class);
    }

    @Override
    public void save() {
        ConfigInstruction instruction = new ConfigInstruction();

        // Getting all the settings
        for (ConfigGroup group : ConfigManager.getInstance().getRegistered()) {
            for (ConfigSetting<?> setting : group.getSettings()) {
                Optional<String> keyName = setting.getKeyName();
                instruction.put(keyName.orElseGet(setting::getCustomKey), setting);
            }
            for (ConfigSubGroup configSubGroup : group.getRegistered()) {
                for (ConfigSetting<?> configSetting : configSubGroup.getRegistered()) {
                    Optional<String> keyName = configSetting.getKeyName();
                    instruction.put(keyName.orElseGet(configSetting::getCustomKey), configSetting);
                }
            }
        }

        try {
            FileWriter configWriter = new FileWriter(this.configPath.toFile());
            configWriter.write(CodeUtilities.GSON.toJson(instruction));
            configWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigInstruction getConfigInstruction() {
        return configInstruction;
    }

    public void setConfigInstruction(ConfigInstruction configInstruction) {
        this.configInstruction = configInstruction;
    }

    public Path getConfigPath() {
        return configPath;
    }

    public static ConfigFile getInstance() {
        return instance;
    }
}
