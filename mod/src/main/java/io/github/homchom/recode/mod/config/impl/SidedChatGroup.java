package io.github.homchom.recode.mod.config.impl;

import io.github.homchom.recode.mod.config.ConfigSounds;
import io.github.homchom.recode.mod.config.structure.ConfigGroup;
import io.github.homchom.recode.mod.config.structure.ConfigSubGroup;
import io.github.homchom.recode.mod.config.types.EnumSetting;
import io.github.homchom.recode.mod.config.types.IntegerSetting;
import io.github.homchom.recode.mod.config.types.SoundSetting;
import io.github.homchom.recode.mod.config.types.StringSetting;
import io.github.homchom.recode.sys.sidedchat.ChatRule;

public class SidedChatGroup extends ConfigGroup {
    public SidedChatGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        this.register(new IntegerSetting("sidechat_width",0));

        for (ChatRule chatRule : ChatRule.getChatRules()) {
            ConfigSubGroup chatRuleSubGroup = new ConfigSubGroup(chatRule.getInternalName());

            if (chatRule.getChatRuleType() == ChatRule.ChatRuleType.CUSTOM)
                chatRuleSubGroup.register(new StringSetting("custom_filter"));

            chatRuleSubGroup.register(new EnumSetting<>(ChatRule.getChatRuleConfigSideName(chatRule), ChatRule.ChatSide.class, ChatRule.ChatSide.MAIN));
            chatRuleSubGroup.register(new SoundSetting(ChatRule.getChatRuleConfigSoundName(chatRule)).setSelected(ConfigSounds.NONE));
            this.register(chatRuleSubGroup);
        }
    }
}
