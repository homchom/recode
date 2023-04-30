package io.github.homchom.recode.mixin.server;

import io.github.homchom.recode.event.SimpleValidated;
import io.github.homchom.recode.game.ItemSlotUpdateEvent;
import io.github.homchom.recode.server.SendCommandEvent;
import io.github.homchom.recode.server.ServerTrust;
import io.github.homchom.recode.sys.hypercube.templates.TemplateStorageHandler;
import io.github.homchom.recode.sys.hypercube.templates.TemplateUtil;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MClientPacketListener {
	@Inject(method = "handleContainerSetSlot", at = @At("TAIL"))
	public void handleItemSlotUpdateEvent(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
		if (packet.getContainerId() == 0) {
			ItemStack stack = packet.getItem();
			if (TemplateUtil.isTemplate(stack)) {
				TemplateStorageHandler.addTemplate(stack);
			}
		}
		ItemSlotUpdateEvent.INSTANCE.runBlocking(packet);
	}

	@Redirect(method = "sendCommand", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
	))
	public void interceptCommandPackets(ClientPacketListener instance, Packet<?> packet) {
		if (packet instanceof ServerboundChatCommandPacket commandPacket) {
			// TODO: should this not be a validated event?
			if (SendCommandEvent.INSTANCE.runBlocking(new SimpleValidated<>(commandPacket.command()))) {
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
