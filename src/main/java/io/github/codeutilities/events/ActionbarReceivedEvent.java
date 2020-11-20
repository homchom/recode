package io.github.codeutilities.events;

import io.github.codeutilities.CodeUtilities;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ActionbarReceivedEvent {
    public static void onMessage(Text message, CallbackInfo ci) {
        //CodeUtilities.log(Level.INFO, "ACTIONBAR: " + message.getString());
    }
}
