package io.github.homchom.recode.mod.features.social.chat;

import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.file.ILoader;
import io.github.homchom.recode.sys.player.chat.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ConversationTimer implements ILoader {
    public static boolean isTimerOn = false;
    public static String currentConversation = null;
    public static String conversationUpdateTime = null;

    @Override
    public void load() {
        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            if (currentConversation != null && !Config.getBoolean("automsg")) {
                currentConversation = null;
                conversationUpdateTime = null;
                return;
            }
            if (!isTimerOn || !Config.getBoolean("automsg_timeout")) return;
            if (System.currentTimeMillis() - Config.getLong("automsg_timeoutNumber") >= Long.parseLong(conversationUpdateTime)) {
                ChatUtil.sendMessage("Your conversation with " + currentConversation + " was inactive and ended.", ChatType.INFO_BLUE);
                currentConversation = null;
                conversationUpdateTime = null;
                isTimerOn = false;
            }
        });
    }
}
