package io.github.codeutilities.mod.events.interfaces;

import io.github.codeutilities.sys.util.networking.State;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface HyperCubeEvents extends CustomEvent {
    Event<HyperCubeEvents> CHANGE_STATE = EventFactory.createArrayBacked(HyperCubeEvents.class,
            listeners -> (newstate, oldstate) -> CustomEvent.makeEvent(listeners, listener -> ((HyperCubeEvents) listener).update(newstate, oldstate)));

    ActionResult update(State newstate, State oldstate);
}
