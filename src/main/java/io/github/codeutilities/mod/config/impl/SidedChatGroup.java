package io.github.codeutilities.mod.config.impl;

import io.github.codeutilities.mod.config.structure.ConfigGroup;
import io.github.codeutilities.mod.config.structure.ConfigSubGroup;
import io.github.codeutilities.mod.config.types.EnumSetting;
import io.github.codeutilities.mod.config.types.IntegerSetting;
import io.github.codeutilities.mod.config.types.StringSetting;
import io.github.codeutilities.sys.sidedchat.ChatRule;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
            chatRuleSubGroup.register(new EnumSetting<>(ChatRule.getChatRuleConfigSoundName(chatRule), ChatRule.ChatSound.class, ChatRule.ChatSound.NONE));
            this.register(chatRuleSubGroup);
        }
    }
}
