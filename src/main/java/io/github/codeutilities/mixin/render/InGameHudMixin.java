package io.github.codeutilities.mixin.render;

import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.gui.CPU_UsageText;
import io.github.codeutilities.util.DFInfo;
import io.github.codeutilities.util.FuncSearchUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    @Inject(method="renderStatusEffectOverlay", at=@At("RETURN"))
    private void renderStatusEffectOverlay(MatrixStack stack, CallbackInfo ci) {
        CPU_UsageText.onRender(stack);

        if(FuncSearchUtil.searchType != null && FuncSearchUtil.searchValue != null && ModConfig.getConfig().functionProcessSearch && DFInfo.isOnDF() && DFInfo.currentState == DFInfo.State.DEV) {
            mc.textRenderer.drawWithShadow(stack, new LiteralText("Searching usages of " + FuncSearchUtil.searchType.toString()).styled(style -> style.withUnderline(true)), 2, 2, 0xffffff);
            mc.textRenderer.drawWithShadow(stack, new LiteralText(FuncSearchUtil.searchValue), 2, 12, 0xffffff);
        }


    }
}
