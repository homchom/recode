package io.github.homchom.recode.mod.mixin.render;

import net.minecraft.client.renderer.texture.Stitcher;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Stitcher.class)
public class MTextureStitcher {

    @Shadow private int height;

    @Inject(method = "stitch", at = @At("RETURN"))
    private void stitch(CallbackInfo ci) {
        this.height*=2;
    }

}
