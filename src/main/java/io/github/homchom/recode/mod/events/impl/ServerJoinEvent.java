package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.mod.features.discordrpc.*;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.util.TimerUtil;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ServerJoinEvent {

    public static void run(ClientboundLoginPacket packet, CallbackInfo ci) {
        if (DFInfo.isOnDF()) {
            DFDiscordRPC.setTime(RPCElapsedOption.SERVER_JOIN);

            TimerUtil.setTimeout(() -> {
                StreamerModeHandler.handleServerJoin(packet, ci);
            }, 2500);
        }
    }
}
