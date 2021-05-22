package io.github.codeutilities.events.interfaces;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public interface OtherEvents extends CustomEvent {
    Event<OtherEvents> TICK = EventFactory.createArrayBacked(OtherEvents.class,
            listeners -> client -> CustomEvent.makeEvent(listeners, listener -> ((OtherEvents) listener).tick(client)));

    ActionResult tick(MinecraftClient client);
}
