package io.github.codeutilities.mixin.render;

import io.github.codeutilities.config.CodeUtilsConfig;
import io.github.codeutilities.util.DFInfo;
import io.github.codeutilities.util.templates.FuncSearchUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

import static net.minecraft.client.render.block.entity.SignBlockEntityRenderer.getModelTexture;

@Mixin(SignBlockEntityRenderer.class)
public class SignBlockEntityRenderMixin {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    @Shadow
    @Final
    private SignBlockEntityRenderer.SignModel model;

    /**
     * @author CodeUtilities
     */
    @Overwrite
    public void render(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        if (!signBlockEntity.getPos().isWithinDistance(mc.cameraEntity.getBlockPos(), CodeUtilsConfig.getInt("signRenderDistance")))
            return;

        TextRenderer textRenderer = mc.textRenderer;

        if (FuncSearchUtil.shouldGlow(signBlockEntity) && DFInfo.currentState == DFInfo.State.DEV && mc.player.isCreative()) {
            double distance = Math.sqrt(signBlockEntity.getPos().getSquaredDistance(mc.cameraEntity.getBlockPos()));
            double dist = MathHelper.clamp(distance, 1, 15);

            OutlineVertexConsumerProvider outlineVertexConsumerProvider = mc.getBufferBuilders().getOutlineVertexConsumers();
            outlineVertexConsumerProvider.setColor(255, 255, 255, (int) (dist * 17));
            vertexConsumerProvider = outlineVertexConsumerProvider;
        }

        BlockState blockState = signBlockEntity.getCachedState();
        matrixStack.push();
        float g = 0.6666667F;
        float h;
        if (blockState.getBlock() instanceof SignBlock) {
            matrixStack.translate(0.5D, 0.5D, 0.5D);
            h = -((float) (blockState.get(SignBlock.ROTATION) * 360) / 16.0F);
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
            this.model.foot.visible = true;
        } else {
            matrixStack.translate(0.5D, 0.5D, 0.5D);
            h = -blockState.get(WallSignBlock.FACING).asRotation();
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
            matrixStack.translate(0.0D, -0.3125D, -0.4375D);
            this.model.foot.visible = false;
        }

        matrixStack.push();
        matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        SpriteIdentifier spriteIdentifier = getModelTexture(blockState.getBlock());
        SignBlockEntityRenderer.SignModel var10002 = this.model;
        var10002.getClass();
        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, var10002::getLayer);
        this.model.field.render(matrixStack, vertexConsumer, i, j);
        this.model.foot.render(matrixStack, vertexConsumer, i, j);
        matrixStack.pop();
        float l = 0.010416667F;
        matrixStack.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
        matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
        int m = signBlockEntity.getTextColor().getSignColor();
        double d = 0.4D;
        int n = (int) ((double) NativeImage.getRed(m) * 0.4D);
        int o = (int) ((double) NativeImage.getGreen(m) * 0.4D);
        int p = (int) ((double) NativeImage.getBlue(m) * 0.4D);
        int q = NativeImage.getAbgrColor(0, p, o, n);

        for (int s = 0; s < 4; ++s) {
            OrderedText orderedText = signBlockEntity.getTextBeingEditedOnRow(s, (text) -> {
                List<OrderedText> list = textRenderer.wrapLines(text, 90);
                return list.isEmpty() ? OrderedText.EMPTY : list.get(0);
            });
            if (orderedText != null) {
                float t = (float) (-textRenderer.getWidth(orderedText) / 2);
                textRenderer.draw(orderedText, t, (float) (s * 10 - 20), q, false, matrixStack.peek().getModel(), vertexConsumerProvider, false, 0, i);
            }
        }

        matrixStack.pop();
    }

}
