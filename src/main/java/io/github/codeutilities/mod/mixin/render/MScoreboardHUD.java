package io.github.codeutilities.mod.mixin.render;

import io.github.codeutilities.CodeUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MScoreboardHUD {
    @Inject(at = @At("HEAD"), method = "renderScoreboardSidebar", cancellable = true)
	private void init(CallbackInfo info) {
		MinecraftClient client = CodeUtilities.MC;
		if (client.options.debugEnabled) {
			info.cancel();
		}
	}
}
