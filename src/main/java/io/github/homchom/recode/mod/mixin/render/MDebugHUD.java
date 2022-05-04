package io.github.homchom.recode.mod.mixin.render;

import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.player.DFInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class MDebugHUD {

    @Inject(method = "getLeftText", at = @At("RETURN"), cancellable = true)
    protected void getLeftText(CallbackInfoReturnable<List<String>> callbackInfoReturnable) {
        if (!Config.getBoolean("f3Tps")) {
            return;
        }

        try {
            List<String> leftText = callbackInfoReturnable.getReturnValue();
            leftText.add("");
            leftText.add(ChatFormatting.UNDERLINE + "Recode");
            leftText.add("Clients TPS: " + DFInfo.TPS);

            callbackInfoReturnable.setReturnValue(leftText);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
