package me.reasonless.codeutilities.events;

import me.reasonless.codeutilities.CodeUtilities;
import me.reasonless.codeutilities.CodeUtilities.PlayMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ActionbarReceivedEvent {

  public static int autofly = 0;

  public static void onMessage(Text message, CallbackInfo ci) {
    MinecraftClient mc = MinecraftClient.getInstance();

    if (CodeUtilities.hasblazing) return;
    //AutoFly
    if (message.asFormattedString()
        .matches(".3DiamondFire .8 - .+ .+ CP.8 - .6. .+ Credits")) {
      autofly++;
      assert mc.player != null;
      if (autofly > 5 && CodeUtilities.p
          .getProperty("autofly")
          .equalsIgnoreCase("true") && !mc.player.abilities.allowFlying) {

        autofly = 0;
        mc.player.sendChatMessage("/fly");
      }
      //Update PlayMode
      CodeUtilities.playMode = PlayMode.SPAWN;
    }
  }

}
