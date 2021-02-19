package io.github.codeutilities.cosmetics;

import io.github.codeutilities.CodeUtilities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BlazeMCworldCosmetic extends
    FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    private final ModelPart[] rods;
    private final Identifier blazeTexture = new Identifier("textures/entity/blaze.png");
    private int lasttick = 0;
    private boolean sneaking = false;

    public BlazeMCworldCosmetic(
        FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
        super(featureRendererContext);
        this.rods = new ModelPart[12];
        for(int i = 0; i < this.rods.length; ++i) {
            this.rods[i] = new ModelPart(64,32, 0, 16);
            this.rods[i].addCuboid(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F);
        }
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l) {
        if ("BlazeMCworld".equals(abstractClientPlayerEntity.getName().getString()) && abstractClientPlayerEntity.hasSkinTexture() && !abstractClientPlayerEntity.isInvisible()) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(blazeTexture));
            int m = LivingEntityRenderer.getOverlay(abstractClientPlayerEntity, 0.0F);

            setAngels(j+g);

            matrixStack.push();

            if (abstractClientPlayerEntity.isInSneakingPose() && !sneaking) {
                System.out.println(g);
                matrixStack.scale(g,g,g);
                matrixStack.translate(0D,1-g,0D);
            } else if (!abstractClientPlayerEntity.isInSneakingPose() && sneaking) {
                matrixStack.scale(1-g,1-g,1-g);
                matrixStack.translate(0D,g,0D);
            }
            if (lasttick != (int) j) {
                sneaking = abstractClientPlayerEntity.isInSneakingPose();
            }
            lasttick = (int) j;

            matrixStack.translate(-0.05D,0D,0.0D);
            for (ModelPart rod : rods) {
                rod.render(matrixStack, vertexConsumer, i, m);
            }
            matrixStack.pop();

        }
    }

    public void setAngels(float animationProgress) {
        float f = animationProgress * 3.1415927F * -0.1F;

        int k;
        for(k = 0; k < 4; ++k) {
            this.rods[k].pivotY = -2.0F + MathHelper.cos(((float)(k * 2) + animationProgress) * 0.25F);
            this.rods[k].pivotX = MathHelper.cos(f) * 10.0F;
            this.rods[k].pivotZ = MathHelper.sin(f) * 10.0F;
            f+=Math.PI/2;
        }

        f = 0.7853982F + animationProgress * 3.1415927F * 0.03F;

        for(k = 4; k < 8; ++k) {
            this.rods[k].pivotY = 2.0F + MathHelper.cos(((float)(k * 2) + animationProgress) * 0.25F);
            this.rods[k].pivotX = MathHelper.cos(f) * 9.0F;
            this.rods[k].pivotZ = MathHelper.sin(f) * 9.0F;
            f+=Math.PI/2;
        }

        f = 0.47123894F + animationProgress * 3.1415927F * -0.05F;

        for(k = 8; k < 12; ++k) {
            this.rods[k].pivotY = 11.0F + MathHelper.cos(((float)k * 1.5F + animationProgress) * 0.5F);
            this.rods[k].pivotX = MathHelper.cos(f) * 6.0F;
            this.rods[k].pivotZ = MathHelper.sin(f) * 6.0F;
            f+=Math.PI/2;
        }
    }
}
