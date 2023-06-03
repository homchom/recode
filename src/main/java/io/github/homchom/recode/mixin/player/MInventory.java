package io.github.homchom.recode.mixin.player;

import io.github.homchom.recode.GlobalsKt;
import io.github.homchom.recode.feature.rendering.QuickChestVarsKt;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public abstract class MInventory {

    @Inject(method = "setItem", at = @At("RETURN"))
    private void onSetItem(int i, ItemStack itemStack, CallbackInfo ci) {
        if ((Object) this == GlobalsKt.getMc().player.getInventory()) {
            QuickChestVarsKt.recentItem(itemStack);
        }
    }

}
