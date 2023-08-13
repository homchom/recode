package io.github.homchom.recode.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.homchom.recode.event.SimpleValidated;
import io.github.homchom.recode.game.ChunkPos3D;
import io.github.homchom.recode.render.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(value = LevelRenderer.class)
public abstract class MLevelRenderer implements RecodeLevelRenderer {
	@Shadow @Nullable private PostChain entityEffect;

	@Unique
	private boolean processedOutlines;
	@Unique
	private final Map<BlockPos, RGBAColor> blockEntityOutlineMap = new HashMap<>();

	@WrapOperation(method = "renderLevel", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD,
			target = "Lnet/minecraft/client/renderer/LevelRenderer;globalBlockEntities:Ljava/util/Set;",
			ordinal = 1
	))
	private Set<BlockEntity> interceptGlobalBlockEntities(
			LevelRenderer instance,
			Operation<Set<BlockEntity>> operation
	) {
		var blockEntities = operation.call(instance);
		if (blockEntities.isEmpty()) return blockEntities;

		return Set.copyOf(recode$runBlockEntityEvents(blockEntities, null));
	}

	@WrapOperation(method = "renderLevel", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;getRenderableBlockEntities()Ljava/util/List;"
	))
	private List<BlockEntity> interceptChunkBlockEntities(
			ChunkRenderDispatcher.CompiledChunk chunk,
			Operation<List<BlockEntity>> operation
	) {
		var blockEntities = operation.call(chunk);
		if (blockEntities.isEmpty()) return blockEntities;

		var chunkPos = new ChunkPos3D(blockEntities.get(0).getBlockPos());
		return recode$runBlockEntityEvents(blockEntities, chunkPos);
	}

	@Override
	public @NotNull List<BlockEntity> recode$runBlockEntityEvents(
			Collection<? extends BlockEntity> blockEntities, ChunkPos3D chunkPos
	) {
		List<SimpleValidated<BlockEntity>> renderList = new ArrayList<>();
		for (var blockEntity : blockEntities) renderList.add(new SimpleValidated<>(blockEntity));
		var filtered = RenderBlockEntitiesEvent.INSTANCE.run(renderList);
		var outlineInput = new BlockEntityOutlineContext.Input(blockEntities, chunkPos);
		blockEntityOutlineMap.putAll(OutlineBlockEntitiesEvent.INSTANCE.run(outlineInput));
		return filtered;
	}

	@Override
	public void recode$processOutlines(float partialTick) {
		if (entityEffect == null || processedOutlines || blockEntityOutlineMap.isEmpty()) return;
		entityEffect.process(partialTick);
		processedOutlines = true;
		Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
	}

	@Override
	public @Nullable RGBAColor recode$getBlockEntityOutlineColor(@NotNull BlockEntity blockEntity) {
		return blockEntityOutlineMap.get(blockEntity.getBlockPos());
	}

	@Inject(method = "renderLevel", at = @At("HEAD"))
	private void startRender(CallbackInfo ci) {
		processedOutlines = false;
		blockEntityOutlineMap.clear();
		OutlineBlockEntitiesEvent.INSTANCE.stabilize();
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", ordinal = 0,
			target = "Lnet/minecraft/client/renderer/PostChain;process(F)V"),
			slice = @Slice(
					from = @At(value = "INVOKE_STRING", args = "ldc=blockentities", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"),
					to = @At(value = "INVOKE_STRING", args = "ldc=destroyProgress", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V")
			))
	private void setProcessedOutlines(CallbackInfo ci) {
		processedOutlines = true;
	}
}