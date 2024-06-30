package io.github.homchom.recode.mod.mixin.render;

import io.github.homchom.recode.hypercube.state.DF;
import io.github.homchom.recode.hypercube.state.PlotMode;
import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.mod.features.StateOverlayHandler;
import io.github.homchom.recode.mod.features.commands.CodeSearcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MInGameHUD {
    @Inject(method = "renderEffects", at = @At("RETURN"))
    private void renderEffects(GuiGraphics guiGraphics, CallbackInfo ci) {
        //LagslayerHUD.onRender(guiGraphics);

        Minecraft mc = Minecraft.getInstance();
        Font tr = mc.font;
        
        if (CodeSearcher.searchType != null && CodeSearcher.searchValue != null && DF.isInMode(DF.getCurrentDFState(), PlotMode.Dev.ID)) {
            guiGraphics.drawString(tr, Component.literal("Searching for usages of " +
                    CodeSearcher.searchType.getSignText().get(0) + ": " + CodeSearcher.searchValue
            ), 4, 4, 0xffffff);
        }

        if (LegacyConfig.getBoolean("plotInfoOverlay")) {
            StateOverlayHandler.drawStateOverlay(tr, guiGraphics);
        }
    }

    @Inject(at = @At("HEAD"), method = "displayScoreboardSidebar", cancellable = true)
    private void displayScoreboardSidebar(CallbackInfo info) {
        if (LegacyConfig.getBoolean("hideScoreboardOnF3")) {
            if (Minecraft.getInstance().gui.getDebugOverlay().showDebugScreen()) {
                info.cancel();
            }
        }
    }
}