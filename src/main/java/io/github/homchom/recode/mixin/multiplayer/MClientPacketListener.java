package io.github.homchom.recode.mixin.multiplayer;

import io.github.homchom.recode.event.SimpleValidated;
import io.github.homchom.recode.multiplayer.SendCommandEvent;
import io.github.homchom.recode.multiplayer.ServerStatus;
import io.github.homchom.recode.sys.hypercube.templates.TemplateStorageHandler;
import io.github.homchom.recode.sys.hypercube.templates.TemplateUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MClientPacketListener {
	@Inject(method = "handleContainerSetSlot", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V",
			shift = At.Shift.AFTER
	))
	private void handleItemSlotUpdate(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
		// TODO: move
		if (packet.getContainerId() == 0) {
			var stack = packet.getItem();
			if (TemplateUtil.isTemplate(stack)) {
				TemplateStorageHandler.addTemplate(stack);
			}
		}
	}

	@Redirect(method = "sendCommand", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
	))
	private void interceptCommandPackets(ClientPacketListener instance, Packet<?> packet) {
		if (packet instanceof ServerboundChatCommandPacket commandPacket) {
			// TODO: should this not be a validated event?
			var context = new SimpleValidated<>(commandPacket.command());
			if (SendCommandEvent.INSTANCE.run(context)) {
				instance.send(packet);
			}
		}
	}

	@Redirect(method = "sendUnsignedCommand", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
	))
	private void interceptUnsignedCommandPackets(ClientPacketListener instance, Packet<?> packet) {
		interceptCommandPackets(instance, packet);
	}

	@Redirect(method = "handleServerData", at = @At(value = "INVOKE", target =
		"Lnet/minecraft/client/gui/components/toasts/ToastComponent;addToast(Lnet/minecraft/client/gui/components/toasts/Toast;)V"))
	private void hideSecureChatToastIfTrusted(ToastComponent instance, Toast toast) {
		if (!ServerStatus.isTrusted(Minecraft.getInstance().getCurrentServer())) {
			instance.addToast(toast);
		}
	}
}
