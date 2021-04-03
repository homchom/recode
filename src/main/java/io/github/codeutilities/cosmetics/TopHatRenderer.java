package io.github.codeutilities.cosmetics;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class TopHatRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    //private final ArrayList<Identifier> hatTexture = new ArrayList<>();

    public TopHatRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l) {
/*        if ("deadmau5".equals(abstractClientPlayerEntity.getName().getString()) && abstractClientPlayerEntity.hasSkinTexture() && !abstractClientPlayerEntity.isInvisible()) {
            int m = LivingEntityRenderer.getOverlay(abstractClientPlayerEntity, 0.0F);

        VertexConsumer vertexConsumer = vertexConsumerProvider
            .getBuffer(RenderLayer.getEntitySolid(hatTexture));

                matrixStack.push();
                matrixStack.translate(0.0D, -0.3D, 0.0D);

                ModelPart ears = new ModelPart(16, 16,0,0);
                ears.addCuboid(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F);
                ears.copyPositionAndRotation(getContextModel().head);
                ears.render(matrixStack,vertexConsumer,i,m);

                matrixStack.pop();

        }*/

    }

}
