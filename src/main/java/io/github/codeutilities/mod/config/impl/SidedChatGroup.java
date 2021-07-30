package io.github.codeutilities.mod.config.impl;

import io.github.codeutilities.mod.config.structure.ConfigGroup;
import io.github.codeutilities.mod.config.structure.ConfigSubGroup;
import io.github.codeutilities.mod.config.types.EnumSetting;
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
        for (ChatRule chatRule : ChatRule.getChatRules()) {
            ConfigSubGroup chatRuleSubGroup = new ConfigSubGroup(chatRule.getInternalName());
            chatRuleSubGroup.register(new EnumSetting<>(String.format("%s.side",chatRule.getInternalName()), ChatRule.ChatSide.class, ChatRule.ChatSide.MAIN));
            chatRuleSubGroup.register(new EnumSetting<>(String.format("%s.sound",chatRule.getInternalName()), ChatRule.ChatSound.class, ChatRule.ChatSound.NONE));
            this.register(chatRuleSubGroup);
        }
    }
}
