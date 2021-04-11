package io.github.codeutilities.cosmetics;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.github.codeutilities.mixin.player.MixinPlayerListEntry;
import io.github.codeutilities.util.WebUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.glassfish.grizzly.streams.StreamReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CosmeticHandler {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static ArrayList<String> cachedHatUUIDs = new ArrayList<>();
    private static ArrayList<BakedModel> cachedHatModels = new ArrayList<>();
    private static ArrayList<JsonObject> cachedHatModelAttributes = new ArrayList<>();
    public static ModelLoader modelLoader = new ModelLoader(mc.getResourceManager(), mc.getBlockColors(), mc.getProfiler(), mc.options.mipmapLevels);
    public static SpriteAtlasManager spriteAtlasTexture = modelLoader.upload(MinecraftClient.getInstance().getTextureManager(), MinecraftClient.getInstance().getProfiler());

    public static void applyCosmetics(UUID uuid, Map<MinecraftProfileTexture.Type, Identifier> identifierMap) {
        executorService.execute(() -> {
            try {
                String cape = getCosmetic(uuid, "cape");
                String hat = getCosmetic(uuid, "hat");
                
                if(cape != null) {
                    URL url = new URL("https://codeutilities.github.io/data/cosmetics/capes/" + cape);
                    Identifier identifier = mc.getTextureManager().registerDynamicTexture(uuid.toString().replaceAll("-", ""), new NativeImageBackedTexture(NativeImage.read(url.openStream())));
                    identifierMap.put(MinecraftProfileTexture.Type.CAPE, identifier);
                }

                cacheModel(uuid, hat);

            } catch (IOException ignored) {}
        });
    }

    private static String getCosmetic(UUID uuid, String key) throws IOException {
        String content = null;
        try {
            content = WebUtil.getString("https://codeutilities.github.io/data/cosmetics/players/" + uuid.toString() + ".json");
            JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
            JsonElement jsonElement = jsonObject.get(key);

            if(jsonElement.isJsonNull()) {
                content = WebUtil.getString("https://codeutilities.github.io/data/cosmetics/players/default.json");
                jsonObject = new JsonParser().parse(content).getAsJsonObject();
                jsonElement = jsonObject.get(key);
                if (jsonElement.isJsonNull()) return null;
            }

            return jsonElement.getAsString();
        }catch(JsonSyntaxException | IOException ignored) {
            content = WebUtil.getString("https://codeutilities.github.io/data/cosmetics/players/default.json");
            JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
            JsonElement jsonElement = jsonObject.get(key);
            if (!jsonElement.isJsonNull()) return jsonElement.getAsString();
        }
        return null;
    }

    public static BakedModel cacheModel(UUID uuid, String hat) throws IOException {

        if(hat != null) {
            String hatjson = WebUtil.getString("https://codeutilities.github.io/data/cosmetics/hats/" + hat);
            hatjson = hatjson.replaceAll("\t", "").replaceAll("\n", "");
            JsonObject attributes = new JsonParser().parse(hatjson).getAsJsonObject().get("attributes").getAsJsonObject();
            JsonUnbakedModel jsonUnbakedModel = JsonUnbakedModel.deserialize(hatjson);
            BakedModel model = jsonUnbakedModel.bake(CosmeticHandler.modelLoader, CosmeticHandler.spriteAtlasTexture::getSprite, ModelRotation.X180_Y0, new Identifier("minecraft:placeholder"));

            if(!cachedHatUUIDs.contains(uuid.toString())) {
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
        if(cachedHatUUIDs.contains(uuid.toString())){
            return cachedHatModels.get(cachedHatUUIDs.indexOf(uuid.toString()));
        } else {
            return null;
        }
    }

    public static JsonObject getModelAttributesFromUUID(UUID uuid) throws IOException {
        if(cachedHatUUIDs.contains(uuid.toString())){
            return cachedHatModelAttributes.get(cachedHatUUIDs.indexOf(uuid.toString()));
        } else {
            return null;
        }
    }
    
    public static void shutdownExecutorService() {
        executorService.shutdown();
    }
}
