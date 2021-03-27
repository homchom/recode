package io.github.codeutilities.mixin.render;

import io.github.codeutilities.gui.CPU_UsageText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method="renderStatusEffectOverlay", at=@At("RETURN"))
    private void renderStatusEffectOverlay(MatrixStack stack, CallbackInfo ci) {
        CPU_UsageText.onRender(stack);
    }
}
