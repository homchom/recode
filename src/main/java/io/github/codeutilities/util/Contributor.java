package io.github.codeutilities.util;

import net.minecraft.util.Identifier;

public class Contributor {
    private final String name;
    private final int id;
    private final int contributions;
    private final String avatarUrl;
    private Identifier avatar;

    public Contributor(String name, int id, int contributions, String avatarUrl) {
        this.name = name;
        this.id = id;
        this.contributions = contributions;
        this.avatarUrl = avatarUrl;
    }

    public Identifier getAvatar() {
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

    public void setAvatar(Identifier avatar) {
        this.avatar = avatar;
    }
}