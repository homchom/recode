package io.github.codeutilities.config.internal;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.structure.ConfigManager;
import io.github.codeutilities.util.file.ILoader;
import io.github.codeutilities.util.file.ISave;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

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
            } catch (FileNotFoundException e) {
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
        ConfigManager.getInstance().getRegistered().forEach(group -> {
            group.getSettings()
                    .forEach(setting -> instruction.put(setting.getKey(), setting));
            group.getRegistered().stream()
                    .flatMap(configSubGroup -> configSubGroup.getRegistered().stream())
                    .forEachOrdered(configSetting -> instruction.put(configSetting.getKey(), configSetting));
        });

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
