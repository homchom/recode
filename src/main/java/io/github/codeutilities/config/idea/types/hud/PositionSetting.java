package io.github.codeutilities.config.idea.types.hud;

import io.github.codeutilities.config.idea.structure.ConfigSetting;

public class PositionSetting extends ConfigSetting<HudData> {
    public PositionSetting(String key, HudData defaultValue) {
        super(key, defaultValue);
    }

    public int getX() {
        return this.value.getX();
    }

    public void setX(int x) {
        this.value.setX(x);
    }

    public int getY() {
        return this.value.getY();
    }

    public void setY(int y) {
        this.value.setY(y);
    }
}
