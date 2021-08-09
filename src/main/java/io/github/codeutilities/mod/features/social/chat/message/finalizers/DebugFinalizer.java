package io.github.codeutilities.mod.features.social.chat.message.finalizers;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageFinalizer;

public class DebugFinalizer extends MessageFinalizer {

    @Override
    protected void receive(Message message) {
        if (Config.getBoolean("debugMode")) {
            CodeUtilities.log(message.toString());
        }
    }
}
