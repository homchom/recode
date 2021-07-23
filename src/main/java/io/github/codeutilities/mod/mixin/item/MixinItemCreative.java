package io.github.codeutilities.mod.mixin.item;

import io.github.codeutilities.sys.templates.TemplateStorageHandler;
import io.github.codeutilities.sys.templates.TemplateUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinItemCreative {

    @Inject(method = "onCreativeInventoryAction", at = @At("HEAD"))
    public void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet, CallbackInfo ci) {
        ItemStack stack = packet.getItemStack();
        if (TemplateUtils.isTemplate(stack)) {
            TemplateStorageHandler.addTemplate(stack);
        }

    }

}




