package io.github.homchom.recode.mod.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.LagslayerHUD;
import io.github.homchom.recode.mod.features.StateOverlayHandler;
import io.github.homchom.recode.mod.features.commands.CodeSearcher;
import io.github.homchom.recode.multiplayer.state.DFGlobals;
import io.github.homchom.recode.multiplayer.state.PlotMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MInGameHUD {
    @Inject(method = "renderEffects", at = @At("RETURN"))
    private void renderEffects(PoseStack stack, CallbackInfo ci) {
        LagslayerHUD.onRender(stack);

        Minecraft mc = LegacyRecode.MC;
        Font tr = mc.font;
        
        if (CodeSearcher.searchType != null && CodeSearcher.searchValue != null && DFGlobals.isInMode(DFGlobals.getCurrentDFState(), PlotMode.Dev)) {
            tr.draw(stack, Component.literal("Searching for usages of " +
                    CodeSearcher.searchType.getSignText().get(0) + ": " + CodeSearcher.searchValue
            ), 4, 4, 0xffffff);
        }

        if (Config.getBoolean("plotInfoOverlay")) {
            StateOverlayHandler.drawStateOverlay(tr, stack);
        }
    }

    @Inject(at = @At("HEAD"), method = "displayScoreboardSidebar", cancellable = true)
    private void displayScoreboardSidebar(CallbackInfo info) {
        if (Config.getBoolean("hideScoreboardOnF3")) {
            if (Minecraft.getInstance().options.renderDebug) {
                info.cancel();
            }
        }
    }
}