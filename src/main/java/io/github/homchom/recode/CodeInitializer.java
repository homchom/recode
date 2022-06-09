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

    @Deprecated
    public void add(IManager<?> manager) {
        this.add(new IManager<?>[]{manager});
    }

    @Deprecated
    public void add(ILoader loader) {
        this.add(new ILoader[]{loader});
    }

    @Deprecated
    public void add(ILoader... loaders) {
        Arrays.stream(loaders).forEach(ILoader::load);
    }

    @Deprecated
    public void add(IManager<?>... managers) {
        Arrays.stream(managers).forEach(IManager::initialize);
    }

    @Deprecated
    public void addIf(ILoader loader, boolean b) {
        if (b) loader.load();
    }

    @Deprecated
    public void addIf(IManager<?> manager, boolean b) {
        if (b) manager.initialize();
    }
}