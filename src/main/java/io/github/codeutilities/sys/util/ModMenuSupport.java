package io.github.codeutilities.sys.util;

public class ModMenuSupport {
    public static boolean isModsButtonPresent() {
        return com.terraformersmc.modmenu.config.ModMenuConfig.MODS_BUTTON_STYLE.getValue() == com.terraformersmc.modmenu.config.ModMenuConfig.ModsButtonStyle.ICON;
    }
}
