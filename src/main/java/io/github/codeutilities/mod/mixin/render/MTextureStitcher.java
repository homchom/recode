package io.github.codeutilities.mod.mixin.render;

import net.minecraft.client.texture.TextureStitcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureStitcher.class)
public class MTextureStitcher {

    @Shadow private int height;

    @Inject(method = "stitch", at = @At("RETURN"))
    private void stitch(CallbackInfo ci) {
        this.height*=2;
    }

}
