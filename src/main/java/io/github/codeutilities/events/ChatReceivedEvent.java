package io.github.codeutilities.events;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.dfrpc.DFDiscordRPC;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ChatReceivedEvent {

    public static boolean pjoin = false;
    public static String dfrpcMsg = "";

    public static void onMessage(Text message, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String text = message.getString();

        boolean cancel = false;

        if (mc.player == null) {
            return;
        }

        //PJoin command (broken right now)
        if (pjoin) {
            System.out.println(text.replaceAll("§", "&"));

            if (text.contains("is currently at§6 spawn.")) {
                ChatUtil.sendMessage("This player is not in a plot.", ChatType.FAIL);
            } else {
                try {
                    String[] lines = text.split("\n");
                    String cmd = "/join " + lines[2].replaceAll(" .* \\[(.*)]$", "$1");
                    System.out.println(lines[2] + " || " + lines[2].replaceAll("≫ .* \\[(.*)]$", "$1"));
                    if (cmd.matches("/join [0-9]+")) {
                        mc.player.sendChatMessage(cmd);
                    } else {
                        ChatUtil.sendMessage("Error while trying to join the plot.", ChatType.FAIL);
                    }
                    cancel = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    ChatUtil.sendMessage("Error while trying to join the plot.", ChatType.FAIL);
                }
            }
            pjoin = false;
        }

        if (DFDiscordRPC.locating) {
            if (message.getString().contains("\nYou are")) {
                dfrpcMsg = message.getString();
                cancel = true;
                DFDiscordRPC.locating = false;
            }
        }

        //Cancelling (set cancel to true)
        if (cancel) {
            CodeUtilities.log(Level.INFO, "[CANCELLED] " + text);
            ci.cancel();
        }
    }
}
