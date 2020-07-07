package me.reasonless.codeutilities.events;

import me.reasonless.codeutilities.CodeUtilities;
import me.reasonless.codeutilities.CodeUtilities.PlayMode;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ChatSend {

  public static void onMessage(String msg, CallbackInfo info) {
	  
	  if (CodeUtilities.hasblazing) return;
	  
    if (msg.startsWith("/build")) {
      CodeUtilities.playMode = PlayMode.BUILD;
    }
    if (msg.startsWith("/dev")) {
      CodeUtilities.playMode = PlayMode.DEV;
    }
    if (msg.startsWith("/play")) {
      CodeUtilities.playMode = PlayMode.PLAY;
    }
    if (msg.startsWith("/spawn") || msg.startsWith("/leave") || msg.startsWith("/server")) {
      CodeUtilities.playMode = PlayMode.SPAWN;
    }

  }

}
