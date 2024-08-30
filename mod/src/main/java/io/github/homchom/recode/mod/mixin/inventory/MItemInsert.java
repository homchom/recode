package io.github.homchom.recode.mod.mixin.inventory;

import io.github.homchom.recode.sys.hypercube.templates.TemplateStorageHandler;
import io.github.homchom.recode.sys.hypercube.templates.TemplateUtil;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MItemInsert {
    @Inject(method = "handleContainerContent", at = @At("HEAD"))
    public void handleContainerContent(ClientboundContainerSetContentPacket packet, CallbackInfo ci) {
        if (packet.getContainerId() == 0) {
            for (ItemStack stack : packet.getItems()) {
                if (TemplateUtil.isTemplate(stack)) {
                    TemplateStorageHandler.addTemplate(stack);
                }
            }
        }
    }


}