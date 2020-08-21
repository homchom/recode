package io.github.codeutilities.mixin.skull;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.SkullBlock.SkullType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.concurrent.*;

@Mixin(SkullBlockEntityRenderer.class)
public class MixinSkullBlockEntityRenderer {

    private static final Map<SkullType, Identifier> TEXTURES = Util
            .make(Maps.newHashMap(), (hashMap) -> {
                hashMap.put(SkullBlock.Type.SKELETON,
                        new Identifier("textures/entity/skeleton/skeleton.png"));
                hashMap.put(SkullBlock.Type.WITHER_SKELETON,
                        new Identifier("textures/entity/skeleton/wither_skeleton.png"));
                hashMap.put(SkullBlock.Type.ZOMBIE,
                        new Identifier("textures/entity/zombie/zombie.png"));
                hashMap.put(SkullBlock.Type.CREEPER,
                        new Identifier("textures/entity/creeper/creeper.png"));
                hashMap.put(SkullBlock.Type.DRAGON,
                        new Identifier("textures/entity/enderdragon/dragon.png"));
                hashMap.put(SkullBlock.Type.PLAYER, DefaultSkinHelper.getTexture());
            });

    private static List<String> loadingQueue = new ArrayList<>();
    private static final ExecutorService POOL = Executors.newCachedThreadPool();

    @Inject(method = "method_3578", at = @At("HEAD"), cancellable = true)
    private static void method_3578(SkullType skullType, GameProfile gameProfile, CallbackInfoReturnable<RenderLayer> cir) {
        cir.cancel();
        Identifier identifier = TEXTURES.get(skullType);
        if (skullType == SkullBlock.Type.PLAYER && gameProfile != null) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            Map<Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().getTextures(gameProfile);

            RenderLayer renderLayer;

            if (map.containsKey(Type.SKIN)) {
                renderLayer = RenderLayer.getEntityTranslucent(loadSkin(map.get(Type.SKIN), Type.SKIN));
            } else {
                renderLayer = RenderLayer.getEntityCutoutNoCull(DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(gameProfile)));
            }

            cir.setReturnValue(renderLayer);
        } else {
            cir.setReturnValue(RenderLayer.getEntityCutoutNoCullZOffset(identifier));
        }
    }

    private static Identifier loadSkin(MinecraftProfileTexture profileTexture, Type skin) {
        MinecraftClient client = MinecraftClient.getInstance();
        String string = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();

        // This retrieves the texture and checks if it's null or not.
        Identifier identifier = new Identifier("skins/" + string);

        // Get the texture from the skin provider.
        TextureManager manager = client.getTextureManager();
        AbstractTexture abstractTexture = manager.getTexture(identifier);

        // If it's null, then it hasn't loaded yet so we will provide a default skin until that is all setup.
        if (abstractTexture == null) {
            client.getSkinProvider().loadSkin(profileTexture, skin);
            return DefaultSkinHelper.getTexture();
        } else {
            //Return identifier if the skin isn't null (meaning it probably has loaded correctly).
            return identifier;
        }

    }

}
