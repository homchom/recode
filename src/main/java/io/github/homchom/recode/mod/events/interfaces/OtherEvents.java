package io.github.homchom.recode.mod.events.interfaces;

import net.fabricmc.fabric.api.event.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;

public interface OtherEvents extends CustomEvent {
    Event<OtherEvents> TICK = EventFactory.createArrayBacked(OtherEvents.class,
            listeners -> client -> CustomEvent.makeEvent(listeners, listener -> ((OtherEvents) listener).tick(client)));

    InteractionResult tick(Minecraft client);
}
