package io.github.codeutilities.mod.events.register;

import io.github.codeutilities.mod.events.interfaces.OtherEvents;
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
