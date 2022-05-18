package io.github.homchom.recode.mod.mixin.inventory;

import io.github.homchom.recode.sys.hypercube.templates.*;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class MItemCreative {
    @Inject(method = "handleSetCreativeModeSlot", at = @At("HEAD"))
    public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket packet, CallbackInfo ci) {
        ItemStack stack = packet.getItem();
        if (TemplateUtils.isTemplate(stack)) {
            TemplateStorageHandler.addTemplate(stack);
        }
    }
}




