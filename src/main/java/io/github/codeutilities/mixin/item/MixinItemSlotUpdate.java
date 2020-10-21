package io.github.codeutilities.mixin.item;

import io.github.codeutilities.template.TemplateStorageHandler;
import io.github.codeutilities.util.TemplateUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.util.registry.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinItemSlotUpdate {

    @Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"))
    public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) {
            ItemStack stack = packet.getItemStack();
            if (TemplateUtils.isTemplate(stack)) {
                TemplateStorageHandler.addTemplate(stack);
            }
        }
    }


}
