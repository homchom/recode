package io.github.homchom.recode.mod.mixin.inventory;

import io.github.homchom.recode.sys.hypercube.templates.*;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MItemInsert {

    @Inject(method = "onInventory", at = @At("HEAD"))
    public void onCreativeInventoryAction(ClientboundContainerSetContentPacket packet, CallbackInfo ci) {
        if (packet.getContainerId() == 0) {
            for (ItemStack stack : packet.getItems()) {
                if (TemplateUtils.isTemplate(stack)) {
                    TemplateStorageHandler.addTemplate(stack);
                }
            }
        }
    }


}