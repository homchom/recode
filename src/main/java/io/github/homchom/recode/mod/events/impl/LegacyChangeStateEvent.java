package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.mod.features.StateOverlayHandler;
import io.github.homchom.recode.mod.features.discordrpc.DFDiscordRPC;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.server.ChangeDFStateEvent;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.chat.MessageGrabber;
import kotlin.Unit;

public class LegacyChangeStateEvent {
    public LegacyChangeStateEvent() {
        ChangeDFStateEvent.INSTANCE.register((context, __) -> {
            run(context.getNew(), context.getOld());
            return Unit.INSTANCE;
        });
    }

    private void run(LegacyState newState, LegacyState oldState) {
        StreamerModeHandler.handleStateChange(oldState, newState);

        if (newState.mode == LegacyState.Mode.OFFLINE) {
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
