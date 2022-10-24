package io.github.homchom.recode.mod.mixin.render.screen;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.config.internal.DestroyItemResetType;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.DFInfo;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public class MCreativeInventoryScreen {
    @Shadow @Nullable private Slot destroyItemSlot;

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    public void slotClicked(Slot slot, int invSlot, int clickData, ClickType actionType, CallbackInfo ci) {
        DestroyItemResetType resetType = Config.getEnum("destroyItemReset", DestroyItemResetType.class);
        if (resetType != DestroyItemResetType.OFF && DFInfo.isOnDF() && DFInfo.currentState.getMode() == LegacyState.CurrentState.Mode.DEV
                && actionType == ClickType.QUICK_MOVE && slot == this.destroyItemSlot) {
            LegacyRecode.MC.setScreen(null);
            String cmd = "";
            switch (resetType) {
                case STANDARD:
                    cmd = "rs";
                    break;
                case COMPACT:
                    cmd = "rc";
                    break;
            }
            LegacyRecode.MC.player.commandUnsigned(cmd);
            ci.cancel();
        }
    }
}
