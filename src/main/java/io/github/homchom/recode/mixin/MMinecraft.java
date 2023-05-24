package io.github.homchom.recode.mixin;

import io.github.homchom.recode.lifecycle.QuitGameEvent;
import kotlin.Unit;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MMinecraft {
    @Inject(method = "stop", at = @At("HEAD"))
    public void quit(CallbackInfo ci) {
        QuitGameEvent.INSTANCE.runBlocking(Unit.INSTANCE);
    }
}