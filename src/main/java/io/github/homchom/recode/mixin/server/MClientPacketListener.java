package io.github.homchom.recode.mixin.server;

import net.minecraft.client.gui.components.toasts.*;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ClientPacketListener.class)
public class MClientPacketListener {
	@Redirect(method = "handleServerData", at = @At(value = "INVOKE", target =
		"Lnet/minecraft/client/gui/components/toasts/ToastComponent;addToast(Lnet/minecraft/client/gui/components/toasts/Toast;)V"))
	public void hideSecureChatToastIfTrusted(ToastComponent instance, Toast toast) {
		// do nothing
	}
}
