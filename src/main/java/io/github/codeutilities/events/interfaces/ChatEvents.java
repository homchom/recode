package io.github.codeutilities.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.ActionResult;

public interface EventChatMessageListener extends CustomEvent {
    Event<EventChatMessageListener> EVENT = EventFactory.createArrayBacked(EventChatMessageListener.class,
            listeners -> packet -> CustomEvent.makeEvent(listeners, listener -> ((EventChatMessageListener) listener).receiveInteract(packet)));
    ActionResult receiveInteract(GameMessageS2CPacket packet);
}
