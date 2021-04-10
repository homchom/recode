package io.github.codeutilities.events;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.dfrpc.DFDiscordRPC;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

                System.out.println("support session? = " + DFDiscordRPC.supportSession);
            }
        }
        
        String msgToString = message.toString();
        String msgGetString = message.getString();

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

        // hide session spy
        if (ModConfig.getConfig().hideSessionSpy && msgGetString.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=green")) {
            cancel = true;
        }

        // hide muted chat
        if (ModConfig.getConfig().hideMutedChat && msgGetString.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=red")) {
            cancel = true;
        }

        // hide var scope messages
        if (ModConfig.getConfig().hideVarScopeMessages
        && (
                // local
                msgToString.equals("TextComponent{text='', siblings=[TextComponent{text='Scope set to ', siblings=[], style=Style{ color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, TextComponent{text='LOCAL', siblings=[], style=Style{ color=green, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, TextComponent{text=' (specific to event thread).', siblings=[], style=Style{ color=white, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}], style=Style{ color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}") ||
                // game
                msgToString.equals("TextComponent{text='', siblings=[TextComponent{text='Scope set to ', siblings=[], style=Style{ color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, TextComponent{text='GAME', siblings=[], style=Style{ color=gray, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, TextComponent{text=' (clears when all players leave).', siblings=[], style=Style{ color=white, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}], style=Style{ color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}") ||
                // save
                msgToString.equals("TextComponent{text='', siblings=[TextComponent{text='Scope set to ', siblings=[], style=Style{ color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, TextComponent{text='SAVE', siblings=[], style=Style{ color=yellow, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, TextComponent{text=' (will be saved).', siblings=[], style=Style{ color=white, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}], style=Style{ color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}")
                )) {
            cancel = true;
        }

        // hide msg matching regex
        if (ModConfig.getConfig().hideMsgMatchingRegex && msgGetString.replaceAll("§.", "").matches(ModConfig.getConfig().hideMsgRegex)) {
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
