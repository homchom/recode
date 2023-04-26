package io.github.homchom.recode.mixin.server;

import io.github.homchom.recode.event.SimpleValidated;
import io.github.homchom.recode.server.SendCommandEvent;
import io.github.homchom.recode.server.ServerTrust;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public abstract class MClientPacketListener {
	@Redirect(method = "sendCommand", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
	))
	public void interceptCommandPackets(ClientPacketListener instance, Packet<?> packet) {
		if (packet instanceof ServerboundChatCommandPacket commandPacket) {
			// TODO: should this not be a validated event?
			if (SendCommandEvent.INSTANCE.run(new SimpleValidated<>(commandPacket.command(), true))) {
				instance.send(packet);
			}
		}
	}

	@Redirect(method = "sendUnsignedCommand", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
	))
	public void interceptUnsignedCommandPackets(ClientPacketListener instance, Packet<?> packet) {
		interceptCommandPackets(instance, packet);
	}

	@Redirect(method = "handleServerData", at = @At(value = "INVOKE", target =
		"Lnet/minecraft/client/gui/components/toasts/ToastComponent;addToast(Lnet/minecraft/client/gui/components/toasts/Toast;)V"))
	public void hideSecureChatToastIfTrusted(ToastComponent instance, Toast toast) {
		if (!ServerTrust.isServerTrusted()) instance.addToast(toast);
	}
}
