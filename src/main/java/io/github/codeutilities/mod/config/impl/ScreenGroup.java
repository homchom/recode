package io.github.codeutilities.mod.config.impl;

import io.github.codeutilities.mod.config.structure.ConfigGroup;
import io.github.codeutilities.mod.config.structure.ConfigSubGroup;
import io.github.codeutilities.mod.config.types.BooleanSetting;
import io.github.codeutilities.mod.config.types.IntegerSetting;

public class ScreenGroup extends ConfigGroup {
    public ScreenGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        this.register(new BooleanSetting("plotInfoOverlay", false));

        // World Rendering
        ConfigSubGroup worldRendering = new ConfigSubGroup("world_rendering");
        worldRendering.register(new BooleanSetting("chestReplacement", false));
        worldRendering.register(new IntegerSetting("signRenderDistance", 100));
        this.register(worldRendering);

        // Title Screen
        ConfigSubGroup titleScreen = new ConfigSubGroup("title_screen");
        titleScreen.register(new BooleanSetting("dfButton", true));
        titleScreen.register(new BooleanSetting("dfNodeButtons", false));
        this.register(titleScreen);

        // Cosmetic
        ConfigSubGroup cosmetic = new ConfigSubGroup("cosmetic");
        cosmetic.register(new BooleanSetting("loadTabStars", true));
        cosmetic.register(new BooleanSetting("cosmeticsEnabled", true));
        this.register(cosmetic);

        // Code
        ConfigSubGroup code = new ConfigSubGroup("code");
        code.register(new BooleanSetting("chestToolTip", true));
        code.register(new BooleanSetting("templatePeeking", false));
        code.register(new BooleanSetting("cpuOnScreen", true));
        code.register(new BooleanSetting("f3Tps", true));
        code.register(new BooleanSetting("variableScopeView", true));
        code.register(new BooleanSetting("highlightVarSyntax", true));
        code.register(new BooleanSetting("showCodeblockDescription", true));
        code.register(new BooleanSetting("showParameterErrors", true));
        code.register(new BooleanSetting("previewHeadSkins", true));
        this.register(code);
    }
}
