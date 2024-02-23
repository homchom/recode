package io.github.homchom.recode.mod.mixin.inventory;

import io.github.homchom.recode.mod.features.keybinds.Keybinds;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class MKeyboardHandler {
    @Inject(method = "keyPress", at = @At(value = "HEAD"))
    public void onKeyPressed(long l, int i, int j, int k, int m, CallbackInfo ci) {
        if(Keybinds.showTags.matches(i,j)) {
            if (k != 1 && k != 2) {
                if (k == 0) {
                    // released
                    Keybinds.showingTags = false;
                }
            } else {
                // pressed
                Keybinds.showingTags = true;
            }
        }
    }
}