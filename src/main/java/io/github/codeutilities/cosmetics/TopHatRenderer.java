package io.github.codeutilities.cosmetics;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class TopHatRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private final Identifier hatTexture = new Identifier("textures/block/stone.png");
    private final ModelPart hatModel;
    
    
    public TopHatRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
        this.hatModel = new ModelPart(16, 16, 0, 0);
        this.hatModel.addCuboid(-6.0F, -10.0F, -6.0F, 12.0F, 2.0F, 12.0F, 0.0F);
        this.hatModel.addCuboid(-4.0F, -18.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F);
    }
    
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l) {
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayers.getItemLayer(new ItemStack(Items.ANVIL), false)); //
        int overlay = LivingEntityRenderer.getOverlay(abstractClientPlayerEntity, 0.0F);
    
        getContextModel().head.rotate(matrixStack);
        
        matrixStack.push();
        matrixStack.translate(0.25D, -0.5D, -0.25D);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        matrixStack.scale(0.5F, -0.5F, -0.5F);
        
    }

    
}
