package io.github.homchom.recode.mod.features.social.chat.message.finalizers;

import io.github.homchom.recode.Logging;
import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessage;
import io.github.homchom.recode.mod.features.social.chat.message.MessageFinalizer;
import net.minecraft.network.chat.Component;

public class DebugFinalizer extends MessageFinalizer {

    @Override
    protected void receive(LegacyMessage message) {
        if (LegacyConfig.getBoolean("debugMode")) {
            Logging.logInfo(Component.Serializer.toJson(message.getText()));
        }
    }
}
