package io.github.codeutilities.config.idea.types.hud;

import io.github.codeutilities.config.idea.structure.ConfigSetting;

public class PositionSetting extends ConfigSetting<HudData> {
    public PositionSetting(String key, HudData defaultValue) {
        super(key, defaultValue);
    }

    public int getX() {
        return this.defaultValue.getX();
    }

    public void setX(int x) {
        this.defaultValue.setX(x);
    }

    public int getY() {
        return this.defaultValue.getY();
    }

    public void setY(int y) {
        this.defaultValue.setY(y);
    }
}
