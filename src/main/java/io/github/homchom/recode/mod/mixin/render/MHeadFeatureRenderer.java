package io.github.homchom.recode.mod.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomHeadLayer.class)
public abstract class MHeadFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {

    @Shadow @Final private float field_24474;

    @Shadow @Final private float field_24475;

    @Shadow @Final private float field_24476;

    public MHeadFeatureRenderer(RenderLayerParent<T, M> context) {
        super(context);
    }

    @Inject(method = "render", cancellable = true, at = @At("HEAD"))
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ItemStack stack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        if (!stack.isEmpty()) {

            CompoundTag info = stack.getTagElement("RecodeTextureData");
            if (info != null && (info.contains("texture") || info.contains("model"))) {
                ci.cancel();
                if (info.contains("armor")) return;
                matrixStack.pushPose();
                matrixStack.scale(this.field_24474, this.field_24475, this.field_24476);
                boolean bl = livingEntity instanceof Villager || livingEntity instanceof ZombieVillager;
                if (livingEntity.isBaby() && !(livingEntity instanceof Villager)) {
                    matrixStack.translate(0.0D, 0.03125D, 0.0D);
                    matrixStack.scale(0.7F, 0.7F, 0.7F);
                    matrixStack.translate(0.0D, 1.0D, 0.0D);
                }

                this.getParentModel().getHead().translateAndRotate(matrixStack);
                matrixStack.translate(0.0D, -0.25D, 0.0D);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                matrixStack.scale(0.625F, -0.625F, -0.625F);
                if (bl) {
                    matrixStack.translate(0.0D, 0.1875D, 0.0D);
                }

                Minecraft.getInstance().getItemInHandRenderer().renderItem(livingEntity, stack, TransformType.HEAD, false, matrixStack, vertexConsumerProvider, i);
                matrixStack.popPose();
            }
        }
    }

}
