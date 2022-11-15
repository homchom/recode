package io.github.homchom.recode.mod.features.social.chat.message;

import io.github.homchom.recode.mod.features.social.chat.message.finalizers.*;

/**
 * Before a message is sent to the client, and after the message checks have been evaluated,
 * plus a check was accepted, all finalizers will be evaluated. The {@link MessageCheck} instance
 * accepted for this message can be retrieved using {@link LegacyMessage#getCheck()}.
 */
public abstract class MessageFinalizer {

    private static final MessageFinalizer[] finalizers = new MessageFinalizer[]{
            new StreamerModeFinalizer(),
            new DebugFinalizer(),
            new MessageGrabberFinalizer()
    };

    /**
     * Use {@link LegacyMessage#cancel()} to cancel the message
    */
    protected abstract void receive(LegacyMessage message);

    public static void run(LegacyMessage message) {
        for (MessageFinalizer finalizer : finalizers) {
            finalizer.receive(message);
        }
    }
}
