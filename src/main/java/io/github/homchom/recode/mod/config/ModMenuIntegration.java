package io.github.homchom.recode.mod.config;

import com.terraformersmc.modmenu.api.*;
import io.github.homchom.recode.mod.config.menu.ConfigScreen;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::getScreen;
    }

}