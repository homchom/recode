package io.github.codeutilities.mod.mixin.inventory;

import io.github.codeutilities.sys.templates.TemplateStorageHandler;
import io.github.codeutilities.sys.templates.TemplateUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MItemInsert {

    @Inject(method = "onInventory", at = @At("HEAD"))
    public void onCreativeInventoryAction(InventoryS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) {
            for (ItemStack stack : packet.getContents()) {
                if (TemplateUtils.isTemplate(stack)) {
                    TemplateStorageHandler.addTemplate(stack);
                }
            }
        }
    }


}