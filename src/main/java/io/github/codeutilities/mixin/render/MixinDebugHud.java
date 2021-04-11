package io.github.codeutilities.mixin.render;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.util.DFInfo;
import io.github.codeutilities.util.TPSUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class MixinDebugHud {
    private MinecraftClient minecraftClient = MinecraftClient.getInstance();

    @Inject(method = "getLeftText", at = @At("RETURN"), cancellable = true)
    protected void getLeftText(CallbackInfoReturnable<List<String>> callbackInfoReturnable) {
        try {
            List<String> leftText = callbackInfoReturnable.getReturnValue();

            if (ModConfig.getConfig().f3Tps) {
                leftText.add("");
                leftText.add(Formatting.UNDERLINE + "CodeUtilities");
                leftText.add("Client TPS: " + TPSUtil.TPS);
            }

            callbackInfoReturnable.setReturnValue(leftText);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
