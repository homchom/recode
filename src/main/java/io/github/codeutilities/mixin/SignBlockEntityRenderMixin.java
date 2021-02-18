package io.github.codeutilities.mixin;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignBlockEntityRenderer.class)
public class SignBlockEntityRenderMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack,
        VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        if (!signBlockEntity.getPos().isWithinDistance(CodeUtilities.mc.cameraEntity.getBlockPos(),
            ModConfig.getConfig().signRenderDistance)) ci.cancel();
    }

}
