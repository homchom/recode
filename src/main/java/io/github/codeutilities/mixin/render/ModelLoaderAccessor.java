package io.github.codeutilities.mixin.render;

import net.minecraft.client.render.model.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BakedModelManager.class)
public interface ModelLoaderAccessor {
    
    @Accessor("atlasManager")
    SpriteAtlasManager getAtlasManger();
}
