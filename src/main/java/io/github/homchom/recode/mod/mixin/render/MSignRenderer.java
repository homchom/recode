package io.github.homchom.recode.mod.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.mod.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(SignRenderer.class)
public class MSignRenderer {
    private final Minecraft mc = Minecraft.getInstance();

    @Inject(at = @At("HEAD"))
    public void render(SignBlockEntity signBlockEntity, float f, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, int j) {
        if (!signBlockEntity.getBlockPos().closerThan(mc.cameraEntity.blockPosition(), Config.getInteger("signRenderDistance")))
            return;
    }
}
