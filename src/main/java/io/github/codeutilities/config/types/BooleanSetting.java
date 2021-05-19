package io.github.codeutilities.config.types;

import io.github.codeutilities.CodeInitializer;
import io.github.codeutilities.config.structure.ConfigSetting;
import io.github.codeutilities.features.external.AudioHandler;

public class BooleanSetting extends ConfigSetting<Boolean> {
    public BooleanSetting() {
    }

    public BooleanSetting(String key, Boolean defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public BooleanSetting setValue(Boolean value) {
        if (this.value != value) {
            this.value = value;
            if (getCustomKey().equals("audio")) {
                AudioHandler instance = AudioHandler.getInstance();
                if (instance != null) instance.setActive(value);
                else CodeInitializer.getInstance().addIf(new AudioHandler(), value);
            }
        }
        return this;
    }
}
