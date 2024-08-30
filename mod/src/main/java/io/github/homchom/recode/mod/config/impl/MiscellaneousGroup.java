package io.github.homchom.recode.mod.config.impl;

import io.github.homchom.recode.mod.config.ConfigSounds;
import io.github.homchom.recode.mod.config.internal.DestroyItemResetType;
import io.github.homchom.recode.mod.config.structure.ConfigGroup;
import io.github.homchom.recode.mod.config.structure.ConfigSubGroup;
import io.github.homchom.recode.mod.config.types.BooleanSetting;
import io.github.homchom.recode.mod.config.types.DoubleSetting;
import io.github.homchom.recode.mod.config.types.EnumSetting;
import io.github.homchom.recode.mod.config.types.SoundSetting;

public class MiscellaneousGroup extends ConfigGroup {
    public MiscellaneousGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        // Non sub-grouped
        this.register(new BooleanSetting("itemApi", true));
        this.register(new EnumSetting<>("destroyItemReset", DestroyItemResetType.class, DestroyItemResetType.OFF));
        this.register(new SoundSetting("incomingReportSound")
                .setSelected(ConfigSounds.FLUTE));
        this.register(new BooleanSetting("debugMode", false));

        // Quick Number Change
        ConfigSubGroup quickNum = new ConfigSubGroup("quicknum");
        quickNum.register(new BooleanSetting("quicknum", true));
        quickNum.register(new BooleanSetting("quicknumSound", true));
        quickNum.register(new DoubleSetting("quicknumPrimaryAmount", 1.0));
        quickNum.register(new DoubleSetting("quicknumSecondaryAmount", 10d));
        quickNum.register(new DoubleSetting("quicknumTertiaryAmount", 0.1));
        this.register(quickNum);

    }


}
