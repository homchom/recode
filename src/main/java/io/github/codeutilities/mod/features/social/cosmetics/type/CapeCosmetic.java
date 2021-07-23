package io.github.codeutilities.mod.features.social.cosmetics.type;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.sys.renderer.TextureMapAccessor;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CapeCosmetic implements CosmeticType {

    private final Map<String, Identifier> textureMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    public String getName() {
        return "cape";
    }

    @Override
    public void applyCosmetic(UUID uuid, String cosmeticId) {
        PlayerListEntry entry = CodeUtilities.MC.getNetworkHandler().getPlayerListEntry(uuid);
        if (textureMap.containsKey(cosmeticId)) {
            Identifier identifier = textureMap.get(cosmeticId);
            if (identifier == null) {
                return;
            }

            getTextures(entry).put(MinecraftProfileTexture.Type.CAPE, identifier);
            return;
        }

        try {
            URL url = new URL("https://codeutilities.github.io/data/cosmetics/capes/" + cosmeticId);
            Identifier identifier = CodeUtilities.MC.getTextureManager().registerDynamicTexture(cosmeticId, new NativeImageBackedTexture(NativeImage.read(url.openStream())));
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

    private static Map<MinecraftProfileTexture.Type, Identifier> getTextures(PlayerListEntry entry) {
        return ((TextureMapAccessor) entry).getTextures();
    }
}
