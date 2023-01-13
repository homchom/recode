package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.mod.features.StateOverlayHandler;
import io.github.homchom.recode.mod.features.discordrpc.DFDiscordRPC;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.server.state.DFState;
import io.github.homchom.recode.server.state.DFStateDetector;
import io.github.homchom.recode.sys.player.chat.MessageGrabber;

public class LegacyChangeStateEvent {
    public LegacyChangeStateEvent() {
        DFStateDetector.INSTANCE.register(this::run);
    }

    private void run(DFState newState) {
        StreamerModeHandler.handleStateChange(newState);

        if (newState == null) MessageGrabber.reset();

        try {
            DFDiscordRPC.getInstance().update(newState);
            StateOverlayHandler.setState(newState);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
