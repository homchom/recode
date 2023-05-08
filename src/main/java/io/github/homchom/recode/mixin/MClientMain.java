package io.github.homchom.recode.mixin;

import io.github.homchom.recode.render.RenderThreadContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Main.class)
public class MClientMain {
    @Redirect(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;run()V"))
    private static void runInCoroutine(Minecraft mc) throws InterruptedException {
        BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE, (scope, continuation) -> {
            RenderThreadContext.INSTANCE.init(scope.getCoroutineContext());
            mc.run();
            return null;
        });
    }
}
