package io.github.homchom.recode.mixin;

import io.github.homchom.recode.RecodeDispatcher;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MMinecraft {
    @Inject(method = "runTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;runAllTasks()V",
            shift = At.Shift.BEFORE
    ))
    private void runRecodeTasksNormally(CallbackInfo ci) {
        RecodeDispatcher.INSTANCE.expedite(); // ensure tasks are run on runTick if not elsewhere
    }
}