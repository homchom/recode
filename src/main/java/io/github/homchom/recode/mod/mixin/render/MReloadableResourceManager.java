package io.github.homchom.recode.mod.mixin.render;

import io.github.homchom.recode.Recode;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.util.List;
import java.util.concurrent.*;

@Mixin(ReloadableResourceManager.class)
public class MReloadableResourceManager {
    @Inject(at = @At("HEAD"), method = "beginReloadInner")
    private void reload(Executor prepareExecutor, Executor applyExecutor, List<PackResources> listeners, CompletableFuture<Unit> initialStage) {
        Recode.modelCache.clear();
    }

}
