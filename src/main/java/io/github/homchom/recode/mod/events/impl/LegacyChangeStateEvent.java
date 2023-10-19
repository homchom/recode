package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.hypercube.state.DFState;
import io.github.homchom.recode.hypercube.state.DFStateDetectors;
import io.github.homchom.recode.mod.features.StateOverlayHandler;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.sys.player.chat.MessageGrabber;
import io.github.homchom.recode.util.Case;

public class LegacyChangeStateEvent {
    public LegacyChangeStateEvent() {
        DFStateDetectors.INSTANCE.register(this::run);
    }

    private void run(Case<? extends DFState> stateCase) {
        var state = stateCase.getContent();
        StreamerModeHandler.handleStateChange(state);

        if (state == null) MessageGrabber.reset();

        try {
            StateOverlayHandler.setState(state);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
