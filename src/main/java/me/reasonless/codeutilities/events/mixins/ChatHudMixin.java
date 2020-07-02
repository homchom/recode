package me.reasonless.codeutilities.events.mixins;

import me.reasonless.codeutilities.CodeUtilities;
import me.reasonless.codeutilities.events.ChatReceivedEvent;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {

  @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
  private void addMessage(Text message, CallbackInfo ci) {

    if (!CodeUtilities.hasblazing) ChatReceivedEvent.onMessage(message, ci);
  }

}
