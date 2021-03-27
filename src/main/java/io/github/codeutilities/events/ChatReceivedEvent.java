package io.github.codeutilities.events;

import com.sun.org.apache.xpath.internal.operations.Mod;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.dfrpc.DFDiscordRPC;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.DFInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

public class ChatReceivedEvent {

    public static boolean pjoin = false;
    public static String dfrpcMsg = "";

    public static boolean cancelTimeMsg;
    public static boolean cancelNVisionMsg;
    public static boolean cancelFlyMsg;

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
            }else {
                try {
                    String[] lines = text.split("\n");
                    String cmd = "/join " + lines[2].replaceAll(" .* \\[(.*)\\]$", "$1");
                    System.out.println(lines[2] + " || " + lines[2].replaceAll("≫ .* \\[(.*)\\]$", "$1"));
                    if (cmd.matches("/join [0-9]+")) {
                        mc.player.sendChatMessage(cmd);
                    } else {
                        ChatUtil.sendMessage("Error while trying to join the plot.", ChatType.FAIL);
                    }
                    cancel = true;
                }catch (Exception e) {
                    e.printStackTrace();
                    ChatUtil.sendMessage("Error while trying to join the plot.", ChatType.FAIL);
                }
            }
            pjoin = false;
        }

        // cancel rpc /locate message
        if (DFDiscordRPC.locating) {
            if (message.getString().contains("\n§6You")) {
                dfrpcMsg = message.getString().replaceAll("§.", "");
                cancel = true;
                DFDiscordRPC.locating = false;
            }
        }

        // hide join/leave messages
        if (ModConfig.getConfig().hideJoinLeaveMessages) {
            if (mc.player.getUuid().equals("3134fb4d-a345-4c5e-9513-97c2c951223e")) {
                // debuggings
                System.out.println(message.toString());
            }
        }
        if (ModConfig.getConfig().hideJoinLeaveMessages
                && message.toString().contains("', siblings=[], style=Style{ color=gray, bold=")

                // check TextComponent
                && (message.toString().contains("bold=false") || message.toString().contains("bold=null"))
                && (message.toString().contains("italic=false") || message.toString().contains("italic=null"))
                && (message.toString().contains("underlined=false") || message.toString().contains("underlined=null"))
                && (message.toString().contains("strikethrough=false") || message.toString().contains("strikethrough=null"))
                && (message.toString().contains("obfuscated=false") || message.toString().contains("obfuscated=null"))
                && (message.toString().contains("clickEvent=false") || message.toString().contains("clickEvent=null"))
                && (message.toString().contains("hoverEvent=false") || message.toString().contains("hoverEvent=null"))
                && (message.toString().contains("insertion=false") || message.toString().contains("insertion=null"))

                && (message.getString().endsWith(" joined.") || message.getString().endsWith(" joined!") || message.getString().endsWith(" left.")) ) {

            // cancel message
            cancel = true;
        }

        if (cancelTimeMsg && text.contains("» Set your player time to " + ModConfig.getConfig().autotimeval + ".") && text.startsWith("»")) {
            cancel = true;
            cancelTimeMsg = false;
        }

        if (cancelNVisionMsg && text.contains("» Enabled night vision.") && text.startsWith("»")) {
            cancel = true;
            cancelNVisionMsg = false;
        }

        if (cancelFlyMsg && text.contains("» Flight enabled.") && text.startsWith("»")) {
            cancel = true;
            cancelFlyMsg = false;
        }

        //Cancelling (set cancel to true)
        if (cancel) {
            CodeUtilities.log(Level.INFO, "[CANCELLED] " + text);
            ci.cancel();
        }
    }
}
