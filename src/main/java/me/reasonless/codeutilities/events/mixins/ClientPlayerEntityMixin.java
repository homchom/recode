package me.reasonless.codeutilities.events.mixins;

import me.reasonless.codeutilities.CodeUtilities;
import me.reasonless.codeutilities.events.ChatSend;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

  @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
  private void handleMessage(String message, CallbackInfo info) {
	  if (!CodeUtilities.hasblazing) ChatSend.onMessage(message, info);
  }

}
