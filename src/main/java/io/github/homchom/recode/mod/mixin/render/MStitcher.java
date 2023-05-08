package io.github.homchom.recode.mod.mixin.render;

import net.minecraft.client.renderer.texture.Stitcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Stitcher.class)
public class MStitcher {
    @Shadow private int storageY;

    @Inject(method = "stitch", at = @At("RETURN"))
    private void stitch(CallbackInfo ci) {
        storageY *= 2;
    }

}
