package io.github.codeutilities.mod.events.impl;

import io.github.codeutilities.sys.streamer.StreamerModeHandler;
import io.github.codeutilities.sys.util.misc.TimerUtil;
import io.github.codeutilities.sys.util.networking.DFInfo;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ServerJoinEvent {

    public static void run(GameJoinS2CPacket packet, CallbackInfo ci) {
        if (DFInfo.isOnDF()) {
            TimerUtil.setTimeout(() -> {
                StreamerModeHandler.handleServerJoin(packet, ci);
            }, 2500);
        }
    }
}
