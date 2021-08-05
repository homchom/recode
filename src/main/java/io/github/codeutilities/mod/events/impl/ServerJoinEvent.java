package io.github.codeutilities.mod.events.impl;

import io.github.codeutilities.mod.features.StreamerModeHandler;
import io.github.codeutilities.mod.features.discordrpc.DFDiscordRPC;
import io.github.codeutilities.mod.features.discordrpc.RPCElapsedOption;
import io.github.codeutilities.sys.util.TimerUtil;
import io.github.codeutilities.sys.player.DFInfo;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ServerJoinEvent {

    public static void run(GameJoinS2CPacket packet, CallbackInfo ci) {
        if (DFInfo.isOnDF()) {
            DFDiscordRPC.setTime(RPCElapsedOption.SERVER_JOIN);

            TimerUtil.setTimeout(() -> {
                StreamerModeHandler.handleServerJoin(packet, ci);
            }, 2500);
        }
    }
}
