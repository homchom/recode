package io.github.homchom.recode.mod.features.social.chat.message.finalizers;

import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.social.chat.message.*;

public class DebugFinalizer extends MessageFinalizer {

    @Override
    protected void receive(Message message) {
        if (Config.getBoolean("debugMode")) {
            Recode.info(message.toString());
        }
    }
}
