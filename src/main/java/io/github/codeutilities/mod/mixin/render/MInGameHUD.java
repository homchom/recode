package io.github.codeutilities.mod.mixin.render;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.mod.features.CPU_UsageText;
import io.github.codeutilities.mod.features.StateOverlayHandler;
import io.github.codeutilities.mod.features.commands.CodeSearcher;
import io.github.codeutilities.sys.networking.State;
import io.github.codeutilities.sys.player.DFInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MInGameHUD {
    @Inject(method = "renderStatusEffectOverlay", at = @At("RETURN"))
    private void renderStatusEffectOverlay(MatrixStack stack, CallbackInfo ci) {
        CPU_UsageText.onRender(stack);

        MinecraftClient mc = CodeUtilities.MC;
        TextRenderer tr = mc.textRenderer;

        if (CodeSearcher.searchType != null && CodeSearcher.searchValue != null && DFInfo.isOnDF() && DFInfo.currentState.getMode() == State.Mode.DEV) {

            tr.drawWithShadow(stack, new LiteralText("Searching usages of " + CodeSearcher.searchType.toString()).styled(style -> style.withUnderline(true)), 2, 2, 0xffffff);
            tr.drawWithShadow(stack, new LiteralText(CodeSearcher.searchValue), 2, 12, 0xffffff);
        }

        if(Config.getBoolean("plotInfoOverlay")) {
            StateOverlayHandler.drawStateOverlay(tr, stack);
        }
    }
}
