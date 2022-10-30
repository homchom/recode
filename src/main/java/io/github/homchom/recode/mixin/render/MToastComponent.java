package io.github.homchom.recode.mixin.render;

import io.github.homchom.recode.server.ServerTrust;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastComponent.class)
public class MToastComponent {

    @Inject(method = "addToast", at = @At("HEAD"), cancellable = true)
    public void addToast(Toast toast, CallbackInfo ci) {
        if (toast instanceof ASystemToast sysToast) {
            if (sysToast.getId() == SystemToast.SystemToastIds.UNSECURE_SERVER_WARNING
                    && ServerTrust.isServerTrusted()) {
                ci.cancel();
            }
        }
    }

}
