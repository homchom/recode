package io.github.homchom.recode.mod.events.interfaces;

import io.github.homchom.recode.mod.features.social.chat.message.Message;
import net.fabricmc.fabric.api.event.*;
import net.minecraft.world.InteractionResult;

public interface ChatEvents extends CustomEvent {
    Event<ChatEvents> RECEIVE_MESSAGE = EventFactory.createArrayBacked(ChatEvents.class,
            listeners -> message -> CustomEvent.makeEvent(listeners, listener -> ((ChatEvents) listener).receive(message)));

    InteractionResult receive(Message message);
}
