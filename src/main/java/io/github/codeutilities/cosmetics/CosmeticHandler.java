package io.github.codeutilities.cosmetics;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.github.codeutilities.CodeUtilities;
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
    private static final MinecraftClient MC = MinecraftClient.getInstance();
    public static final ModelLoader MODEL_LOADER = new ModelLoader(MC.getResourceManager(), MC.getBlockColors(), MC.getProfiler(), MC.options.mipmapLevels);
    public static final SpriteAtlasManager SPRITE_ATLAS_MANAGER = MODEL_LOADER.upload(MinecraftClient.getInstance().getTextureManager(), MinecraftClient.getInstance().getProfiler());
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(10);

    private static final ArrayList<String> CACHED_HAT_UUIDS = new ArrayList<>();
    private static final ArrayList<BakedModel> CACHED_HAT_MODELS = new ArrayList<>();
    private static final ArrayList<JsonObject> CACHED_HAT_MODEL_ATTRIBUTES = new ArrayList<>();

    public static void applyCosmetics(UUID uuid, Map<MinecraftProfileTexture.Type, Identifier> identifierMap) {
        if (ModConfig.getConfig().cosmeticType == ModConfig.CosmeticType.Disabled) return;
        EXECUTOR_SERVICE.execute(() -> {
            try {
                String cape = getCosmetic(uuid, "cape");
                String hat = getCosmetic(uuid, "hat");

                if (cape != null) {
                    URL url = new URL("https://codeutilities.github.io/data/cosmetics/capes/" + cape);
                    Identifier identifier = MC.getTextureManager().registerDynamicTexture(uuid.toString().replaceAll("-", ""), new NativeImageBackedTexture(NativeImage.read(url.openStream())));
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
            JsonObject jsonObject = CodeUtilities.JSON_PARSER.parse(content).getAsJsonObject();
            JsonElement jsonElement = jsonObject.get(key);

            if (jsonElement.isJsonNull()) {
                if (ModConfig.getConfig().cosmeticType == ModConfig.CosmeticType.No_Event_Cosmetics) return null;
                content = WebUtil.getString("https://codeutilities.github.io/data/cosmetics/players/default.json");
                jsonObject = CodeUtilities.JSON_PARSER.parse(content).getAsJsonObject();
                jsonElement = jsonObject.get(key);
                if (jsonElement.isJsonNull()) return null;
            }

            return jsonElement.getAsString();
        } catch (JsonSyntaxException | IOException ignored) {
            if (ModConfig.getConfig().cosmeticType == ModConfig.CosmeticType.No_Event_Cosmetics) return null;
            content = WebUtil.getString("https://codeutilities.github.io/data/cosmetics/players/default.json");
            JsonObject jsonObject = CodeUtilities.JSON_PARSER.parse(content).getAsJsonObject();
            JsonElement jsonElement = jsonObject.get(key);
            if (!jsonElement.isJsonNull()) return jsonElement.getAsString();
        }
        return null;
    }

    public static BakedModel cacheModel(UUID uuid, String hat) throws IOException {

        if (hat != null) {
            String hatjson = WebUtil.getString("https://codeutilities.github.io/data/cosmetics/hats/" + hat);
            hatjson = hatjson.replaceAll("\t", "").replaceAll("\n", "");
            JsonObject attributes = CodeUtilities.JSON_PARSER.parse(hatjson).getAsJsonObject().get("attributes").getAsJsonObject();
            JsonUnbakedModel jsonUnbakedModel = JsonUnbakedModel.deserialize(hatjson);
            BakedModel model = jsonUnbakedModel.bake(CosmeticHandler.MODEL_LOADER, CosmeticHandler.SPRITE_ATLAS_MANAGER::getSprite, ModelRotation.X180_Y0, new Identifier("minecraft:placeholder"));

            if (!CACHED_HAT_UUIDS.contains(uuid.toString())) {
                CACHED_HAT_UUIDS.add(uuid.toString());
                CACHED_HAT_MODELS.add(model);
                CACHED_HAT_MODEL_ATTRIBUTES.add(attributes);
            } else {
                CACHED_HAT_MODEL_ATTRIBUTES.set(CACHED_HAT_UUIDS.indexOf(uuid.toString()), attributes);
                CACHED_HAT_MODELS.set(CACHED_HAT_UUIDS.indexOf(uuid.toString()), model);
            }
            return model;
        }
        return null;
    }

    public static BakedModel getModelFromUUID(UUID uuid) throws IOException {
        if (CACHED_HAT_UUIDS.contains(uuid.toString())) {
            return CACHED_HAT_MODELS.get(CACHED_HAT_UUIDS.indexOf(uuid.toString()));
        } else {
            return null;
        }
    }

    public static JsonObject getModelAttributesFromUUID(UUID uuid) throws IOException {
        if (CACHED_HAT_UUIDS.contains(uuid.toString())) {
            return CACHED_HAT_MODEL_ATTRIBUTES.get(CACHED_HAT_UUIDS.indexOf(uuid.toString()));
        } else {
            return null;
        }
    }

    public static void shutdownExecutorService() {
        EXECUTOR_SERVICE.shutdown();
    }
}
