package io.github.homchom.recode.mixin.server;

import io.github.homchom.recode.server.ServerTrust;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public class MClientPacketListener {
	@Redirect(method = "handleServerData", at = @At(value = "INVOKE", target =
			"Lnet/minecraft/network/protocol/game/ClientboundServerDataPacket;enforcesSecureChat()Z"))
	public boolean enforcesSecureChatOrIsTrusted(ClientboundServerDataPacket packet) {
		return packet.enforcesSecureChat() || ServerTrust.isServerTrusted();
	}
}
