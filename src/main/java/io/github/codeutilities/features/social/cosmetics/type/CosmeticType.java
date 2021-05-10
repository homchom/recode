package io.github.codeutilities.features.social.cosmetics.type;

import java.util.UUID;

public interface CosmeticType {

    String getName();

    void applyCosmetic(UUID playerEntity, String cosmeticId);

    void invalidateCache();

    CapeCosmetic CAPE = new CapeCosmetic();
    HatCosmetic HAT = new HatCosmetic();

    CosmeticType[] REGISTERED_COSMETICS = {
            CAPE,
            HAT
    };

}
