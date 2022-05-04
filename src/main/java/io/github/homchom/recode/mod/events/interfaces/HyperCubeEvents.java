package io.github.homchom.recode.mod.events.interfaces;

import io.github.homchom.recode.sys.networking.State;
import net.fabricmc.fabric.api.event.*;
import net.minecraft.world.InteractionResult;

public interface HyperCubeEvents extends CustomEvent {
    Event<HyperCubeEvents> CHANGE_STATE = EventFactory.createArrayBacked(HyperCubeEvents.class,
            listeners -> (newstate, oldstate) -> CustomEvent.makeEvent(listeners, listener -> ((HyperCubeEvents) listener).update(newstate, oldstate)));

    InteractionResult update(State newstate, State oldstate);
}
