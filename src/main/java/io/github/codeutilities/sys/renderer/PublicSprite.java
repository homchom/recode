package io.github.codeutilities.sys.renderer;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;

public class PublicSprite extends Sprite {

    public PublicSprite(SpriteAtlasTexture spriteAtlasTexture, Info info, int maxLevel, int atlasWidth, int atlasHeight, int x, int y, NativeImage nativeImage) {
        super(spriteAtlasTexture, info, maxLevel, atlasWidth, atlasHeight, x, y, nativeImage);
    }
}
