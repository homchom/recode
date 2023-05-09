package io.github.homchom.recode.mod.mixin.render.screen;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.config.internal.DestroyItemResetType;
import io.github.homchom.recode.server.DF;
import io.github.homchom.recode.server.PlotMode;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public class MCreativeInventoryScreen {
    @Shadow @Nullable private Slot destroyItemSlot;

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    public void slotClicked(Slot slot, int invSlot, int clickData, ClickType actionType, CallbackInfo ci) {
        DestroyItemResetType resetType = Config.getEnum("destroyItemReset", DestroyItemResetType.class);
        if (resetType != DestroyItemResetType.OFF && DF.isInMode(DF.getCurrentDFState(), PlotMode.Dev)
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
            LegacyRecode.MC.player.connection.sendUnsignedCommand(cmd);
            ci.cancel();
        }
    }
}
