package io.github.codeutilities.mod.features.social.chat.message.finalizers;

import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageFinalizer;
import io.github.codeutilities.sys.player.chat.MessageGrabber;

public class MessageGrabberFinalizer extends MessageFinalizer {

    @Override
    protected void receive(Message message) {
        if(MessageGrabber.isActive()) {
            MessageGrabber.supply(message);
        }
    }
}
