package io.github.homchom.recode.sys.sidedchat;

import com.google.common.collect.Lists;
import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.mod.config.types.IConfigEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Class that contains a rule for sorting messages. May test a text component.
 */
public class ChatRule {
    private static final List<ChatRule> chatRules = Lists.newLinkedList();

    private final String name;                   // display name
    private final String internalName;
    private final Predicate<Component> predicate;     // predicate to use
    private final ChatRuleType chatRuleType;

    public ChatRuleType getChatRuleType() {
        return chatRuleType;
    }

    public ChatSide getChatSide() {
        return LegacyConfig.getEnum(getChatRuleConfigSideName(this),ChatSide.class);
    }

    public SoundEvent getChatSound() {
        return LegacyConfig.getSound(getChatRuleConfigSoundName(this));
    }

    public String getName() {
        return name;
    }

    public String getInternalName() {
        return internalName;
    }

    public ChatRule(String name, Predicate<Component> predicate, ChatRuleType chatRuleType) {
        this.name = name;
        this.internalName = name.toLowerCase(Locale.ROOT);
        this.predicate = predicate;
        this.chatRuleType = chatRuleType;
    }

    public boolean matches(Component message) {
        return predicate.test(message);
    }

    public static Collection<ChatRule> getChatRules() {
        return chatRules;
    }

    public static ChatRule getChatRule(ChatRuleType chatRuleType) {
        return chatRules
                .stream()
                .filter(chatRule -> chatRule.chatRuleType == chatRuleType)
                .findFirst()
                .orElse(null); // the else should never run (there is a chat for every type)
    }

    public static String getChatRuleConfigSideName(ChatRule chatRule) {
        return String.format("%s.side",chatRule.getInternalName());
    }

    public static String getChatRuleConfigSoundName(ChatRule chatRule) {
        return String.format("%s.sound",chatRule.getInternalName());
    }

    // load the rules
    static {
        chatRules.add(new ChatRule("custom_chat", ChatPredicates.getCustomPredicate(), ChatRuleType.CUSTOM));
        chatRules.add(new ChatRule("messages", ChatPredicates.getMessagePredicate(), ChatRuleType.MESSAGE));
        chatRules.add(new ChatRule("support_chat", ChatPredicates.getSupportPredicate(), ChatRuleType.SUPPORT));
        chatRules.add(new ChatRule("session_chat", ChatPredicates.getSessionPredicate(), ChatRuleType.SESSION));
        chatRules.add(new ChatRule("mod_chat", ChatPredicates.getModPredicate(), ChatRuleType.MOD));
        chatRules.add(new ChatRule("admin_chat", ChatPredicates.getAdminPredicate(), ChatRuleType.ADMIN));
    }

    public enum ChatSide implements IConfigEnum {
        MAIN,
        SIDE,
        EITHER; // either is for when a rule has no preference (e.g. you wanna use a rule to specify sound only)

        @Override
        public String getKey() {
            return "chatside";
        }
    }

    public enum ChatRuleType {
        CUSTOM,
        MESSAGE,
        SUPPORT,
        SESSION,
        MOD,
        ADMIN
    }
}

