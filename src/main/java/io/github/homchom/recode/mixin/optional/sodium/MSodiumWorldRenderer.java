package io.github.homchom.recode.mixin.optional.sodium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.homchom.recode.game.ChunkPos3D;
import io.github.homchom.recode.render.RecodeLevelRenderer;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

// makes code search compatible with Sodium
@Mixin(SodiumWorldRenderer.class)
public abstract class MSodiumWorldRenderer {
    @WrapOperation(method = "renderBlockEntities", at = @At(value = "INVOKE",
            target = "Lme/jellysquid/mods/sodium/client/render/chunk/RenderSection;getCulledBlockEntities()[Lnet/minecraft/world/level/block/entity/BlockEntity;"
    ))
    private BlockEntity @Nullable [] interceptChunkBlockEntities(
            RenderSection section,
            Operation<BlockEntity @Nullable []> operation
    ) {
        var blockEntities = operation.call(section);
        if (blockEntities == null || blockEntities.length == 0) return blockEntities;

        var blockEntityList = List.of(blockEntities);
        var chunkPos = new ChunkPos3D(blockEntityList.stream().findFirst().get().getBlockPos());
        var levelRenderer = (RecodeLevelRenderer) Minecraft.getInstance().levelRenderer;
        return levelRenderer.recode$runBlockEntityEvents(blockEntityList, chunkPos)
                .toArray(new BlockEntity[0]);
    }

    @WrapOperation(method = "renderGlobalBlockEntities", at = @At(value = "INVOKE",
            target = "Lme/jellysquid/mods/sodium/client/render/chunk/RenderSection;getGlobalBlockEntities()[Lnet/minecraft/world/level/block/entity/BlockEntity;"
    ))
    private BlockEntity @Nullable [] interceptGlobalBlockEntities(
            RenderSection section,
            Operation<BlockEntity @Nullable []> operation
    ) {
        var blockEntities = operation.call(section);
        if (blockEntities == null || blockEntities.length == 0) return blockEntities;

        var levelRenderer = (RecodeLevelRenderer) Minecraft.getInstance().levelRenderer;
        return levelRenderer.recode$runBlockEntityEvents(List.of(blockEntities), null)
                .toArray(new BlockEntity[0]);
    }
}
