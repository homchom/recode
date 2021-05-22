package io.github.codeutilities.events.interfaces;

import io.github.codeutilities.util.networking.State;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public interface OtherEvents extends CustomEvent {
    Event<OtherEvents> TICK = EventFactory.createArrayBacked(OtherEvents.class,
            listeners -> client -> CustomEvent.makeEvent(listeners, listener -> ((OtherEvents) listener).tick(client)));

    ActionResult tick(MinecraftClient client);
}
