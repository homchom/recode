package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.mod.features.discordrpc.DFDiscordRPC;
import io.github.homchom.recode.mod.features.discordrpc.RPCElapsedOption;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.util.TimerUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public class LegacyJoinEvent {
    public LegacyJoinEvent() {
        ClientPlayConnectionEvents.JOIN.register(this::run);
    }

    private void run(ClientPacketListener clientPacketListener, PacketSender packetSender, Minecraft minecraft) {
        if (DFInfo.isOnDF()) {
            DFDiscordRPC.setTime(RPCElapsedOption.SERVER_JOIN);
            TimerUtil.setTimeout(StreamerModeHandler::handleServerJoin, 2500);
        }
    }
}
