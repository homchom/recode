package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.mod.features.StateOverlayHandler;
import io.github.homchom.recode.mod.features.discordrpc.DFDiscordRPC;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.server.DFState;
import io.github.homchom.recode.server.DFStateDetectors;
import io.github.homchom.recode.sys.player.chat.MessageGrabber;
import io.github.homchom.recode.util.Case;

public class LegacyChangeStateEvent {
    public LegacyChangeStateEvent() {
        DFStateDetectors.INSTANCE.register(this::run);
    }

    private void run(Case<? extends DFState> newState) {
        var state = newState.getContent();
        StreamerModeHandler.handleStateChange(state);

        if (state == null) MessageGrabber.reset();

        try {
            DFDiscordRPC.getInstance().update(state);
            StateOverlayHandler.setState(state);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
