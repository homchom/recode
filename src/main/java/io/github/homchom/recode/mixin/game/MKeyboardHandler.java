package io.github.homchom.recode.mixin.game;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.features.PlotModeSwitcherScreen;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.DFInfo;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyboardHandler.class)
public class MKeyboardHandler {
    @Inject(method = "handleDebugKeys", at = @At("HEAD"), cancellable = true)
    private void handleDebugKeys(int i, CallbackInfoReturnable<Boolean> cir) {
        if(i == 293 && DFInfo.isOnDF() && DFInfo.currentState.getMode() != LegacyState.Mode.SPAWN) {
            cir.cancel();
            LegacyRecode.MC.setScreen(new PlotModeSwitcherScreen());
            cir.setReturnValue(true);
        }
    }
}
