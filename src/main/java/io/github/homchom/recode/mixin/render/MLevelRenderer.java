package io.github.homchom.recode.mixin.render;

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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(LevelRenderer.class)
public abstract class MLevelRenderer implements OutlineProcessor {
	private static final String popPushMethod =
			"Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V";

	@Shadow @Nullable private PostChain entityEffect;
	@Shadow @Final private Set<BlockEntity> globalBlockEntities;

	private boolean processedOutlines;
	private final Map<BlockPos, RGBAColor> blockEntityOutlineMap = new HashMap<>();

	@Redirect(method = "renderLevel", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD,
			target = "Lnet/minecraft/client/renderer/LevelRenderer;globalBlockEntities:Ljava/util/Set;",
			ordinal = 1
	))
	public Set<BlockEntity> interceptGlobalBlockEntities(LevelRenderer instance) {
		if (globalBlockEntities.isEmpty()) return globalBlockEntities;
		return Set.copyOf(runBlockEntityEvents(globalBlockEntities, null));
	}

	@Redirect(method = "renderLevel", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;getRenderableBlockEntities()Ljava/util/List;"
	))
	public List<BlockEntity> interceptChunkBlockEntities(ChunkRenderDispatcher.CompiledChunk chunk) {
		var blockEntities = chunk.getRenderableBlockEntities();
		if (blockEntities.isEmpty()) return blockEntities;
		var chunkPos = new ChunkPos3D(blockEntities.get(0).getBlockPos());
		return runBlockEntityEvents(blockEntities, chunkPos);
	}

	private List<BlockEntity> runBlockEntityEvents(
			Collection<BlockEntity> blockEntities, @Nullable ChunkPos3D chunkPos) {
		var renderList = blockEntities.stream().map(SimpleValidated::new).toList();
		var filtered = RenderBlockEntitiesEvent.INSTANCE.runBlocking(renderList);
		var outlineContext = new BlockEntityOutlineContext(blockEntities, chunkPos);
		blockEntityOutlineMap.putAll(OutlineBlockEntitiesEvent.INSTANCE.runBlocking(outlineContext));
		return filtered;
	}

	@Override
	public boolean needsOutlineProcessing() {
		return entityEffect != null && !processedOutlines && !blockEntityOutlineMap.isEmpty();
	}

	@Override
	public void processOutlines(float partialTick) {
		Objects.requireNonNull(entityEffect).process(partialTick);
		processedOutlines = true;
		Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
	}

	@Override
	public @Nullable RGBAColor getBlockEntityOutlineColor(@NotNull BlockEntity blockEntity) {
		return blockEntityOutlineMap.get(blockEntity.getBlockPos());
	}

	@Inject(method = "renderLevel", at = @At("HEAD"))
	public void resetOutlineVars(CallbackInfo ci) {
		processedOutlines = false;
		blockEntityOutlineMap.clear();
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", ordinal = 0,
			target = "Lnet/minecraft/client/renderer/PostChain;process(F)V"),
			slice = @Slice(
					from = @At(value = "INVOKE_STRING", args = "ldc=blockentities", target = popPushMethod),
					to = @At(value = "INVOKE_STRING", args = "ldc=destroyProgress", target = popPushMethod)
			))
	public void setProcessedOutlines(CallbackInfo ci) {
		processedOutlines = true;
	}
}