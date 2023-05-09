package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.mod.features.StateOverlayHandler;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.server.DFState;
import io.github.homchom.recode.server.DFStateDetectors;
import io.github.homchom.recode.sys.player.chat.MessageGrabber;
import org.jetbrains.annotations.Nullable;

public class LegacyChangeStateEvent {
    public LegacyChangeStateEvent() {
        DFStateDetectors.INSTANCE.register(this::run);
    }

    private void run(@Nullable DFState state) {
        StreamerModeHandler.handleStateChange(state);

        if (state == null) MessageGrabber.reset();

        try {
            StateOverlayHandler.setState(state);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
