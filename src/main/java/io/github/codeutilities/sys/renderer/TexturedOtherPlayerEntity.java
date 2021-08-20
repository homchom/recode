package io.github.codeutilities.sys.renderer;

import com.mojang.authlib.GameProfile;
import io.github.codeutilities.CodeUtilities;
import java.util.UUID;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.util.Identifier;

public class TexturedOtherPlayerEntity extends OtherClientPlayerEntity {

    Identifier texture;

    public TexturedOtherPlayerEntity(Identifier texture) {
        super(CodeUtilities.MC.world, new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000000"),""));
        this.texture = texture;
    }

    @Override
    public Identifier getSkinTexture() {
        return texture;
    }

    @Override
    public boolean isPartVisible(PlayerModelPart modelPart) {
        return true;
    }

    @Override
    public boolean shouldRenderName() {
        return false;
    }
}
