package io.github.codeutilities.mixin.skull;

import com.google.common.cache.LoadingCache;
import com.google.common.hash.Hashing;
import com.mojang.authlib.minecraft.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

@Mixin(PlayerSkinProvider.class)
public class MixinPlayerSkinProvider {

    @Shadow
    @Final
    private TextureManager textureManager;

    @Shadow
    @Final
    private File skinCacheDir;

    @Shadow
    @Final
    private MinecraftSessionService sessionService;

    @Shadow
    @Final
    private LoadingCache<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCache;


    private final Map<Identifier, AbstractTexture> headQueueMap = new ConcurrentHashMap<>();
    private final List<Identifier> headQueue = new ArrayList<>();
    private final ExecutorService POOL = Executors.newCachedThreadPool();

    @Inject(method = "loadSkin(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/client/texture/PlayerSkinProvider$SkinTextureAvailableCallback;)Lnet/minecraft/util/Identifier;", at = @At("HEAD"), cancellable = true)
    private void loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type type, PlayerSkinProvider.SkinTextureAvailableCallback callback, CallbackInfoReturnable<Identifier> cir) {
        cir.cancel();
        String string = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
        Identifier identifier = new Identifier("skins/" + string);

        AbstractTexture abstractTexture = this.textureManager.getTexture(identifier);
        if (abstractTexture != null) {
            if (callback != null) {
                callback.onSkinTextureAvailable(type, identifier, profileTexture);
            }
        } else {
            AbstractTexture texture = headQueueMap.get(identifier);
            if (texture != null) {
                try {
                    Map<Identifier, AbstractTexture> textures = (Map<Identifier, AbstractTexture>) FieldUtils.readDeclaredField(textureManager, "textures", true);
                    textures.put(identifier, texture);
                    headQueueMap.remove(identifier);
                    headQueue.remove(identifier);
                    cir.setReturnValue(identifier);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            } else {
                if (headQueue.contains(identifier)) {
                    cir.setReturnValue(DefaultSkinHelper.getTexture());
                    return;
                } else {
                    headQueue.add(identifier);
                }

            }

            File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
            File file2 = new File(file, string);
            // Weird fix for skins not loading?
            try {
                file2.createNewFile();
            } catch (IOException ignored) {

            }
            PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(file2, profileTexture.getUrl(), DefaultSkinHelper.getTexture(), type == MinecraftProfileTexture.Type.SKIN, () -> {
                if (callback != null) {
                    callback.onSkinTextureAvailable(type, identifier, profileTexture);
                }

            });

            CompletableFuture.runAsync(() -> {
                try {
                    playerSkinTexture.load(MinecraftClient.getInstance().getResourceManager());
                    headQueueMap.put(identifier, playerSkinTexture);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, POOL);

            cir.setReturnValue(DefaultSkinHelper.getTexture());
            return;
        }

        cir.setReturnValue(identifier);
    }


}
