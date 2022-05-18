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

@SuppressWarnings("ALL")
@Mixin(CustomHeadLayer.class)
public abstract class MHeadFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {

    @Shadow @Final private float scaleX;
    @Shadow @Final private float scaleY;
    @Shadow @Final private float scaleZ;

    public MHeadFeatureRenderer(RenderLayerParent<T, M> context) {
        super(context);
    }

    @Inject(method = "render", cancellable = true, at = @At("HEAD"))
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ItemStack stack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        if (!stack.isEmpty()) {
            CompoundTag info = stack.getTagElement("RecodeTextureData");
            if (info != null && (info.contains("texture") || info.contains("model"))) {
                ci.cancel();
                if (info.contains("armor")) return;
                poseStack.pushPose();
                poseStack.scale(this.scaleX, this.scaleY, this.scaleZ);
                boolean bl = livingEntity instanceof Villager || livingEntity instanceof ZombieVillager;
                if (livingEntity.isBaby() && !(livingEntity instanceof Villager)) {
                    poseStack.translate(0.0D, 0.03125D, 0.0D);
                    poseStack.scale(0.7F, 0.7F, 0.7F);
                    poseStack.translate(0.0D, 1.0D, 0.0D);
                }

                this.getParentModel().getHead().translateAndRotate(poseStack);
                poseStack.translate(0.0D, -0.25D, 0.0D);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                poseStack.scale(0.625F, -0.625F, -0.625F);
                if (bl) {
                    poseStack.translate(0.0D, 0.1875D, 0.0D);
                }

                Minecraft.getInstance().getItemInHandRenderer().renderItem(livingEntity, stack, TransformType.HEAD, false, poseStack, multiBufferSource, i);
                poseStack.popPose();
            }
        }
    }
}
