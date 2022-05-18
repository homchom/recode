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
    private Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations;

    @Shadow
    private boolean pendingTextures;

    @Shadow
    private String skinModel;

    @Shadow
    @Final
    private GameProfile profile;

    @Overwrite
    public void registerTextures() {
        synchronized (this) {
            if (!this.pendingTextures) {
                this.pendingTextures = true;
                Minecraft.getInstance().getSkinManager().registerSkins(profile, (type, identifier, minecraftProfileTexture) -> {
                    this.textureLocations.put(type, identifier);
                    if (type == MinecraftProfileTexture.Type.SKIN) {
                        this.skinModel = minecraftProfileTexture.getMetadata("model");
                        if (this.skinModel == null) {
                            this.skinModel = "default";
                        }
                    }
                    CosmeticHandler.INSTANCE.applyCosmetics(profile.getId());

                }, true);
            }
        }
    }
}
