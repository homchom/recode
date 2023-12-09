package io.github.homchom.recode.sys.renderer;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelPart;

import java.util.UUID;

public class TexturedOtherPlayerEntity extends RemotePlayer {

    ResourceLocation texture;

    public TexturedOtherPlayerEntity(ResourceLocation texture) {
        super(Minecraft.getInstance().level, new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000000"), ""));
        this.texture = texture;
    }

    @Override
    public boolean isModelPartShown(PlayerModelPart modelPart) {
        return true;
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }
}
