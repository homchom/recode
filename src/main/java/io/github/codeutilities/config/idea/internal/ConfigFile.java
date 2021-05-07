package io.github.codeutilities.config.idea.internal;

import io.github.codeutilities.util.file.ILoader;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class ConfigFile implements ILoader {
    private static final FabricLoader FABRIC_LOADER = FabricLoader.getInstance();
    private Path configPath;

    @Override
    public void load() {
        this.configPath = FABRIC_LOADER.getConfigDir().resolve("codeutilities.json");

    }
}
