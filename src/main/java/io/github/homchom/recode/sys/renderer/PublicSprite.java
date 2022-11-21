package io.github.homchom.recode.sys.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class PublicSprite extends TextureAtlasSprite {

    public PublicSprite(TextureAtlas spriteAtlasTexture, Info info, int maxLevel, int atlasWidth, int atlasHeight, int x, int y, NativeImage nativeImage) {
        super(spriteAtlasTexture, info, maxLevel, atlasWidth, atlasHeight, x, y, nativeImage);
    }
}
