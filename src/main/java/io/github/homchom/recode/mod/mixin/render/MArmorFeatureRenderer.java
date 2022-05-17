package io.github.homchom.recode.mod.mixin.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.*;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.sys.util.LimitedHashmap;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class MArmorFeatureRenderer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    @Shadow protected abstract void setPartVisibility(A bipedModel, EquipmentSlot slot);

    public MArmorFeatureRenderer(RenderLayerParent<T, M> context) {
        super(context);
    }

    LimitedHashmap<String, ResourceLocation> cu_cache = new LimitedHashmap<>(64);

    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void renderArmorPiece(PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot equipmentSlot, int i, A bipedEntityModel, CallbackInfo ci) {
        ItemStack stack = livingEntity.getItemBySlot(equipmentSlot);
        if (!stack.isEmpty()) {
            CompoundTag info = stack.getTagElement("RecodeTextureData");
            if (info != null && (info.contains("texture") || info.contains("model") || info.contains("armor"))) {
                ci.cancel();

                if (info.contains("armor")) {
                    this.getParentModel().copyPropertiesTo(bipedEntityModel);
                    setPartVisibility(bipedEntityModel,equipmentSlot);
                    try {
                        ResourceLocation id = cu_cache.computeIfAbsent(info.getString("armor"),(s) -> {
                            try {
                                NativeImage img = NativeImage.fromBase64(info.getString("armor"));
                                if (img.getWidth() * img.getHeight() > 64*4*32*4) return MissingTextureAtlasSprite.getLocation();
                                TextureManager tm = Recode.MC.getTextureManager();
                                DynamicTexture nibt = new DynamicTexture(img);
                                return tm.register("cu_armor", nibt);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return MissingTextureAtlasSprite.getLocation();
                        });

                        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource, RenderType.armorCutoutNoCull(id), false, stack.hasFoil());
                        bipedEntityModel.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }
        }
    }
}
