package io.github.homchom.recode.mod.config.types.hud;

import io.github.homchom.recode.mod.config.structure.ConfigSetting;
import io.github.homchom.recode.mod.config.structure.IAdvancedSetting;

public class PositionSetting extends ConfigSetting<HudData> implements IAdvancedSetting {
    public PositionSetting() {
    }

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
