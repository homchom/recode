package io.github.homchom.recode.mod.features.commands.recode;

public class LegacyFeature {
    private final String name;
    private final String description;

    public LegacyFeature(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
