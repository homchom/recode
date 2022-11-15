package io.github.homchom.recode.mod.features.social.chat.message.finalizers;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.social.chat.message.*;
import net.minecraft.network.chat.Component;

public class DebugFinalizer extends MessageFinalizer {

    @Override
    protected void receive(LegacyMessage message) {
        if (Config.getBoolean("debugMode")) {
            LegacyRecode.info(Component.Serializer.toJson(message.getText()));
        }
    }
}
