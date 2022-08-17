package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.DFInfo;

import java.util.Collection;

public class JoinDiamondFireCheck extends MessageCheck {

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.JOIN_DF;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return DFInfo.currentState.getMode() == LegacyState.Mode.SPAWN && stripped.equals("◆ Welcome back to DiamondFire! ◆");
    }

    @Override
    public void onReceive(LegacyMessage message) {
        // Check if the player joined Beta
        DFInfo.isInBeta = false;
        Collection<String> lines = LegacyRecode.MC.level.getScoreboard().getTrackedPlayers();
        for (String line : lines) {
            try {
                if (line.startsWith("§aNode ") && (line.split(" ")[1]).equals("Beta§8")) {
                    DFInfo.isInBeta = true;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
    }
}
