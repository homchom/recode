package io.github.codeutilities.mod.features.social.chat.message.checks;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import io.github.codeutilities.mod.features.social.chat.message.MessageType;
import io.github.codeutilities.sys.networking.State;
import io.github.codeutilities.sys.player.DFInfo;

import java.util.Collection;

public class JoinDiamondFireCheck extends MessageCheck {

    @Override
    protected MessageType getType() {
        return MessageType.JOIN_DF;
    }

    @Override
    protected boolean check(Message message, String stripped) {
        return DFInfo.currentState.getMode() == State.Mode.SPAWN && stripped.equals("◆ Welcome back to DiamondFire! ◆");
    }

    @Override
    protected void onReceive(Message message) {
        // Check if the player joined Beta
        DFInfo.isInBeta = false;
        Collection<String> lines = CodeUtilities.MC.world.getScoreboard().getKnownPlayers();
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
