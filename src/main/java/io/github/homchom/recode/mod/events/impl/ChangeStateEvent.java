package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.mod.events.interfaces.HyperCubeEvents;
import io.github.homchom.recode.mod.features.StateOverlayHandler;
import io.github.homchom.recode.mod.features.discordrpc.DFDiscordRPC;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.sys.networking.State;
import io.github.homchom.recode.sys.player.chat.MessageGrabber;
import net.minecraft.world.InteractionResult;

public class ChangeStateEvent {
    public ChangeStateEvent() {
        HyperCubeEvents.CHANGE_STATE.register(this::run);
    }

    private InteractionResult run(State newstate, State oldstate) {
        StreamerModeHandler.handleStateChange(oldstate, newstate);

        if (newstate.mode == State.Mode.OFFLINE) {
            MessageGrabber.reset();
        }

        try{
            DFDiscordRPC.getInstance().update(newstate);
            StateOverlayHandler.setState(newstate);
        }catch(Exception e){
            e.printStackTrace();
        }
        return InteractionResult.PASS;
    }
}
