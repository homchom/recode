package io.github.codeutilities.cosmetics;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.github.codeutilities.util.WebUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CosmeticHandler {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void applyCape(UUID uuid, Map<MinecraftProfileTexture.Type, Identifier> identifierMap) {
        executorService.execute(() -> {
            try {
                String cape = getCosmetic(uuid);

                if (cape != null) {
                    URL url = new URL("https://codeutilities.github.io/data/cosmetics/capes/" + cape);
                    Identifier identifier = mc.getTextureManager().registerDynamicTexture(uuid.toString().replaceAll("-", ""), new NativeImageBackedTexture(NativeImage.read(url.openStream())));
                    identifierMap.put(MinecraftProfileTexture.Type.CAPE, identifier);
                }

            } catch (IOException ignored) {
            }
        });
    }

    private static String getCosmetic(UUID uuid) throws IOException {
        String content = WebUtil.getString("https://codeutilities.github.io/data/cosmetics/players/" + uuid.toString() + ".json");
        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonElement jsonElement = jsonObject.get("cape");
        if (jsonElement.isJsonNull()) return null;
        return jsonElement.getAsString();
    }

    public static void shutdownExecutorService() {
        executorService.shutdown();
    }
}
