package io.github.codeutilities;

import io.github.codeutilities.util.ILoader;
import io.github.codeutilities.util.IManager;

import java.util.Arrays;

public class CodeInitializer {
    private static CodeInitializer instance;

    public CodeInitializer() {
        instance = this;
    }

    public static CodeInitializer getInstance() {
        return instance;
    }

    public void add(IManager<?> manager) {
        this.add(new IManager<?>[]{manager});
    }

    public void add(ILoader loader) {
        this.add(new ILoader[]{loader});
    }

    public void add(ILoader... loaders) {
        Arrays.stream(loaders).forEach(ILoader::load);
    }

    public void add(IManager<?>... managers) {
        Arrays.stream(managers).forEach(IManager::initialize);
    }

    public void addIf(ILoader loader, boolean b) {
        if (b) loader.load();
    }
    public void addIf(IManager<?> manager, boolean b) {
        if (b) manager.initialize();
    }

}