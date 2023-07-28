package io.github.homchom.recode.mixin.render;

import io.github.homchom.recode.game.ChunkPos3D;
import io.github.homchom.recode.render.RecodeLevelRenderer;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.List;
import java.util.Set;

// makes code search compatible with Sodium
@Mixin(value = SodiumWorldRenderer.class, remap = false)
public abstract class MSodiumWorldRenderer {
    @Shadow @Final private Set<BlockEntity> globalBlockEntities;

    @Redirect(method = "renderTileEntities", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD,
            target = "Lme/jellysquid/mods/sodium/client/render/SodiumWorldRenderer;globalBlockEntities:Ljava/util/Set;"
    ))
    public Set<BlockEntity> interceptGlobalBlockEntitiesForRecode(SodiumWorldRenderer instance) {
        if (globalBlockEntities.isEmpty()) return globalBlockEntities;

        var levelRenderer = (RecodeLevelRenderer) Minecraft.getInstance().levelRenderer;
        return Set.copyOf(levelRenderer.recode$runBlockEntityEvents(globalBlockEntities, null));
    }

    @Redirect(method = "renderTileEntities", at = @At(value = "INVOKE",
            target = "Lme/jellysquid/mods/sodium/client/render/chunk/RenderSectionManager;getVisibleBlockEntities()Ljava/util/Collection;"
    ))
    public Collection<BlockEntity> interceptChunkBlockEntities(RenderSectionManager manager) {
        var blockEntities = manager.getVisibleBlockEntities();
        if (blockEntities.isEmpty()) return List.copyOf(blockEntities);

        var chunkPos = new ChunkPos3D(blockEntities.stream().findFirst().get().getBlockPos());
        var levelRenderer = (RecodeLevelRenderer) Minecraft.getInstance().levelRenderer;
        return levelRenderer.recode$runBlockEntityEvents(blockEntities, chunkPos);
    }
}
