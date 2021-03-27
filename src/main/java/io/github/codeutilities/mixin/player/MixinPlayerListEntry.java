package io.github.codeutilities.mixin.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.github.codeutilities.cosmetics.CosmeticHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public class MixinPlayerListEntry {
    @Shadow
    @Final
    private Map<MinecraftProfileTexture.Type, Identifier> textures;

    @Shadow
    private boolean texturesLoaded;

    @Shadow
    private String model;

    @Shadow
    @Final
    private GameProfile profile;

    @Overwrite
    public void loadTextures() {
        synchronized (this) {
            if (!this.texturesLoaded) {
                this.texturesLoaded = true;
                MinecraftClient.getInstance().getSkinProvider().loadSkin(profile, (type, identifier, minecraftProfileTexture) -> {
                    this.textures.put(type, identifier);
                    if (type == MinecraftProfileTexture.Type.SKIN) {
                        this.model = minecraftProfileTexture.getMetadata("model");
                        if (this.model == null) {
                            this.model = "default";
                        }
                    }

                }, true);
                CosmeticHandler.applyCape(profile.getId(), textures);
            }

        }

    }


}
