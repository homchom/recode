package io.github.codeutilities.events.register;

import io.github.codeutilities.events.interfaces.HyperCubeEvents;
import io.github.codeutilities.features.external.DFDiscordRPC;
import io.github.codeutilities.features.social.tab.Client;
import io.github.codeutilities.util.networking.State;
import net.minecraft.util.ActionResult;

public class ChangeStateEvent {
    public ChangeStateEvent() {
        HyperCubeEvents.CHANGE_STATE.register(this::run);
    }

    private ActionResult run(State newstate, State oldstate) {
        try{
            DFDiscordRPC.getInstance().update(newstate);
            if(Client.client.isOpen()) Client.client.send("{\"content\":" + newstate.toJson() + ",\"type\":\"state\"}");
        }catch(Exception e){
            e.printStackTrace();
        }
        return ActionResult.PASS;
    }
}
