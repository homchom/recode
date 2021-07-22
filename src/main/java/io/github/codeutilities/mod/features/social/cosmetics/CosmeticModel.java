package io.github.codeutilities.mod.features.social.cosmetics;

import com.google.gson.JsonObject;
import net.minecraft.client.render.model.BakedModel;

public class CosmeticModel {

    final BakedModel model;
    final JsonObject attributes;

    public CosmeticModel(BakedModel model, JsonObject attributes) {
        this.model = model;
        this.attributes = attributes;
    }
}
