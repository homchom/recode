package io.github.homchom.recode.mod.mixin.render.player;

import com.google.common.hash.Hashing;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import io.github.homchom.recode.Recode;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(SkinManager.class)
public class MSkinManager {
    long loadingTexture = 0;

    @Shadow
    @Final
    private TextureManager textureManager;

    // Complete overwrite for loadSkin method in net.minecraft.client.texture.PlayerSkinProvider
    @Shadow
    @Final
    private File skinsDirectory;

    @Inject(method = "registerTexture(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/client/resources/PlayerSkinProvider$SkinTextureAvailableCallback;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void registerTexture(MinecraftProfileTexture profileTexture, Type type,
                          @Nullable SkinManager.SkinTextureCallback callback,
                          CallbackInfoReturnable<ResourceLocation> cir) {
        String string = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
        ResourceLocation identifier = new ResourceLocation("skins/" + string);
        AbstractTexture abstractTexture = this.textureManager.getTexture(identifier);
        if (abstractTexture != null) {
            if (callback != null) {
                callback.onSkinTextureAvailable(type, identifier, profileTexture);
            }
        } else {
            if (loadingTexture < System.currentTimeMillis() - 5000) {
                loadingTexture = System.currentTimeMillis();
                File file = new File(skinsDirectory, string.length() > 2 ? string.substring(0, 2) : "xx");
                File file2 = new File(file, string);
                ResourceLocation finalIdentifier = identifier;
                new Thread(() -> {
                    HttpTexture playerSkinTexture = new HttpTexture(file2,
                            profileTexture.getUrl(), DefaultPlayerSkin
                            .getDefaultSkin(), type == Type.SKIN, () -> {
                        if (callback != null) {
                            callback.onSkinTextureAvailable(type, finalIdentifier, profileTexture);
                        }

                    });
                    loadingTexture = 0;
                    Recode.MC.execute(() -> this.textureManager.register(finalIdentifier, playerSkinTexture));
                }).start();
            }
            identifier = DefaultPlayerSkin.getDefaultSkin();
        }

        cir.setReturnValue(identifier);
        cir.cancel();
    }

}
