package io.github.homchom.recode.mod.features.social.cosmetics;

import com.google.gson.*;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.social.cosmetics.type.CosmeticType;
import io.github.homchom.recode.sys.file.ILoader;
import io.github.homchom.recode.sys.networking.WebUtil;

import java.util.*;
import java.util.concurrent.*;

public class CosmeticHandler implements ILoader {

    public static final CosmeticHandler INSTANCE = new CosmeticHandler();

    private final ExecutorService executorService = Executors.newFixedThreadPool(2); // Don't allocate too many threads.

    private JsonObject CACHED_DEFAULTS = null;
    private final HashSet<UUID> specialUsers = new HashSet<>();

    public void applyCosmetics(UUID uuid) {
        if (!Config.getBoolean("cosmeticsEnabled")) {
            return;
        }

        executorService.execute(() -> {
            JsonObject cosmetics = getPlayerCosmetics(uuid);
            if (cosmetics == null) {
                return;
            }

            if (!specialUsers.contains(uuid)) { // Don't try to load user data from users without cosmetics.
                return;
            }

            for (CosmeticType type : CosmeticType.REGISTERED_COSMETICS) {
                JsonElement element = cosmetics.get(type.getName());
                if (element != null && !element.isJsonNull()) {
                    type.applyCosmetic(uuid, element.getAsString());
                }
            }
        });
    }

    private JsonObject getPlayerCosmetics(UUID uuid) {
        JsonObject playerCosmetics = WebUtil.getObject("https://codeutilities.github.io/data/cosmetics/players/" + uuid.toString() + ".json");
        if (playerCosmetics == null) {
            if (Config.getBoolean("cosmeticsEnabled")) {
                if (CACHED_DEFAULTS == null) {
                    CACHED_DEFAULTS = WebUtil.getObject("https://codeutilities.github.io/data/cosmetics/players/default.json");
                }
                return CACHED_DEFAULTS;
            }

            return null;
        } else {
            return playerCosmetics;
        }
    }

    public void shutdownExecutorService() {
        executorService.shutdown();
    }

    @Override
    public void load() {
        try {
            JsonObject object = WebUtil.getObject("https://raw.githubusercontent.com/CodeUtilities/data/main/cosmetics/players/registry.json");
            for (JsonElement element : object.getAsJsonArray("registry")) {
                specialUsers.add(UUID.fromString(element.getAsString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void invalidateCache() {
        specialUsers.clear();
    }
}
