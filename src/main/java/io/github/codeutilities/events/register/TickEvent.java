package io.github.codeutilities.events.register;

import io.github.codeutilities.events.interfaces.HyperCubeEvents;
import io.github.codeutilities.events.interfaces.OtherEvents;
import io.github.codeutilities.util.networking.State;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public class TickEvent {
    public TickEvent() {
        OtherEvents.TICK.register(this::run);
    }

    private ActionResult run(MinecraftClient client) {
        
        return ActionResult.PASS;
    }
}
