package io.github.homchom.recode.mod.config.impl;

import io.github.homchom.recode.mod.config.structure.ConfigGroup;
import io.github.homchom.recode.mod.config.structure.ConfigSubGroup;
import io.github.homchom.recode.mod.config.types.BooleanSetting;
import io.github.homchom.recode.mod.config.types.StringSetting;
import io.github.homchom.recode.mod.config.types.TextDescription;

public class StreamerModeGroup extends ConfigGroup {
    public StreamerModeGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {

        this.register(new TextDescription("streamerDescription"));

        this.register(new BooleanSetting("streamer", false));

        ConfigSubGroup modules = new ConfigSubGroup("streamerModules");
        modules.register(new BooleanSetting("streamerAutoAdminV", true));
        modules.register(new BooleanSetting("streamerAutoChatLocal", false));
        modules.register(new BooleanSetting("streamerSpies", true));
        modules.register(new BooleanSetting("streamerHideDMs", false));
        modules.register(new BooleanSetting("streamerHidePlotAds", false));
        modules.register(new BooleanSetting("streamerHidePlotBoosts", false));
        modules.register(new BooleanSetting("streamerHideBuycraftUpdate", true));
        modules.register(new BooleanSetting("streamerHideSupport", true));
        modules.register(new BooleanSetting("streamerHideModeration", true));
        modules.register(new BooleanSetting("streamerHideAdmin", true));
        this.register(modules);

        ConfigSubGroup regex = new ConfigSubGroup("streamerRegex");
        regex.register(new BooleanSetting("streamerHideRegexEnabled", false));
        regex.register(new StringSetting("streamerHideRegex"));
        this.register(regex);

    }
}
