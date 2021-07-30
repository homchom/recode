package io.github.codeutilities.sys.sidedchat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.Predicate;

/**
 * Class that contains a rule for sorting messages. May test a text component.
 */
public class ChatRule {
    private static final Map<ChatRuleType, ChatRule> chatRuleMap = Maps.newEnumMap(ChatRuleType.class);

//    public static void loadFromConfig() {
//        for (ChatRuleType chatRuleType : ChatRuleType.values()) {
//            getChatRule(chatRuleType).setChatSide(Config.getChatSide(chatRuleType));
//            getChatRule(chatRuleType).setChatSound(Config.getChatSound(chatRuleType));
//        }
//    }

    private final String name;                   // display name
    private final String internalName;
    private final Predicate<Text> predicate;     // predicate to use
    private ChatSide chatSide = ChatSide.SIDE;   // the side & sound this rule sends to
    private ChatSound chatSound = ChatSound.NONE;

    public ChatSide getChatSide() {
        return chatSide;
    }

    public void setChatSide(ChatSide chatSide) {
        this.chatSide = chatSide;
    }

    public String getName() {
        return name;
    }

    public ChatSound getChatSound() {
        return chatSound;
    }

    public void setChatSound(ChatSound chatSound) {
        this.chatSound = chatSound;
    }

    public String getInternalName() {
        return internalName;
    }

    public ChatRule(String name, Predicate<Text> predicate) {
        this.name = name;
        this.internalName = name.toLowerCase(Locale.ROOT);
        this.predicate = predicate;
    }

    public boolean matches(Text message) {
        return predicate.test(message);
    }

    public void toggleChatSide() {
        chatSide = chatSide.next();
    }

    public static Collection<ChatRule> getChatRules() {
        return chatRuleMap.values();
    }

    public static ChatRule getChatRule(ChatRuleType chatRuleType) {
        return chatRuleMap.get(chatRuleType);
    }

    /**
     * Easy way to toggle a rule's chat side
     * @param chatRuleType SUPPORT, EXPERT etc
     * @return The new chat side
     */
    public static ChatSide toggleChatType(ChatRuleType chatRuleType) {
        ChatRule chatRule = chatRuleMap.get(chatRuleType);
        chatRule.setChatSide(chatRule.getChatSide().next());
        return chatRule.getChatSide();
    }

    // load the rules
    static {
        chatRuleMap.put(ChatRuleType.CUSTOM, new ChatRule("custom_chat", ChatPredicates.getCustomPredicate()));
        chatRuleMap.put(ChatRuleType.MESSAGE, new ChatRule("messages", ChatPredicates.getMessagePredicate()));
        chatRuleMap.put(ChatRuleType.SUPPORT, new ChatRule("support_chat", ChatPredicates.getSupportPredicate()));
        chatRuleMap.put(ChatRuleType.SESSION, new ChatRule("session_chat", ChatPredicates.getSessionPredicate()));
        chatRuleMap.put(ChatRuleType.MOD, new ChatRule("mod_chat", ChatPredicates.getModPredicate()));
        chatRuleMap.put(ChatRuleType.ADMIN, new ChatRule("admin_chat", ChatPredicates.getAdminPredicate()));
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

