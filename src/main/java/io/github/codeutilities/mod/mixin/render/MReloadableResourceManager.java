package io.github.codeutilities.mod.mixin.render;

import io.github.codeutilities.CodeUtilities;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReloadableResourceManagerImpl.class)
public class MReloadableResourceManager {

    @Inject(at = @At("HEAD"), method = "beginReloadInner")
    private void reload(Executor prepareExecutor, Executor applyExecutor, List<ResourceReloadListener> listeners, CompletableFuture<Unit> initialStage, CallbackInfoReturnable<ResourceReloadMonitor> cir) {
        CodeUtilities.textureCache.clear();
    }

}
