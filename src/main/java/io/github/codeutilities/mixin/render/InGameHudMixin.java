package io.github.codeutilities.mixin.render;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.gui.CPU_UsageText;
import io.github.codeutilities.util.networking.DFInfo;
import io.github.codeutilities.util.templates.SearchUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderStatusEffectOverlay", at = @At("RETURN"))
    private void renderStatusEffectOverlay(MatrixStack stack, CallbackInfo ci) {
        CPU_UsageText.onRender(stack);

        if (SearchUtil.searchType != null && SearchUtil.searchValue != null && DFInfo.isOnDF() && DFInfo.currentState == DFInfo.State.DEV) {
            MinecraftClient mc = CodeUtilities.MC;
            mc.textRenderer.drawWithShadow(stack, new LiteralText("Searching usages of " + SearchUtil.searchType.toString()).styled(style -> style.withUnderline(true)), 2, 2, 0xffffff);
            mc.textRenderer.drawWithShadow(stack, new LiteralText(SearchUtil.searchValue), 2, 12, 0xffffff);
        }
    }
}
