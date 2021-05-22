package io.github.codeutilities.events.register;

import io.github.codeutilities.events.interfaces.ChatEvents;
import io.github.codeutilities.events.interfaces.HyperCubeEvents;
import io.github.codeutilities.features.external.DFDiscordRPC;
import io.github.codeutilities.features.social.tab.Client;
import io.github.codeutilities.util.networking.DFInfo;
import io.github.codeutilities.util.networking.State;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class ChangeStateEvent {
    public ChangeStateEvent() {
        HyperCubeEvents.CHANGE_STATE.register(this::run);
    }

    private ActionResult run(State newstate, State oldstate) {
        DFDiscordRPC.getInstance().update(newstate);
        if(Client.connected) Client.client.send("{\"content\":" + newstate.toJson() + ",\"type\":\"state\"}");
        return ActionResult.PASS;
    }
}
