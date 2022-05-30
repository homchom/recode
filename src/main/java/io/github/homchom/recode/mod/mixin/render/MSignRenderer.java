package io.github.homchom.recode.mod.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.mod.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignRenderer.class)
public class MSignRenderer {
    private final Minecraft mc = Minecraft.getInstance();

    @Inject(method = "render(Lnet/minecraft/world/level/block/entity/SignBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At("HEAD"), cancellable = true)
    public void render(SignBlockEntity signBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo ci) {
        if (!signBlockEntity.getBlockPos().closerThan(mc.cameraEntity.blockPosition(), Config.getInteger("signRenderDistance")))
            ci.cancel();
    }
}
