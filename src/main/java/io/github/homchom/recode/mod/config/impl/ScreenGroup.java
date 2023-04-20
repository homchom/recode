package io.github.homchom.recode.mod.config.impl;

import io.github.homchom.recode.mod.config.structure.*;
import io.github.homchom.recode.mod.config.types.*;

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
        this.register(titleScreen);

        // Code
        ConfigSubGroup code = new ConfigSubGroup("code");
        code.register(new BooleanSetting("chestToolTip", true));
        code.register(new BooleanSetting("templatePeeking", false));
        code.register(new BooleanSetting("cpuOnScreen", true));
        code.register(new BooleanSetting("variableScopeView", true));
        code.register(new BooleanSetting("highlightVarSyntax", true));
        code.register(new BooleanSetting("showCodeblockDescription", true));
        code.register(new BooleanSetting("showParameterErrors", true));
        code.register(new BooleanSetting("previewHeadSkins", true));
        this.register(code);

        ConfigSubGroup codeSearch = new ConfigSubGroup("code_search");
        codeSearch.register(new BooleanSetting("codeSearchTracers", false));
        codeSearch.register(new BooleanSetting("codeSearchPulse", false));
        codeSearch.register(new BooleanSetting("codeSearchTwoWayPulse", false));
        this.register(codeSearch);
    }
}
