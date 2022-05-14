package io.github.homchom.recode.mod.mixin.render;

import io.github.homchom.recode.Recode;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.List;
import java.util.concurrent.*;

@SuppressWarnings("ALL")
@Mixin(ReloadableResourceManager.class)
public class MReloadableResourceManager {
    @Inject(method = "createReload", at = @At("HEAD"))
    private void createReload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<PackResources> list, CallbackInfoReturnable ci) {
        Recode.modelCache.clear();
    }
}
