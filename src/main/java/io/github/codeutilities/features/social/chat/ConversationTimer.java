package io.github.codeutilities.features.social.chat;

import io.github.codeutilities.config.CodeUtilsConfig;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.codeutilities.util.file.ILoader;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ConversationTimer implements ILoader {
    public static boolean isTimerOn = false;
    public static String currentConversation = null;
    public static String conversationUpdateTime = null;

    @Override
    public void load() {
        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            if (currentConversation != null && !CodeUtilsConfig.getBoolean("automsg")) {
                currentConversation = null;
                conversationUpdateTime = null;
                return;
            }
            if (!isTimerOn || !CodeUtilsConfig.getBoolean("automsg_timeout")) return;
            if (System.currentTimeMillis() - CodeUtilsConfig.getLong("automsg_timeoutNumber") >= Long.parseLong(conversationUpdateTime)) {
                ChatUtil.sendMessage("Your conversation with " + currentConversation + " was inactive and ended.", ChatType.INFO_BLUE);
                currentConversation = null;
                conversationUpdateTime = null;
                isTimerOn = false;
            }
        });
    }
}
