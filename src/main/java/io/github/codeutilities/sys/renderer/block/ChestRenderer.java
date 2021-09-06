package io.github.codeutilities.sys.renderer.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Calendar;

public class ChestRenderer<T extends BlockEntity & ChestAnimationProgress> extends BlockEntityRenderer<T> {
    private final ColoredModelPart singleChestLid;
    private final ColoredModelPart singleChestBase;
    private final ColoredModelPart singleChestLatch;
    private final ColoredModelPart doubleChestRightLid;
    private final ColoredModelPart doubleChestRightBase;
    private final ColoredModelPart doubleChestRightLatch;
    private final ColoredModelPart doubleChestLeftLid;
    private final ColoredModelPart doubleChestLeftBase;
    private final ColoredModelPart doubleChestLeftLatch;
    private SpriteIdentifier chestSprite = new SpriteIdentifier(TexturedRenderLayers.CHEST_ATLAS_TEXTURE, new Identifier("entity/chest/normal"));
    private boolean christmas;

    public ChestRenderer() {
        super(BlockEntityRenderDispatcher.INSTANCE);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
            this.christmas = true;
        }

        this.singleChestBase = new ColoredModelPart(64, 64, 0, 19);
        this.singleChestBase.addCuboid(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
        this.singleChestLid = new ColoredModelPart(64, 64, 0, 0);
        this.singleChestLid.addCuboid(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
        this.singleChestLid.pivotY = 9.0F;
        this.singleChestLid.pivotZ = 1.0F;
        this.singleChestLatch = new ColoredModelPart(64, 64, 0, 0);
        this.singleChestLatch.addCuboid(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
        this.singleChestLatch.pivotY = 8.0F;
        this.doubleChestRightBase = new ColoredModelPart(64, 64, 0, 19);
        this.doubleChestRightBase.addCuboid(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
        this.doubleChestRightLid = new ColoredModelPart(64, 64, 0, 0);
        this.doubleChestRightLid.addCuboid(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
        this.doubleChestRightLid.pivotY = 9.0F;
        this.doubleChestRightLid.pivotZ = 1.0F;
        this.doubleChestRightLatch = new ColoredModelPart(64, 64, 0, 0);
        this.doubleChestRightLatch.addCuboid(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
        this.doubleChestRightLatch.pivotY = 8.0F;
        this.doubleChestLeftBase = new ColoredModelPart(64, 64, 0, 19);
        this.doubleChestLeftBase.addCuboid(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
        this.doubleChestLeftLid = new ColoredModelPart(64, 64, 0, 0);
        this.doubleChestLeftLid.addCuboid(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
        this.doubleChestLeftLid.pivotY = 9.0F;
        this.doubleChestLeftLid.pivotZ = 1.0F;
        this.doubleChestLeftLatch = new ColoredModelPart(64, 64, 0, 0);
        this.doubleChestLeftLatch.addCuboid(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
        this.doubleChestLeftLatch.pivotY = 8.0F;
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

    }

    public void render(World world, BlockPos pos, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Color color) {
        BlockState blockState = Blocks.CHEST.getDefaultState();
        blockState = blockState.getProperties().contains(ChestBlock.FACING) ? blockState : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
        ChestType chestType = blockState.contains(ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
        Block block = blockState.getBlock();
        if (block instanceof AbstractChestBlock) {
            AbstractChestBlock<?> abstractChestBlock = (AbstractChestBlock)block;
            boolean bl2 = chestType != ChestType.SINGLE;
            matrices.push();
            float f = blockState.get(ChestBlock.FACING).asRotation();
            matrices.translate(0.5D, 0.5D, 0.5D);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-f));
            matrices.translate(-0.5D, -0.5D, -0.5D);

            float g = 0f;
            g = 1.0F - g;
            g = 1.0F - g * g * g;
            VertexConsumer vertexConsumer = chestSprite.getVertexConsumer(vertexConsumers, BlockRenderer.BetterRenderLayers::flexibleCustomTexture);
            if (bl2) {
                if (chestType == ChestType.LEFT) {
                    this.render(matrices, vertexConsumer, this.doubleChestLeftLid, this.doubleChestLeftLatch, this.doubleChestLeftBase, g, light, overlay, color);
                } else {
                    this.render(matrices, vertexConsumer, this.doubleChestRightLid, this.doubleChestRightLatch, this.doubleChestRightBase, g, light, overlay, color);
                }
            } else {
                this.render(matrices, vertexConsumer, this.singleChestLid, this.singleChestLatch, this.singleChestBase, g, light, overlay, color);
            }

            matrices.pop();
        }
    }

    private void render(MatrixStack matrices, VertexConsumer vertices, ColoredModelPart lid, ColoredModelPart latch, ColoredModelPart base, float openFactor, int light, int overlay, Color color) {
        lid.pitch = -(openFactor * 1.5707964F);
        latch.pitch = lid.pitch;
        lid.render(matrices, vertices, light, overlay, color);
        latch.render(matrices, vertices, light, overlay, color);
        base.render(matrices, vertices, light, overlay, color);
    }

}
