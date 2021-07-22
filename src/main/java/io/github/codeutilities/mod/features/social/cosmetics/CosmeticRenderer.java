package io.github.codeutilities.mod.features.social.cosmetics;

import com.google.gson.JsonObject;
import io.github.codeutilities.mod.features.social.cosmetics.type.CosmeticType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;

public class CosmeticRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public CosmeticRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l) {
        if (!abstractClientPlayerEntity.isInvisible()) {
            CosmeticModel cosmeticModel = CosmeticType.HAT.getPlayerHat(abstractClientPlayerEntity.getUuid());

            if (cosmeticModel != null) {
                BakedModel model = cosmeticModel.model;
                JsonObject attributes = cosmeticModel.attributes;

                Vector3f translation = model.getTransformation().head.translation;
                Vector3f scale = model.getTransformation().head.scale;
                ModelPart head = getContextModel().head;
                float scalex = scale.getX() - 0.333333333f;
                float scaley = scale.getY() - 0.333333333f;
                float scalez = scale.getZ() - 0.333333333f;
                matrixStack.translate((translation.getX() * -1) * scalex, (translation.getY() * -1) * scaley, (translation.getZ() * -1) * scalez);
                if (attributes.get("type").getAsString().equals("head"))
                    rotate(matrixStack, 0f, abstractClientPlayerEntity.isInSneakingPose() ? translation.getY() * 0.675f + 0.27f : translation.getY() * 0.675f, 0f, head.pitch, head.yaw, head.roll);
                matrixStack.scale(scalex, scaley, scalez);
                if (abstractClientPlayerEntity.isInSneakingPose()) matrixStack.translate(0f, 0.4f, 0f);
                matrixStack.translate(-0.5f, -0.9f, -0.5f);

                MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(
                        matrixStack.peek(),
                        vertexConsumerProvider.getBuffer(RenderLayer.getSolid()),
                        null,
                        model,
                        1f, 1f, 1f,
                        Math.max(i - 2, 0),
                        OverlayTexture.DEFAULT_UV
                );
            }
        }
    }

    public void rotate(MatrixStack matrix, float pivotX, float pivotY, float pivotZ, float pitch, float yaw, float roll) {
        matrix.translate(pivotX, pivotY, pivotZ);
        if (roll != 0.0F) {
            matrix.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(roll));
        }

        if (yaw != 0.0F) {
            matrix.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(yaw));
        }

        if (pitch != 0.0F) {
            matrix.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(pitch));
        }
        matrix.translate(pivotX * -1, pivotY * -1, pivotZ * -1);
    }

}
