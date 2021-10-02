package io.github.codeutilities.mod.mixin.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.renderer.PublicSprite;
import java.util.Map;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel.GuiLight;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.Sprite.Info;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemModels.class)
public class MItemModels {

    int spriteIndex = 0;

    @Inject(method = "getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/render/model/BakedModel;", at = @At("HEAD"), cancellable = true)
    private void getModel(ItemStack stack, CallbackInfoReturnable<BakedModel> cir) {
        if (!Config.getBoolean("betaItemTextures")) {
            return;
        }
        CompoundTag info = stack.getSubTag("CodeutilitiesTextureData");
        if (info != null && (info.contains("texture") || info.contains("model"))) {

            if (CodeUtilities.modelCache.containsKey(info.toString())) {
                BakedModel m = CodeUtilities.modelCache.get(info.toString());
                if (m == null) {
                    return;
                }
                cir.setReturnValue(m);
                cir.cancel();
                return;
            }

            try {
                Sprite s = null;
                Identifier id = new Identifier("minecraft:cu_custom");
                if (info.contains("texture")) {
                    String tdata = info.getString("texture");
                    NativeImage texture = NativeImage.read(tdata);

                    SpriteAtlasTexture sat = CodeUtilities.MC.getBakedModelManager().method_24153(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

                    if (texture.getWidth() * texture.getHeight() > 64 * 64) {
                        s = MissingSprite.getMissingSprite(sat, 0, 0, 0, 0, 0);
                    } else {
                        sat.bindTexture();

                        int x = spriteIndex%16*64;
                        int y = 1024+spriteIndex/16*64;

                        spriteIndex++;
                        if (spriteIndex >= 256) {
                            spriteIndex = 0;
                        }
                        s = new PublicSprite(
                            sat,
                            new Info(id, texture.getWidth(), texture.getHeight(), AnimationResourceMetadata.EMPTY),
                            0, 1024, 2048,
                            x, y,
                            texture
                        );
                        s.upload();
                    }
                }

                ItemModelGenerator gen = new ItemModelGenerator();

                Map<String, Either<SpriteIdentifier, String>> textures = Maps.newHashMap();
                textures.put("layer0", Either.left(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, id)));

                JsonUnbakedModel unbaked;

                Sprite finalS = s; //lambda weirdness

                if (info.contains("model")) {
                    String model = info.getString("model");
                    unbaked = JsonUnbakedModel.deserialize(model);
                } else {
                    String parentRot = "apple";
                    if (info.contains("weapon")) {
                        parentRot = "diamond_sword";
                    }

                    unbaked = new JsonUnbakedModel(
                        id,
                        Lists.newArrayList(),
                        textures,
                        true,
                        GuiLight.field_21858,
                        ((JsonUnbakedModel) CodeUtilities.modelLoader.getOrLoadModel(new Identifier("item/" + parentRot))).getTransformations(),
                        Lists.newArrayList()
                    );
                    unbaked = gen.create(
                        i -> {
                            if (finalS != null && i.getTextureId().toString().contains("cu_custom")) return finalS;
                            return i.getSprite();
                        },
                        unbaked
                    );
                }

                BakedModel baked = unbaked.bake(
                    CodeUtilities.modelLoader,
                    i ->  {
                    if (finalS != null && i.getTextureId().toString().contains("cu_custom")) return finalS;
                    return i.getSprite();
                },
                    ModelRotation.X0_Y0,
                    id
                );

                CodeUtilities.modelCache.put(info.toString(), baked);
                cir.setReturnValue(baked);
                cir.cancel();
            } catch (Exception err) {
                err.printStackTrace();
                CodeUtilities.modelCache.put(info.toString(), null);
            }

        }
    }

}
