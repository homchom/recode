package io.github.codeutilities.mod.mixin.player;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public interface TextureMapAccessor {
    @Accessor("textures")
    Map<MinecraftProfileTexture.Type, Identifier> getTextures();
}
