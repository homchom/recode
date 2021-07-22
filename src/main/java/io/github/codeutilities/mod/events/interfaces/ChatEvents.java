package io.github.codeutilities.mod.events.interfaces;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public interface ChatEvents extends CustomEvent {
    Event<ChatEvents> RECEIVE_MESSAGE = EventFactory.createArrayBacked(ChatEvents.class,
            listeners -> message -> CustomEvent.makeEvent(listeners, listener -> ((ChatEvents) listener).receive(message)));

    ActionResult receive(Text message);
}
