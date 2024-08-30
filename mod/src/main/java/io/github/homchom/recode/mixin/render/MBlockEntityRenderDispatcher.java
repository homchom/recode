package io.github.homchom.recode.mixin.render;

import io.github.homchom.recode.render.Blaze3DExtensions;
import io.github.homchom.recode.render.DRecodeLevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = BlockEntityRenderDispatcher.class)
public abstract class MBlockEntityRenderDispatcher {
	@ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private MultiBufferSource outlineBlockEntities(MultiBufferSource multiBufferSource, BlockEntity blockEntity) {
		var processor = (DRecodeLevelRenderer) Minecraft.getInstance().levelRenderer;
		var outlineColor = processor.recode$getBlockEntityOutlineColor(blockEntity);
		if (outlineColor != null) {
			return Blaze3DExtensions.withOutline(multiBufferSource, outlineColor);
		}
		return multiBufferSource;
	}
}
