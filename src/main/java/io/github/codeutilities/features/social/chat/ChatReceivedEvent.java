package io.github.codeutilities.features.social.chat;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.CodeUtilsConfig;
import io.github.codeutilities.config.ConfigSounds;
import io.github.codeutilities.features.external.DFDiscordRPC;
import io.github.codeutilities.util.render.gui.CPU_UsageText;
import io.github.codeutilities.util.networking.DFInfo;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.codeutilities.util.chat.TextUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatReceivedEvent {

    public static boolean pjoin = false;
    public static String dfrpcMsg = "";

    public static boolean cancelTimeMsg;
    public static boolean cancelNVisionMsg;
    public static boolean cancelFlyMsg;
    public static boolean cancelAdminVanishMsg;
    public static boolean cancelLagSlayerMsg;

    public static int cancelMsgs = 0;

    public static void onMessage(Text message, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String text = message.getString();

        boolean cancel = false;
        boolean showCancelMsg = true;

        if (mc.player == null) {
            return;
        }

        if (cancelMsgs > 0) {
            cancelMsgs--;
            cancel = true;
        }

        // cancel rpc /locate message
        if (DFDiscordRPC.locating) {
            if (text.contains("\nYou are currently")) {
                dfrpcMsg = text.replaceAll("§.", "");
                cancel = true;
                showCancelMsg = false;
                DFDiscordRPC.locating = false;
            }
        }


        // detect if player is in beta
        if (DFInfo.currentState == DFInfo.State.LOBBY && text.equals("◆ Welcome back to DiamondFire! ◆")) {
            DFInfo.isInBeta = false;
            Collection<String> lines = mc.world.getScoreboard().getKnownPlayers();
            for (String line : lines) {
                try {
                    if (line.startsWith("§aNode ") && (line.split(" ")[1]).equals("Beta§8")) {
                        DFInfo.isInBeta = true;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
        }

        // update conversation end timer
        if (DFInfo.currentConversation != null && text.toLowerCase().startsWith("[" + DFInfo.currentConversation.toLowerCase() + " → you] "))
            DFInfo.conversationUpdateTime = String.valueOf(System.currentTimeMillis());

        //LagSlayer enable/disable
        if (text.matches("^\\[LagSlayer\\] Now monitoring plot .*\\. Type /lagslayer to stop monitoring\\.$")) {
            CPU_UsageText.lagSlayerEnabled = true;
            if (cancelLagSlayerMsg) cancel = true;
            cancelLagSlayerMsg = false;
        }

        if (text.matches("^\\[LagSlayer\\] Stopped monitoring plot .*\\.$")) {
            CPU_UsageText.lagSlayerEnabled = false;
            if (cancelLagSlayerMsg) cancel = true;
            cancelLagSlayerMsg = false;
        }

        if (text.matches("^Error: You must be in a plot to use this command!$")) {
            CPU_UsageText.lagSlayerEnabled = false;
            if (cancelLagSlayerMsg) cancel = true;
            cancelLagSlayerMsg = false;
        }
        if (text.matches("^Error: You can't monitor this plot!$")) {
            CPU_UsageText.lagSlayerEnabled = false;
            if (cancelLagSlayerMsg) cancel = true;
            cancelLagSlayerMsg = false;
        }

        //PJoin command
        if (pjoin) {
            String msg = text.replaceAll("§.", "");
            if (msg.startsWith("                                       \n")) {
                if (msg.contains(" is currently at spawn\n")) {
                    ChatUtil.sendMessage("This player is not in a plot.", ChatType.FAIL);
                } else {
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

                }
                cancel = true;
                pjoin = false;
            }
        }

        String msgToString = message.toString();
        String msgGetString = message.getString();

        String msgWithColor = TextUtil.textComponentToColorCodes(message);
        String msgWithoutColor = msgWithColor.replaceAll("§.", "");

        // highlight name
        if (CodeUtilsConfig.getBool("highlight")) {
            String highlightMatcher = CodeUtilsConfig.getStr("highlightMatcher").replaceAll("\\{name}", mc.player.getName().getString());

            if (( DFInfo.currentState != DFInfo.State.PLAY && msgWithoutColor.matches("^[^0-z]+.*[a-zA-Z]+: .*"))
                    || (DFInfo.currentState == DFInfo.State.PLAY && msgWithoutColor.matches("^.*[a-zA-Z]+: .*"))) {
                if ((!msgWithoutColor.matches("^.*" + highlightMatcher + ": .*")) || CodeUtilsConfig.getBool("highlightIgnoreSender")) {
                    if (msgWithoutColor.contains(highlightMatcher)) {
                        String[] chars = msgWithColor.split("");
                        int i = 0;
                        int newMsgIter = 0;
                        StringBuilder getColorCodes = new StringBuilder();
                        String newMsg = msgWithColor;
                        String textLeft;

                        for (String currentChar : chars) {
                            textLeft = msgWithColor.substring(i) + " ";
                            i++;
                            if (currentChar.equals("§")) getColorCodes.append(currentChar).append(chars[i]);
                            if (textLeft.matches("^" + highlightMatcher + "[^a-zA-Z0-9].*")) {
                                newMsg = newMsg.substring(0, newMsgIter) + CodeUtilsConfig.getStr("highlightPrefix").replaceAll("&", "§")
                                        + highlightMatcher + getColorCodes + newMsg.substring(newMsgIter).replaceFirst("^" + highlightMatcher, "");

                                newMsgIter = newMsgIter + CodeUtilsConfig.getStr("highlightPrefix").length() + getColorCodes.toString().length();
                            }
                            newMsgIter++;
                        }
                        mc.player.sendMessage(TextUtil.colorCodesToTextComponent(newMsg), false);
                        if ((ConfigSounds.getSoundFromName(CodeUtilsConfig.getStr("highlightSound")) != null) &&
                                (CodeUtilsConfig.getBool("highlightOwnSenderSound") || (!msgWithoutColor.matches("^.*" + highlightMatcher + ": .+")))) {
                            mc.player.playSound(ConfigSounds.getSoundFromName(CodeUtilsConfig.getStr("highlightSound")), CodeUtilsConfig.getFloat("highlightSoundVolume"), 1);
                        }
                        cancel = true;
                    }
                }
            }
        }

        // hide join/leave messages
        if (CodeUtilsConfig.getBool("hideJoinLeaveMessages")
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

                && (msgGetString.endsWith(" joined.") || msgGetString.endsWith(" joined!") || msgGetString.endsWith(" left."))) {

            // cancel message
            cancel = true;
        }

        // hide session spy
        if (CodeUtilsConfig.getBool("hideSessionSpy") && msgGetString.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=green")) {
            cancel = true;
        }

        // hide muted chat
        if (CodeUtilsConfig.getBool("hideMutedChat") && msgGetString.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=red")) {
            cancel = true;
        }

        // hide var scope messages
        if (CodeUtilsConfig.getBool("hideVarScopeMessages")
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
        if (CodeUtilsConfig.getBool("hideMsgMatchingRegex") && msgGetString.replaceAll("§.", "").matches(CodeUtilsConfig.getStr("hideMsgRegex"))) {
            cancel = true;
        }

        if (cancelTimeMsg && text.contains("» Set your player time to " + CodeUtilsConfig.getInt("autotimeval") + ".") && text.startsWith("»")) {
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
            if (showCancelMsg) CodeUtilities.log(Level.INFO, "[CANCELLED] " + msgWithColor);
            ci.cancel();
        }
    }
}