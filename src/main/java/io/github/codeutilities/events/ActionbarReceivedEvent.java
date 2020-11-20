package io.github.codeutilities.events;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.DFInfo;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ActionbarReceivedEvent {
    public static void onMessage(Text message, CallbackInfo ci) {
        //CodeUtilities.log(Level.INFO, "ACTIONBAR: " + message.getString());
        String text = message.getString();

        //Updating Player State
        //PLAY, BUILD and DEV is handled at ChatReceivedEvent
        if (text.matches("DiamondFire  - .* CP - ⛁ .* Credits")) {
            DFInfo.currentState = DFInfo.State.LOBBY;
        }
    }
}
