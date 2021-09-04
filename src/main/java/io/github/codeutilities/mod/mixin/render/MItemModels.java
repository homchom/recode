package io.github.codeutilities.mod.mixin.render;

import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.renderer.BuiltinItemModel;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemModels.class)
public class MItemModels {

    @Inject(method = "getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/render/model/BakedModel;", at = @At("HEAD"), cancellable = true)
    private void getModel(ItemStack stack, CallbackInfoReturnable<BakedModel> cir) {
        if (!Config.getBoolean("betaItemTextures")) return;
        if (stack.getSubTag("CodeutilitiesTextureData") != null && stack.getSubTag("CodeutilitiesTextureData").contains("texture")) {
            cir.setReturnValue(new BuiltinItemModel());
            cir.cancel();
        }
    }

}
