package io.github.homchom.recode.mod.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.*;
import io.github.homchom.recode.mod.features.commands.CodeSearcher;
import io.github.homchom.recode.sys.networking.State;
import io.github.homchom.recode.sys.player.DFInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MInGameHUD {
    @Inject(method = "renderEffects", at = @At("RETURN"))
    private void renderEffects(PoseStack stack, CallbackInfo ci) {
        LagslayerHUD.onRender(stack);

        Minecraft mc = Recode.MC;
        Font tr = mc.font;

        if (CodeSearcher.searchType != null && CodeSearcher.searchValue != null && DFInfo.isOnDF() && DFInfo.currentState.getMode() == State.Mode.DEV) {

            tr.drawShadow(stack, new TextComponent("Searching usages of " + CodeSearcher.searchType.toString()).withStyle(style -> style.withUnderlined(true)), 2, 2, 0xffffff);
            tr.drawShadow(stack, new TextComponent(CodeSearcher.searchValue), 2, 12, 0xffffff);
        }

        if (Config.getBoolean("plotInfoOverlay")) {
            StateOverlayHandler.drawStateOverlay(tr, stack);
        }
    }

    @Inject(at = @At("HEAD"), method = "displayScoreboardSidebar", cancellable = true)
    private void displayScoreboardSidebar(CallbackInfo info) {
        Minecraft client = Recode.MC;
        if (Config.getBoolean("hideScoreboardOnF3")) {
            if (client.options.renderDebug) {
                info.cancel();
            }
        }
    }
}