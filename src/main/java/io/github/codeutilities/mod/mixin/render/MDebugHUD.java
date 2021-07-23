package io.github.codeutilities.mod.mixin.render;

import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.player.DFInfo;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class MDebugHUD {

    @Inject(method = "getLeftText", at = @At("RETURN"), cancellable = true)
    protected void getLeftText(CallbackInfoReturnable<List<String>> callbackInfoReturnable) {
        if (!Config.getBoolean("f3Tps")) {
            return;
        }

        try {
            List<String> leftText = callbackInfoReturnable.getReturnValue();
            leftText.add("");
            leftText.add(Formatting.UNDERLINE + "CodeUtilities");
            leftText.add("Client TPS: " + DFInfo.TPS);

            callbackInfoReturnable.setReturnValue(leftText);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
