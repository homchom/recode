package io.github.codeutilities.mod.config.impl;

import io.github.codeutilities.mod.config.structure.ConfigGroup;
import io.github.codeutilities.mod.config.structure.ConfigSubGroup;
import io.github.codeutilities.mod.config.types.BooleanSetting;
import io.github.codeutilities.mod.config.types.StringSetting;
import io.github.codeutilities.mod.config.types.list.ListSetting;
import io.github.codeutilities.mod.config.types.list.StringListSetting;
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
//            chatRuleSubGroup.register(new ListSetting<>(
//                    String.format("%s.side",chatRule.getInternalName()), // eg support.side
//                    ChatRule.ChatSide.values()                    // uses a map of ChatSide[] -> toString
//            ).setSelected(ChatRule.ChatSide.MAIN));
            chatRuleSubGroup.register(new ListSetting<>("hello",ChatRule.ChatSide.values()).setSelected(ChatRule.ChatSide.MAIN));
//            chatRuleSubGroup.register(new StringSetting(String.format("%s.sound",chatRule.getInternalName()), ChatRule.ChatSound.NONE.name()));
            this.register(chatRuleSubGroup);
        }
    }
}
