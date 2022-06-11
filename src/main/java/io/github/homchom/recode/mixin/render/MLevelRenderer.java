package io.github.homchom.recode.mixin.render;

import io.github.homchom.recode.render.OutlineProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LevelRenderer.class)
public class MLevelRenderer implements OutlineProcessor {
	private static final String popPushMethod =
			"Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V";

	@Shadow
	@Nullable
	private PostChain entityEffect;

	private boolean processedOutlines;

	@Override
	public boolean canProcessOutlines() {
		return entityEffect != null && !processedOutlines;
	}

	@Override
	public void processOutlines(float partialTick) {
		Objects.requireNonNull(entityEffect).process(partialTick);
		processedOutlines = true;
		Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
	}

	@Inject(method = "renderLevel", at = @At("HEAD"))
	public void resetOutlineFlag(CallbackInfo ci) {
		processedOutlines = false;
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", ordinal = 0,
			target = "Lnet/minecraft/client/renderer/PostChain;process(F)V"),
			slice = @Slice(
					from = @At(value = "INVOKE_STRING", args = "ldc=blockentities", target = popPushMethod),
					to = @At(value = "INVOKE_STRING", args = "ldc=destroyProgress", target = popPushMethod)
			))
	public void setOutlineFlag(CallbackInfo ci) {
		processedOutlines = true;
	}
}