package io.github.homchom.recode.mod.features.social.cosmetics.type;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.NativeImage;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.mixin.render.TextureMapAccessor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.net.URL;
import java.util.*;

public class CapeCosmetic implements CosmeticType {

    private final Map<String, ResourceLocation> textureMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    public String getName() {
        return "cape";
    }

    @Override
    public void applyCosmetic(UUID uuid, String cosmeticId) {
        PlayerInfo entry = Recode.MC.getConnection().getPlayerInfo(uuid);
        if (textureMap.containsKey(cosmeticId)) {
            ResourceLocation identifier = textureMap.get(cosmeticId);
            if (identifier == null) {
                return;
            }

            getTextures(entry).put(MinecraftProfileTexture.Type.CAPE, identifier);
            return;
        }

        try {
            URL url = new URL("https://codeutilities.github.io/data/cosmetics/capes/" + cosmeticId);
            ResourceLocation identifier = Recode.MC.getTextureManager().register(cosmeticId, new DynamicTexture(NativeImage.read(url.openStream())));
            getTextures(entry).put(MinecraftProfileTexture.Type.CAPE, identifier);
            getTextures(entry).put(MinecraftProfileTexture.Type.ELYTRA, identifier);

            textureMap.put(cosmeticId, identifier);
        } catch (Exception e) {
            textureMap.put(cosmeticId, null);
            e.printStackTrace();
        }
    }

    // Implement proper cape cache purging
    @Override
    public void invalidateCache() {
    }

    private static Map<MinecraftProfileTexture.Type, ResourceLocation> getTextures(PlayerInfo entry) {
        return ((TextureMapAccessor) entry).getTextures();
    }
}
