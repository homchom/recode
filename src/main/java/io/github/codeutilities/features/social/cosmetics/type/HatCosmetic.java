package io.github.codeutilities.features.social.cosmetics.type;

import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.features.social.cosmetics.CosmeticModel;
import io.github.codeutilities.util.networking.WebUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HatCosmetic implements CosmeticType {

    private final Map<UUID, CosmeticModel> playerHatRegistry = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, CosmeticModel> hatRegistry = Collections.synchronizedMap(new HashMap<>());

    private final MinecraftClient mc = CodeUtilities.MC;
    private final ModelLoader modelLoader = new ModelLoader(mc.getResourceManager(), mc.getBlockColors(), mc.getProfiler(), mc.options.mipmapLevels);
    private final SpriteAtlasManager spriteAtlasTexture = modelLoader.upload(MinecraftClient.getInstance().getTextureManager(), MinecraftClient.getInstance().getProfiler());

    @Override
    public String getName() {
        return "hat";
    }

    // Avoid using get() == null to prevent loading invalid hats.
    @Override
    public void applyCosmetic(UUID uuid, String cosmeticId) {
        if (playerHatRegistry.containsKey(uuid)) {
            return;
        }

        CosmeticModel cosmeticModel = null;
        if (!hatRegistry.containsKey(cosmeticId)) {
            try {
                String hatData = WebUtil.getString("https://codeutilities.github.io/data/cosmetics/hats/" + cosmeticId);
                JsonObject hatJson = CodeUtilities.JSON_PARSER.parse(hatData).getAsJsonObject();
                JsonObject attributes = hatJson.get("attributes").getAsJsonObject();

                JsonUnbakedModel jsonUnbakedModel = JsonUnbakedModel.deserialize(hatData.replaceAll("\t", "").replaceAll("\n", "")); // Unsure if this replaceall is needed.
                BakedModel model = jsonUnbakedModel.bake(modelLoader, spriteAtlasTexture::getSprite, ModelRotation.X180_Y0, new Identifier("codeutilities:cosmetic"));

                cosmeticModel = new CosmeticModel(model, attributes);
                hatRegistry.put(cosmeticId, cosmeticModel);
            } catch (Exception e) {
                e.printStackTrace();
                // Hat failed, so insert null.
                hatRegistry.put("cosmeticId", null);
            }
        } else {
            cosmeticModel = hatRegistry.get(cosmeticId);
        }

        playerHatRegistry.put(uuid, cosmeticModel);
    }

    public CosmeticModel getPlayerHat(UUID uuid) {
        return playerHatRegistry.get(uuid);
    }

    @Override
    public void invalidateCache() {
        playerHatRegistry.clear();
        hatRegistry.clear();
    }
}
