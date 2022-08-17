package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.DFInfo;
import net.minecraft.network.chat.ClickEvent.Action;

public class LocateCheck extends MessageCheck {

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.LOCATE;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return stripped.contains("\nYou are currently") &&
            message.getText().getStyle().getClickEvent().getAction() == Action.RUN_COMMAND;
    }

    @Override
    public void onReceive(LegacyMessage message) {
        DFInfo.setCurrentState(LegacyState.fromLocate(message, DFInfo.currentState));
    }
}
