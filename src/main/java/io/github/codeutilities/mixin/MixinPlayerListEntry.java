package io.github.codeutilities.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.cosmetics.CosmeticHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(PlayerListEntry.class)
public class MixinPlayerListEntry {
    @Shadow @Final
    private Map<MinecraftProfileTexture.Type, Identifier> textures;
    
    @Shadow
    private boolean texturesLoaded;
    
    @Shadow
    private String model;
    
    @Shadow @Final
    private GameProfile profile;
    
    @Overwrite
    public void loadTextures() {
        synchronized(this) {
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
