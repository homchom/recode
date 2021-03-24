package io.github.codeutilities.mixin.player;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {
    
    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    public void getPlayerName(PlayerListEntry playerListEntry, CallbackInfoReturnable<Text> callbackInfoReturnable) {
//        MutableText star = new LiteralText("★ ").styled(style -> style.withColor(TextColor.fromRgb(41727)));
//        callbackInfoReturnable.setReturnValue(new LiteralText("").append(star).append(callbackInfoReturnable.getReturnValue()));
    }
}
