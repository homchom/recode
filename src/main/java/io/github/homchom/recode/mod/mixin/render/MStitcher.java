package io.github.homchom.recode.mod.mixin.render;

import net.minecraft.client.renderer.texture.Stitcher;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ALL")
@Mixin(Stitcher.class)
public class MStitcher {
    @Shadow private int storageY;

    @Inject(method = "stitch", at = @At("RETURN"))
    private void stitch(CallbackInfo ci) {
        storageY *= 2;
    }

}
