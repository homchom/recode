package io.github.codeutilities.sys.sidedchat;

import com.google.common.collect.Lists;
import io.github.codeutilities.mod.config.Config;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.Predicate;

/**
 * Class that contains a rule for sorting messages. May test a text component.
 */
public class ChatRule {
    private static final List<ChatRule> chatRules = Lists.newLinkedList();

    private final String name;                   // display name
    private final String internalName;
    private final Predicate<Text> predicate;     // predicate to use
    private final ChatRuleType chatRuleType;

    public ChatRuleType getChatRuleType() {
        return chatRuleType;
    }

    public ChatSide getChatSide() {
        return Config.getEnum(getChatRuleConfigSideName(this),ChatSide.class);
    }

    public ChatSound getChatSound() {
        return Config.getEnum(getChatRuleConfigSoundName(this),ChatSound.class);
    }

    public String getName() {
        return name;
    }

    public String getInternalName() {
        return internalName;
    }

    public ChatRule(String name, Predicate<Text> predicate, ChatRuleType chatRuleType) {
        this.name = name;
        this.internalName = name.toLowerCase(Locale.ROOT);
        this.predicate = predicate;
        this.chatRuleType = chatRuleType;
    }

    public boolean matches(Text message) {
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

    public enum ChatSide {
        MAIN,
        SIDE,
        EITHER; // either is for when a rule has no preference (e.g. you wanna use a rule to specify sound only)

        public ChatSide next() {
            int myIndex = Lists.newArrayList(ChatSide.values()).indexOf(this);
            myIndex++;
            if (myIndex >= ChatSide.values().length) myIndex = 0;
            return ChatSide.values()[myIndex];
        }

        public static String[] getValueNames() {
            return Arrays.stream(ChatRule.ChatSide.values()).map(ChatRule.ChatSide::toString).toArray(String[]::new);
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

    public enum ChatSound {
        NONE(null),
        BASS(SoundEvents.BLOCK_NOTE_BLOCK_BASS),
        BASS_DRUM(SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM),
        BANJO(SoundEvents.BLOCK_NOTE_BLOCK_BANJO),
        BELL(SoundEvents.BLOCK_NOTE_BLOCK_BELL),
        BIT(SoundEvents.BLOCK_NOTE_BLOCK_BIT),
        CHIME(SoundEvents.BLOCK_NOTE_BLOCK_CHIME),
        CLICK(SoundEvents.BLOCK_NOTE_BLOCK_HAT),
        COW_BELL(SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL),
        DIDGERIDOO(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO),
        FLUTE(SoundEvents.BLOCK_NOTE_BLOCK_FLUTE),
        GUITAR(SoundEvents.BLOCK_NOTE_BLOCK_GUITAR),
        HARP(SoundEvents.BLOCK_NOTE_BLOCK_HARP),
        IRON_XYLOPHONE(SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE),
        PLING(SoundEvents.BLOCK_NOTE_BLOCK_PLING),
        SNARE_DRUM(SoundEvents.BLOCK_NOTE_BLOCK_SNARE),
        XYLOPHONE(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE);

        private SoundEvent soundEvent;

        ChatSound(SoundEvent soundEvent) {
            this.soundEvent = soundEvent;
        }

        public SoundEvent getSoundEvent() {
            return soundEvent;
        }

        public ChatSound next() {
            int myIndex = Lists.newArrayList(ChatSound.values()).indexOf(this);
            myIndex++;
            if (myIndex >= ChatSound.values().length) myIndex = 0;
            return ChatSound.values()[myIndex];
        }

        public static String[] getValueNames() {
            return Arrays.stream(ChatRule.ChatSound.values()).map(ChatRule.ChatSound::toString).toArray(String[]::new);
        }
    }
}

