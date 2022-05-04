package io.github.homchom.recode.mod.mixin.render.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.github.homchom.recode.mod.features.social.cosmetics.CosmeticHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;

import java.util.Map;

@Mixin(PlayerInfo.class)
public class MPlayerListEntry {
    @Shadow
    @Final
    private Map<MinecraftProfileTexture.Type, ResourceLocation> textures;

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
                Minecraft.getInstance().getSkinManager().registerSkins(profile, (type, identifier, minecraftProfileTexture) -> {
                    this.textures.put(type, identifier);
                    if (type == MinecraftProfileTexture.Type.SKIN) {
                        this.model = minecraftProfileTexture.getMetadata("model");
                        if (this.model == null) {
                            this.model = "default";
                        }
                    }
                    CosmeticHandler.INSTANCE.applyCosmetics(profile.getId());

                }, true);
            }

        }

    }


}
