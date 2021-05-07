package io.github.codeutilities.mixin.item;

import io.github.codeutilities.util.templates.TemplateStorageHandler;
import io.github.codeutilities.util.templates.TemplateUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinItemInsert {

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