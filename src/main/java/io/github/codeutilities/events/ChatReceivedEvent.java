package io.github.codeutilities.events;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.DFInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ChatReceivedEvent {

    public static void onMessage(Text message, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        //CodeUtilities.log(Level.INFO, "CHAT: " + message.getString());
        String text = message.getString();
        
        if (mc.player == null) {
            return;
        }
        
        //Patch Number detection
        if (text.matches("Current patch: .*\\. See the patch notes with \\/patch!")) {
            try {
                String patchText = text.replaceAll("Current patch: (.*)\\. See the patch notes with \\/patch!", "$1");

                DFInfo.isPatchNewer(patchText, "0"); //very lazy validation lol
                DFInfo.patchId = patchText;
                CodeUtilities.log(Level.INFO, "DiamondFire Patch " + DFInfo.patchId + " detected!");
            }catch (Exception e) {
                CodeUtilities.log(Level.INFO, "Error on parsing patch number!");
                e.printStackTrace();
            }
        }

        //Updating Player State
        //LOBBY is handled at ActionbarReceivedEvent
        //PLAY
        if (text.matches("Joined game: .* by .*") && text.startsWith("Joined game: ")) {
            DFInfo.currentState = DFInfo.State.PLAY;
        }
        //BUILD
        if (mc.player.isCreative() && text.contains("» You are now in build mode.") && text.startsWith("»")) {
            DFInfo.currentState = DFInfo.State.BUILD;
        }
        //DEV
        if (mc.player.isCreative() && text.contains("» You are now in dev mode.") && text.startsWith("»")) {
            DFInfo.currentState = DFInfo.State.DEV;
        }
    }
}
