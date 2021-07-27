package io.github.codeutilities.mod.mixin.render;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.math.MatrixStack;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class MSideChatHUD {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "render",at = @At("TAIL"))
    private void render(MatrixStack matrices, int tickDelta, CallbackInfo ci) {
        LOGGER.error("rendering");
    }
}
