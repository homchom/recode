package io.github.codeutilities.mod.mixin.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeadFeatureRenderer.class)
public abstract class MHeadFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M> {

    @Shadow @Final private float field_24474;

    @Shadow @Final private float field_24475;

    @Shadow @Final private float field_24476;

    public MHeadFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "render", cancellable = true, at = @At("HEAD"))
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ItemStack stack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
        if (!stack.isEmpty()) {

            CompoundTag info = stack.getSubTag("CodeutilitiesTextureData");
            if (info != null && (info.contains("texture") || info.contains("model"))) {
                ci.cancel();
                if (info.contains("armor")) return;
                matrixStack.push();
                matrixStack.scale(this.field_24474, this.field_24475, this.field_24476);
                boolean bl = livingEntity instanceof VillagerEntity || livingEntity instanceof ZombieVillagerEntity;
                if (livingEntity.isBaby() && !(livingEntity instanceof VillagerEntity)) {
                    matrixStack.translate(0.0D, 0.03125D, 0.0D);
                    matrixStack.scale(0.7F, 0.7F, 0.7F);
                    matrixStack.translate(0.0D, 1.0D, 0.0D);
                }

                this.getContextModel().getHead().rotate(matrixStack);
                matrixStack.translate(0.0D, -0.25D, 0.0D);
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
                matrixStack.scale(0.625F, -0.625F, -0.625F);
                if (bl) {
                    matrixStack.translate(0.0D, 0.1875D, 0.0D);
                }

                MinecraftClient.getInstance().getHeldItemRenderer().renderItem(livingEntity, stack, Mode.HEAD, false, matrixStack, vertexConsumerProvider, i);
                matrixStack.pop();
            }
        }
    }

}
