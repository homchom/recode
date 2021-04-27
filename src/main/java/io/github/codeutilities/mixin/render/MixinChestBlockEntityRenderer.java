package io.github.codeutilities.mixin.render;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.CodeUtilsConfig;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.*;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlockEntityRenderer.class)
public abstract class MixinChestBlockEntityRenderer<T extends BlockEntity & ChestAnimationProgress> extends
        BlockEntityRenderer<T> {
    
    public MixinChestBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }
    
    @Inject(method = "Lnet/minecraft/client/render/block/entity/ChestBlockEntityRenderer;render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("HEAD"), cancellable = true)
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        if (CodeUtilsConfig.getBool("chestReplacement") && entity instanceof ChestBlockEntity) {
            ci.cancel();
            
            BlockState state = Blocks.BARREL.getDefaultState();
            CodeUtilities.MC.getBlockRenderManager().renderBlockAsEntity(state, matrices, vertexConsumers, light, overlay);
        }
    }
}
