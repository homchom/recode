package io.github.homchom.recode.mixin.multiplayer;

import io.github.homchom.recode.multiplayer.Sender;
import io.github.homchom.recode.sys.hypercube.templates.TemplateStorageHandler;
import io.github.homchom.recode.sys.hypercube.templates.TemplateUtil;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

	// command rate limiting

	@Inject(method = {"sendChat", "sendCommand"}, at = @At("HEAD"))
	private void recordMessagesForRateLimiting(String string, CallbackInfo ci) {
		Sender.Companion.recordCommand();
	}

	@Inject(method = "sendUnsignedCommand", at = @At("HEAD"))
	private void recordUnsignedCommandsForRateLimiting(String command, CallbackInfoReturnable<Boolean> cir) {
		Sender.Companion.recordCommand();
	}
}
