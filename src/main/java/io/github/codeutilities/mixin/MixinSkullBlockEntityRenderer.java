package io.github.codeutilities.mixin;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.SkullBlock.SkullType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkullBlockEntityRenderer.class)
public class MixinSkullBlockEntityRenderer {

    private static final Map TEXTURES = (Map) Util
        .make(Maps.newHashMap(), (hashMap) -> {
            hashMap.put(SkullBlock.Type.SKELETON,
                new Identifier("textures/entity/skeleton/skeleton.png"));
            hashMap.put(SkullBlock.Type.WITHER_SKELETON,
                new Identifier("textures/entity/skeleton/wither_skeleton.png"));
            hashMap
                .put(SkullBlock.Type.ZOMBIE, new Identifier("textures/entity/zombie/zombie.png"));
            hashMap.put(SkullBlock.Type.CREEPER,
                new Identifier("textures/entity/creeper/creeper.png"));
            hashMap.put(SkullBlock.Type.DRAGON,
                new Identifier("textures/entity/enderdragon/dragon.png"));
            hashMap.put(SkullBlock.Type.PLAYER, DefaultSkinHelper.getTexture());
        });
    private static boolean loading = false;

    @Inject(method = "method_3578", at = @At("HEAD"), cancellable = true)
    private static void method_3578(SkullType skullType, GameProfile gameProfile,
        CallbackInfoReturnable<RenderLayer> cir) {
        cir.cancel();
        Identifier identifier = (Identifier) TEXTURES.get(skullType);
        if (skullType == SkullBlock.Type.PLAYER && gameProfile != null) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            Map<Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider()
                .getTextures(gameProfile);
            cir.setReturnValue(map.containsKey(Type.SKIN) ? RenderLayer.getEntityTranslucent(
                loadSkin((MinecraftProfileTexture) map.get(Type.SKIN), Type.SKIN))
                : RenderLayer.getEntityCutoutNoCull(
                    DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(gameProfile))));
        } else {
            cir.setReturnValue(RenderLayer.getEntityCutoutNoCullZOffset(identifier));
        }
    }

    private static Identifier loadSkin(MinecraftProfileTexture profileTexture, Type skin) {
        try {
            String string = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
            Identifier identifier = new Identifier("skins/" + string);
            Field f = PlayerSkinProvider.class.getDeclaredField("textureManager");
            f.setAccessible(true);
            AbstractTexture abstractTexture = ((TextureManager) f
                .get(MinecraftClient.getInstance().getSkinProvider())).getTexture(identifier);
            if (abstractTexture == null) {
                if (!loading) {
                    loading = true;
                    new Thread(() -> {
                        MinecraftClient.getInstance().getSkinProvider()
                            .loadSkin(profileTexture, skin);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        loading = false;
                    }).start();
                }
                return DefaultSkinHelper.getTexture();
            }
            return MinecraftClient.getInstance().getSkinProvider().loadSkin(profileTexture, skin);
        } catch (Exception e) {
            e.printStackTrace();
            return DefaultSkinHelper.getTexture();
        }
    }

}
