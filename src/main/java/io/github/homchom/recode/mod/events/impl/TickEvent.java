package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.mod.events.interfaces.OtherEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;

public class TickEvent {
    public TickEvent() {
        OtherEvents.TICK.register(this::run);
    }

    private InteractionResult run(Minecraft client) {
        
        return InteractionResult.PASS;
    }
}
