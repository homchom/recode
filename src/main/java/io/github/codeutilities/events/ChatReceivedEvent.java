package io.github.codeutilities.events;

import com.sun.org.apache.xpath.internal.operations.Mod;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.JereConfig;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatReceivedEvent {

    public static boolean pjoin = false;
    public static String dfrpcMsg = "";

    public static boolean cancelTimeMsg;
    public static boolean cancelNVisionMsg;
    public static boolean cancelFlyMsg;
    public static boolean cancelAdminVanishMsg;

    public static int cancelMsgs = 0;

    public static void onMessage(Text message, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String text = message.getString();

        boolean cancel = false;

        if (mc.player == null) {
            return;
        }

        if (cancelMsgs > 0) {
            cancelMsgs--;
            cancel = true;
        }

        //PJoin command
        if (pjoin) {
            String msg = text.replaceAll("§.", "");
            if (msg.startsWith("                                       \n")) {
                if (msg.contains(" is currently at spawn.\n")) {
                    ChatUtil.sendMessage("This player is not in a plot.", ChatType.FAIL);
                    cancel = true;
                }else {
                    // PLOT ID
                    Pattern pattern = Pattern.compile("\\[[0-9]+]\n");
                    Matcher matcher = pattern.matcher(msg);
                    String id = "";
                    while (matcher.find()) {
                        id = matcher.group();
                    }
                    id = id.replaceAll("\\[|]|\n", "");

                    String cmd = "/join " + id;

                    if (cmd.matches("/join [0-9]+")) {
                        mc.player.sendChatMessage(cmd);
                    } else {
                        ChatUtil.sendMessage("Error while trying to join the plot.", ChatType.FAIL);
                    }

                    cancel = true;
                }
                pjoin = false;
            }
        }

        // cancel rpc /locate message
        if (DFDiscordRPC.locating) {
            if (text.contains("\n§6You")) {
                dfrpcMsg = text.replaceAll("§.", "");
                cancel = true;
                DFDiscordRPC.locating = false;
            }
        }
        
        String msgToString = message.toString();
        String msgGetString = message.getString();

        /*
        if (mc.player.getUuid().toString().equals("3134fb4d-a345-4c5e-9513-97c2c951223e")) {
            // debuggings
            System.out.println(message.toString());
        }
         */

        // hide join/leave messages
        if (ModConfig.getConfig().hideJoinLeaveMessages
                && msgToString.contains("', siblings=[], style=Style{ color=gray, bold=")

                // check TextComponent
                && (msgToString.contains("bold=false") || msgToString.contains("bold=null"))
                && (msgToString.contains("italic=false") || msgToString.contains("italic=null"))
                && (msgToString.contains("underlined=false") || msgToString.contains("underlined=null"))
                && (msgToString.contains("strikethrough=false") || msgToString.contains("strikethrough=null"))
                && (msgToString.contains("obfuscated=false") || msgToString.contains("obfuscated=null"))
                && (msgToString.contains("clickEvent=false") || msgToString.contains("clickEvent=null"))
                && (msgToString.contains("hoverEvent=false") || msgToString.contains("hoverEvent=null"))
                && (msgToString.contains("insertion=false") || msgToString.contains("insertion=null"))

                && (msgGetString.endsWith(" joined.") || msgGetString.endsWith(" joined!") || msgGetString.endsWith(" left.")) ) {

            // cancel message
            cancel = true;
        }

        // streamer mode
        if (JereConfig.getConfig().streamerMode && (mc.player.getUuid().toString().equals("6c669475-3026-4603-b3e7-52c97681ad3a") || mc.player.getUuid().toString().equals("3134fb4d-a345-4c5e-9513-97c2c951223e"))
                && ((
                        msgGetString.startsWith("*") && msgToString.contains("text='*'") && (
                                // sessionspy
                                msgToString.contains("color=green") ||
                                // mutedchat
                                msgToString.contains("color=red") ||
                                // socialspy
                                msgToString.contains("color=#FF7F55")
                 ))
                || msgGetString.startsWith("[SUPPORT] ")
                || msgGetString.startsWith("[MOD] ")
                || msgGetString.startsWith("! Incoming Report (")
                || msgGetString.startsWith("Scanning ")
                || msgGetString.startsWith("§9§l» §3Support Question: §8(§7Click to answer§8)\nAsked by ")
                || msgGetString.matches("^» .* has answered .*'s question:")
        )) {
            cancel = true;
            if (msgGetString.startsWith("Scanning ")) cancelMsgs = 1;
            if (msgGetString.matches("^» .* has answered .*'s question:")) cancelMsgs = 2;
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

        // adminv msg cancel (streamer mode)
        if (cancelAdminVanishMsg && text.equals("You are no longer invisible.")) {
            cancel = true;
            cancelAdminVanishMsg = false;
        }

        //Cancelling (set cancel to true)
        if (cancel) {
            CodeUtilities.log(Level.INFO, "[CANCELLED] " + text);
            ci.cancel();
        }
    }
}
