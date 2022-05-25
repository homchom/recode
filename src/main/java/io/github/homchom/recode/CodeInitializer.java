package io.github.homchom.recode;

import io.github.homchom.recode.mod.commands.IManager;
import io.github.homchom.recode.sys.file.ILoader;

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

    public void addif (ILoader loader, boolean b) {
        if (b) loader.load();
    }

    public void addif (IManager<?> manager, boolean b) {
        if (b) manager.initialize();
    }

}