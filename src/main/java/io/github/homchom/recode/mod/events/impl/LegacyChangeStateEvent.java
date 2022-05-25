package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.event.RecodeEvents;
import io.github.homchom.recode.mod.features.StateOverlayHandler;
import io.github.homchom.recode.mod.features.discordrpc.DFDiscordRPC;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.sys.networking.State;
import io.github.homchom.recode.sys.player.chat.MessageGrabber;
import kotlin.Unit;

public class LegacyChangeStateEvent {
    public LegacyChangeStateEvent() {
        RecodeEvents.CHANGE_DF_STATE.register((newState, oldState) -> {
            run(newState, oldState);
            return Unit.INSTANCE;
        });
    }

    private void run(State newState, State oldState) {
        StreamerModeHandler.handleStateChange(oldState, newState);

        if (newState.mode == State.Mode.OFFLINE) {
            MessageGrabber.reset();
        }

        try {
            DFDiscordRPC.getInstance().update(newState);
            StateOverlayHandler.setState(newState);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
