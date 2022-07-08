package io.github.homchom.recode.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.event.*;
import io.github.homchom.recode.render.Blaze3DExtensions;
import io.github.homchom.recode.ui.RGBAColor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockEntityRenderDispatcher.class, priority = 100)
public class MBlockEntityRenderDispatcher {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void renderBlockEntities(
			BlockEntity blockEntity, float f, PoseStack poseStack,
			MultiBufferSource multiBufferSource, CallbackInfo ci) {
		if (!EventValidation.validate(RecodeEvents.RENDER_BLOCK_ENTITY, blockEntity)) {
			ci.cancel();
		}
	}

	@ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	public MultiBufferSource outlineBlockEntities(MultiBufferSource multiBufferSource, BlockEntity blockEntity) {
		RGBAColor outlineColor = RecodeEvents.OUTLINE_BLOCK_ENTITY
				.invoke(blockEntity, new RecodeEvents.OutlineResult())
				.getOutlineColor();
		if (outlineColor != null) {
			return Blaze3DExtensions.withOutline(multiBufferSource, outlineColor);
		}
		return multiBufferSource;
	}
}
