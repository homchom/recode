package io.github.codeutilities.mod.features.social.cosmetics.type;

import java.util.UUID;

public interface CosmeticType {

    String getName();

    void applyCosmetic(UUID playerEntity, String cosmeticId);

    void invalidateCache();

    CapeCosmetic CAPE = new CapeCosmetic();

    CosmeticType[] REGISTERED_COSMETICS = {
            CAPE
    };

}
