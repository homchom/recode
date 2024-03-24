package io.github.homchom.recode.mod.features.social.chat;

import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.sys.file.ILoader;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ConversationTimer implements ILoader {
    public static boolean isTimerOn = false;
    public static String currentConversation = null;
    public static String conversationUpdateTime = null;

    @Override
    public void load() {
        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            if (currentConversation != null && !LegacyConfig.getBoolean("automsg")) {
                currentConversation = null;
                conversationUpdateTime = null;
                return;
            }
            if (!isTimerOn || !LegacyConfig.getBoolean("automsg_timeout")) return;
            if (System.currentTimeMillis() - LegacyConfig.getLong("automsg_timeoutNumber") >= Long.parseLong(conversationUpdateTime)) {
                ChatUtil.sendMessage("Your conversation with " + currentConversation + " was inactive and ended.", ChatType.INFO_BLUE);
                currentConversation = null;
                conversationUpdateTime = null;
                isTimerOn = false;
            }
        });
    }
}
