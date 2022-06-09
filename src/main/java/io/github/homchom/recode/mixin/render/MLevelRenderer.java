package io.github.homchom.recode.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import io.github.homchom.recode.event.RecodeEvents;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LevelRenderer.class)
public class MLevelRenderer {
	private static final String popPushMethod =
			"Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V";

	@Shadow
	@Nullable
	private PostChain entityEffect;

	private boolean processedOutline;

	public boolean canProcessOutline() {
		return entityEffect != null && !processedOutline;
	}

	public void processOutline(float partialTick) {
		Objects.requireNonNull(entityEffect).process(partialTick);
		processedOutline = true;
		Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
	}

	@Inject(method = "renderLevel", at = @At("HEAD"))
	public void resetOutlineFlag(CallbackInfo ci) {
		processedOutline = false;
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", ordinal = 0,
			target = "Lnet/minecraft/client/renderer/PostChain;process(F)V"),
			slice = @Slice(
					from = @At(value = "INVOKE_STRING", args = "ldc=blockentities", target = popPushMethod),
					to = @At(value = "INVOKE_STRING", args = "ldc=destroyProgress", target = popPushMethod)
			))
	public void setOutlineFlag(CallbackInfo ci) {
		processedOutline = true;
	}

	// TODO: move this to fabric event
	@Inject(method = "renderLevel", at = @At(value = "INVOKE_STRING", args = "ldc=destroyProgress",
			target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"))
	public void processBlockEntityOutlines(
			PoseStack poseStack, float f, long l, boolean bl, Camera camera,
			GameRenderer gameRenderer, LightTexture lightTexture,
			Matrix4f matrix4f, CallbackInfo ci) {
		if (canProcessOutline()) {
			if (RecodeEvents.OUTLINE_BLOCK_ENTITY.getPrevResult() != null) {
				processOutline(f);
				Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
			}
		}
	}
}