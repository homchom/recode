package io.github.codeutilities.mod.mixin.render;

import io.github.codeutilities.mod.features.TemplatePeeker;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignBlockEntityRenderer.class)
public class MSignBlockEntityRenderer {

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(BlockEntityRenderDispatcher berd, CallbackInfo ci) {
        TemplatePeeker.berdContext = berd;
    }

}
