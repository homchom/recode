package io.github.homchom.recode.mod.features.commands.recode;

import net.minecraft.resources.ResourceLocation;

public class Contributor {
    private final String name;
    private final int id;
    private final int contributions;
    private final String avatarUrl;
    private ResourceLocation avatar;

    public Contributor(String name, int id, int contributions, String avatarUrl) {
        this.name = name;
        this.id = id;
        this.contributions = contributions;
        this.avatarUrl = avatarUrl;
    }

    public ResourceLocation getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getContributions() {
        return contributions;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatar(ResourceLocation avatar) {
        this.avatar = avatar;
    }
}