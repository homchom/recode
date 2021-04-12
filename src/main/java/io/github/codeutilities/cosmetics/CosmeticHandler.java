package io.github.codeutilities.cosmetics;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.util.networking.WebUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CosmeticHandler {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static ModelLoader modelLoader = new ModelLoader(mc.getResourceManager(), mc.getBlockColors(), mc.getProfiler(), mc.options.mipmapLevels);
    public static SpriteAtlasManager spriteAtlasTexture = modelLoader.upload(MinecraftClient.getInstance().getTextureManager(), MinecraftClient.getInstance().getProfiler());
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static final ArrayList<String> cachedHatUUIDs = new ArrayList<>();
    private static final ArrayList<BakedModel> cachedHatModels = new ArrayList<>();
    private static final ArrayList<JsonObject> cachedHatModelAttributes = new ArrayList<>();

    public static void applyCosmetics(UUID uuid, Map<MinecraftProfileTexture.Type, Identifier> identifierMap) {
        if (ModConfig.getConfig().cosmeticType == ModConfig.CosmeticType.Disabled) return;
        executorService.execute(() -> {
            try {
                String cape = getCosmetic(uuid, "cape");
                String hat = getCosmetic(uuid, "hat");

                if (cape != null) {
                    URL url = new URL("https://codeutilities.github.io/data/cosmetics/capes/" + cape);
                    Identifier identifier = mc.getTextureManager().registerDynamicTexture(uuid.toString().replaceAll("-", ""), new NativeImageBackedTexture(NativeImage.read(url.openStream())));
                    identifierMap.put(MinecraftProfileTexture.Type.CAPE, identifier);
                }

                cacheModel(uuid, hat);

            } catch (IOException ignored) {
            }
        });
    }

    private static String getCosmetic(UUID uuid, String key) throws IOException {
        String content = null;
        if (ModConfig.getConfig().cosmeticType == ModConfig.CosmeticType.Disabled) return null;
        try {
            content = WebUtil.getString("https://codeutilities.github.io/data/cosmetics/players/" + uuid.toString() + ".json");
            JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
            JsonElement jsonElement = jsonObject.get(key);

            if (jsonElement.isJsonNull()) {
                if (ModConfig.getConfig().cosmeticType == ModConfig.CosmeticType.No_Event_Cosmetics) return null;
                content = WebUtil.getString("https://codeutilities.github.io/data/cosmetics/players/default.json");
                jsonObject = new JsonParser().parse(content).getAsJsonObject();
                jsonElement = jsonObject.get(key);
                if (jsonElement.isJsonNull()) return null;
            }

            return jsonElement.getAsString();
        } catch (JsonSyntaxException | IOException ignored) {
            if (ModConfig.getConfig().cosmeticType == ModConfig.CosmeticType.No_Event_Cosmetics) return null;
            content = WebUtil.getString("https://codeutilities.github.io/data/cosmetics/players/default.json");
            JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
            JsonElement jsonElement = jsonObject.get(key);
            if (!jsonElement.isJsonNull()) return jsonElement.getAsString();
        }
        return null;
    }

    public static BakedModel cacheModel(UUID uuid, String hat) throws IOException {

        if (hat != null) {
            String hatjson = WebUtil.getString("https://codeutilities.github.io/data/cosmetics/hats/" + hat);
            hatjson = hatjson.replaceAll("\t", "").replaceAll("\n", "");
            JsonObject attributes = new JsonParser().parse(hatjson).getAsJsonObject().get("attributes").getAsJsonObject();
            JsonUnbakedModel jsonUnbakedModel = JsonUnbakedModel.deserialize(hatjson);
            BakedModel model = jsonUnbakedModel.bake(CosmeticHandler.modelLoader, CosmeticHandler.spriteAtlasTexture::getSprite, ModelRotation.X180_Y0, new Identifier("minecraft:placeholder"));

            if (!cachedHatUUIDs.contains(uuid.toString())) {
                cachedHatUUIDs.add(uuid.toString());
                cachedHatModels.add(model);
                cachedHatModelAttributes.add(attributes);
            } else {
                cachedHatModelAttributes.set(cachedHatUUIDs.indexOf(uuid.toString()), attributes);
                cachedHatModels.set(cachedHatUUIDs.indexOf(uuid.toString()), model);
            }
            return model;
        }
        return null;
    }

    public static BakedModel getModelFromUUID(UUID uuid) throws IOException {
        if (cachedHatUUIDs.contains(uuid.toString())) {
            return cachedHatModels.get(cachedHatUUIDs.indexOf(uuid.toString()));
        } else {
            return null;
        }
    }

    public static JsonObject getModelAttributesFromUUID(UUID uuid) throws IOException {
        if (cachedHatUUIDs.contains(uuid.toString())) {
            return cachedHatModelAttributes.get(cachedHatUUIDs.indexOf(uuid.toString()));
        } else {
            return null;
        }
    }

    public static void shutdownExecutorService() {
        executorService.shutdown();
    }
}
