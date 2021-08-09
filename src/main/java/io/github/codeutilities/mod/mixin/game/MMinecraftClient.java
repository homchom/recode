package io.github.codeutilities.mod.mixin.game;

import io.github.codeutilities.CodeUtilities;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MMinecraftClient {

    @Inject(method = "stop", at = @At("HEAD"))
    public void stop(CallbackInfo ci) {
        CodeUtilities.onClose();
    }

}
