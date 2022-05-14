package io.github.homchom.recode.mod.mixin.render;

import com.google.common.collect.*;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Either;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.renderer.PublicSprite;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite.Info;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.model.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@SuppressWarnings("ALL")
@Mixin(ItemModelShaper.class)
public class MItemModels {
    int spriteIndex = 0;

    @Inject(method = "getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("HEAD"), cancellable = true)
    private void getItemModel(ItemStack stack, CallbackInfoReturnable<BakedModel> cir) {
        if (!Config.getBoolean("betaItemTextures")) {
            return;
        }
        CompoundTag info = stack.getTagElement("RecodeTextureData");
        if (info != null && (info.contains("texture") || info.contains("model"))) {

            if (Recode.modelCache.containsKey(info.toString())) {
                BakedModel m = Recode.modelCache.get(info.toString());
                if (m == null) {
                    return;
                }
                cir.setReturnValue(m);
                cir.cancel();
                return;
            }

            try {
                TextureAtlasSprite s = null;
                ResourceLocation id = new ResourceLocation("minecraft:cu_custom");
                if (info.contains("texture")) {
                    String tdata = info.getString("texture");
                    NativeImage texture = NativeImage.fromBase64(tdata);

                    TextureAtlas sat = Recode.MC.getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);

                    if (texture.getWidth() * texture.getHeight() > 64 * 64) {
                        s = MissingTextureAtlasSprite.newInstance(sat, 0, 0, 0, 0, 0);
                    } else {
                        sat.bind();

                        int x = spriteIndex%16*64;
                        int y = 1024+spriteIndex/16*64;

                        spriteIndex++;
                        if (spriteIndex >= 256) {
                            spriteIndex = 0;
                        }
                        s = new PublicSprite(
                            sat,
                            new Info(id, texture.getWidth(), texture.getHeight(), AnimationMetadataSection.EMPTY),
                            0, 1024, 2048,
                            x, y,
                            texture
                        );
                        s.uploadFirstFrame();
                    }
                }

                ItemModelGenerator gen = new ItemModelGenerator();

                Map<String, Either<Material, String>> textures = Maps.newHashMap();
                textures.put("layer0", Either.left(new Material(InventoryMenu.BLOCK_ATLAS, id)));

                BlockModel unbaked;

                TextureAtlasSprite finalS = s; //lambda weirdness

                if (info.contains("model")) {
                    String model = info.getString("model");
                    unbaked = BlockModel.fromString(model);
                } else {
                    String parentRot = "apple";
                    if (info.contains("weapon")) {
                        parentRot = "diamond_sword";
                    }

                    unbaked = new BlockModel(
                        id,
                        Lists.newArrayList(),
                        textures,
                        true,
                        BlockModel.GuiLight.FRONT,
                        ((BlockModel) Recode.modelLoader.getModel(new ResourceLocation("item/" + parentRot))).getTransforms(),
                        Lists.newArrayList()
                    );
                    unbaked = gen.generateBlockModel(
                        i -> {
                            if (finalS != null && i.texture().toString().contains("cu_custom")) return finalS;
                            return i.sprite();
                        },
                        unbaked
                    );
                }

                BakedModel baked = unbaked.bake(
                    Recode.modelLoader,
                    i ->  {
                    if (finalS != null && i.texture().toString().contains("cu_custom")) return finalS;
                    return i.sprite();
                },
                    BlockModelRotation.X0_Y0,
                    id
                );

                Recode.modelCache.put(info.toString(), baked);
                cir.setReturnValue(baked);
                cir.cancel();
            } catch (Exception err) {
                err.printStackTrace();
                Recode.modelCache.put(info.toString(), null);
            }

        }
    }
}
