package io.github.homchom.recode.sys.sidedchat;

import com.google.common.collect.Lists;
import io.github.homchom.recode.mod.config.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChatPredicates {
    private static final String CUSTOM_WORDS_DELIMINITER = ",";

    //CUSTOM
    public static Predicate<Component> getCustomPredicate() {
        return iTextComponent -> {
            List<String> customWords = getCustomWords();

            if (customWords.isEmpty() || getCustomWordsString().trim().isEmpty()) return false; // do no checks if the input is empty

            for (String customWord : customWords) {
                if (iTextComponent.getString().contains(customWord)) return true;
            }

            return false;
        };
    }

    public static String getCustomWordsString() {
        return Config.getString("custom_filter");
    }

    private static List<String> getCustomWords() {
        return Lists.newArrayList(getCustomWordsString().split(CUSTOM_WORDS_DELIMINITER))
                .stream() // trim each word then collect
                .map(String::trim)
                .collect(Collectors.toList());
    }

    //MESSAGE
    private static final ChatPattern oldMessageChatPattern = new ChatPattern(
            new ChatPattern.ChatComponent("[", TextColor.fromLegacyFormat(ChatFormatting.DARK_RED),0),
            new ChatPattern.ChatComponent(null, TextColor.fromLegacyFormat(ChatFormatting.AQUA)),
            new ChatPattern.ChatComponent(" -> ", TextColor.fromLegacyFormat(ChatFormatting.GOLD)),
            new ChatPattern.ChatComponent(null, TextColor.fromLegacyFormat(ChatFormatting.AQUA)),
            new ChatPattern.ChatComponent("] ", TextColor.fromLegacyFormat(ChatFormatting.DARK_RED))
    );
    private static final ChatPattern oldCrossNodeMessagePattern = new ChatPattern(
            new ChatPattern.ChatComponent("[", TextColor.fromLegacyFormat(ChatFormatting.GOLD),0),
            new ChatPattern.ChatComponent(null, TextColor.fromLegacyFormat(ChatFormatting.AQUA)),
            new ChatPattern.ChatComponent(" -> ", TextColor.fromLegacyFormat(ChatFormatting.DARK_RED)),
            new ChatPattern.ChatComponent(null, TextColor.fromLegacyFormat(ChatFormatting.AQUA)),
            new ChatPattern.ChatComponent("] ", TextColor.fromLegacyFormat(ChatFormatting.GOLD))
    );

    // TODO: are both of these needed anymore? aren't messages consistently formatted?
    private static final ChatPattern messageChatPattern = new ChatPattern(
            new ChatPattern.ChatComponent("[", TextColor.fromRgb(0xff7f55),0),
            new ChatPattern.ChatComponent(null, TextColor.fromLegacyFormat(ChatFormatting.AQUA)),
            new ChatPattern.ChatComponent(" → ", TextColor.fromRgb(0xff7f55)),
            new ChatPattern.ChatComponent(null, TextColor.fromRgb(0xffd47f)),
            new ChatPattern.ChatComponent("] ", TextColor.fromRgb(0xff7f55))
    );
    private static final ChatPattern messageChatPattern2 = new ChatPattern(
            new ChatPattern.ChatComponent("[", TextColor.fromRgb(0xff7f55),0),
            new ChatPattern.ChatComponent(null, TextColor.fromRgb(0xffd47f)),
            new ChatPattern.ChatComponent(" → ", TextColor.fromRgb(0xff7f55)),
            new ChatPattern.ChatComponent(null, TextColor.fromLegacyFormat(ChatFormatting.AQUA)),
            new ChatPattern.ChatComponent("] ", TextColor.fromRgb(0xff7f55))
    );

    public static Predicate<Component> getMessagePredicate() {
        return iTextComponent -> {
            ChatPattern chatPattern = new ChatPattern(iTextComponent);
            return chatPattern.contains(messageChatPattern)         ||
                    chatPattern.contains(messageChatPattern2)       ||
                    chatPattern.contains(oldMessageChatPattern)     ||
                    chatPattern.contains(oldCrossNodeMessagePattern);
        };
    }

    //SUPPORT
    private static final ChatPattern supportChatPattern = new ChatPattern(
            new ChatPattern.ChatComponent("[SUPPORT] ", TextColor.fromLegacyFormat(ChatFormatting.BLUE),0)
    );
    public static Predicate<Component> getSupportPredicate() {
        return text -> (new ChatPattern(text)).contains(supportChatPattern);
    }

    //MOD
    private static final ChatPattern modChatPattern = new ChatPattern(
            new ChatPattern.ChatComponent("[MOD] ", TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN),0)
    );
    public static Predicate<Component> getModPredicate() {
        return text -> (new ChatPattern(text)).contains(modChatPattern);
    }

    //SESSION
    private static final ChatPattern sessionChatPattern = new ChatPattern(
            new ChatPattern.ChatComponent("*",TextColor.fromLegacyFormat(ChatFormatting.GREEN),0)
    );
    public static Predicate<Component> getSessionPredicate() {
        return text -> (new ChatPattern(text)).contains(sessionChatPattern);
    }

    //ADMIN
    private static final ChatPattern adminChatPattern = new ChatPattern(
            new ChatPattern.ChatComponent("[ADMIN] ", TextColor.fromLegacyFormat(ChatFormatting.RED),0)
    );
    public static Predicate<Component> getAdminPredicate() {
        return text -> (new ChatPattern(text)).contains(adminChatPattern);
    }
}

