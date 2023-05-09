package io.github.homchom.recode.mod.features.social.chat.message.finalizers;

import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessage;
import io.github.homchom.recode.mod.features.social.chat.message.MessageFinalizer;
import io.github.homchom.recode.sys.player.chat.MessageGrabber;

public class MessageGrabberFinalizer extends MessageFinalizer {

    @Override
    protected void receive(LegacyMessage message) {
        if (MessageGrabber.isActive()) {
            MessageGrabber.supply(message);
        }
    }
}
