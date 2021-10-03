package io.github.codeutilities.mod.mixin.render;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.sys.util.LimitedHashmap;
import java.io.IOException;
import java.lang.annotation.Native;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class MArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {

    @Shadow protected abstract void setVisible(A bipedModel, EquipmentSlot slot);

    public MArmorFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    LimitedHashmap<String, Identifier> cu_cache = new LimitedHashmap<>(64);

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T livingEntity, EquipmentSlot equipmentSlot, int i, A bipedEntityModel, CallbackInfo ci) {
        ItemStack stack = livingEntity.getEquippedStack(equipmentSlot);
        if (!stack.isEmpty()) {

            CompoundTag info = stack.getSubTag("CodeutilitiesTextureData");
            if (info != null && (info.contains("texture") || info.contains("model") || info.contains("armor"))) {
                ci.cancel();

                if (info.contains("armor")) {
                    this.getContextModel().setAttributes(bipedEntityModel);
                    setVisible(bipedEntityModel,equipmentSlot);
                    try {
                        Identifier id = cu_cache.computeIfAbsent(info.getString("armor"),(s) -> {
                            try {
                                NativeImage img = NativeImage.read(info.getString("armor"));
                                if (img.getWidth() * img.getHeight() > 64*4*32*4) return MissingSprite.getMissingSpriteId();
                                TextureManager tm = CodeUtilities.MC.getTextureManager();
                                NativeImageBackedTexture nibt = new NativeImageBackedTexture(img);
                                return tm.registerDynamicTexture("cu_armor", nibt);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return MissingSprite.getMissingSpriteId();
                        });

                        VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(id), false, stack.hasGlint());
                        bipedEntityModel.render(matrices, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }
        }
    }
}
