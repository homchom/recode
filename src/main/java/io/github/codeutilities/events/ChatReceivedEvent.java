package io.github.codeutilities.events;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.DFInfo;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ChatReceivedEvent {
    public static void onMessage(Text message, CallbackInfo ci) {
        //CodeUtilities.log(Level.INFO, "CHAT: " + message.getString());
        String text = message.getString();

        //Patch Number detection
        if (text.matches("Current patch: .*\\. See the patch notes with \\/patch!")) {
            try {
                String patchText = text.replaceAll("Current patch: (.*)\\. See the patch notes with \\/patch!", "$1");

                DFInfo.PATCH_ID = patchText;
                CodeUtilities.log(Level.INFO, "DiamondFire Patch " + DFInfo.PATCH_ID + " detected!");
            }catch (Exception e) {
                CodeUtilities.log(Level.INFO, "Error on parsing patch number!");
                e.printStackTrace();
            }
        }
    }
}
