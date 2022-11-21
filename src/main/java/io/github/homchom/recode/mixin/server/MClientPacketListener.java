package io.github.homchom.recode.mixin.server;

import io.github.homchom.recode.server.ServerTrust;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public class MClientPacketListener {
	@Redirect(method = "handleServerData", at = @At(value = "INVOKE", target =
		"Lnet/minecraft/client/gui/components/toasts/ToastComponent;addToast(Lnet/minecraft/client/gui/components/toasts/Toast;)V"))
	public void hideSecureChatToastIfTrusted(ToastComponent instance, Toast toast) {
		if (!ServerTrust.isServerTrusted()) instance.addToast(toast);
	}
}
