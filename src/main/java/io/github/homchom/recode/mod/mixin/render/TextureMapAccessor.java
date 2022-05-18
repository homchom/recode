package io.github.homchom.recode.mod.mixin.render;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(PlayerInfo.class)
public interface TextureMapAccessor {
    @Accessor("textureLocations")
    Map<MinecraftProfileTexture.Type, ResourceLocation> getTextures();
}
