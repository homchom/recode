package io.github.codeutilities;

import io.github.codeutilities.util.IManager;

public class CodeInitializer {
    private static CodeInitializer instance;

    public CodeInitializer() {
        instance = this;
    }

    public void initialize(IManager<?> manager) {
        this.initialize(new IManager<?>[] {manager});
    }

    public void initialize(IManager<?>... managers) {
        for (IManager<?> manager : managers) {
            manager.initialize();
        }
    }

    public static CodeInitializer getInstance() {
        return instance;
    }
}
