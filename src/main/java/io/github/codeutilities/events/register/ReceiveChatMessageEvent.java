package io.github.codeutilities.events.register;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.CodeUtilsConfig;
import io.github.codeutilities.config.ConfigSounds;
import io.github.codeutilities.events.interfaces.ChatEvents;
import io.github.codeutilities.features.external.DFDiscordRPC;
import io.github.codeutilities.features.social.chat.ConversationTimer;
import io.github.codeutilities.gui.CPU_UsageText;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.codeutilities.util.chat.TextUtil;
import io.github.codeutilities.util.networking.DFInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.Level;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReceiveChatMessageEvent {
    public ReceiveChatMessageEvent() {
        ChatEvents.RECEIVE_MESSAGE.register(this::run);
    }

    public static boolean pjoin = false;
    public static String dfrpcMsg = "";

    public static boolean cancelTimeMsg;
    public static boolean cancelNVisionMsg;
    public static boolean cancelFlyMsg;
    public static boolean cancelAdminVanishMsg;
    public static boolean cancelLagSlayerMsg;

    public static int cancelMsgs = 0;

    private ActionResult run(Text message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String text = message.getString();

        boolean cancel = false;
        boolean showCancelMsg = true;

        if (mc.player == null) {
            return ActionResult.FAIL;
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
        if (ConversationTimer.currentConversation != null && text.toLowerCase().startsWith("[" + ConversationTimer.currentConversation.toLowerCase() + " → you] "))
            ConversationTimer.conversationUpdateTime = String.valueOf(System.currentTimeMillis());

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
        if (CodeUtilsConfig.getBoolean("highlight")) {
            String highlightMatcher = CodeUtilsConfig.getString("highlightMatcher").replaceAll("\\{name}", mc.player.getName().getString());

            if (( DFInfo.currentState != DFInfo.State.PLAY && msgWithoutColor.matches("^[^0-z]+.*[a-zA-Z]+: .*"))
                    || (DFInfo.currentState == DFInfo.State.PLAY && msgWithoutColor.matches("^.*[a-zA-Z]+: .*"))) {
                if ((!msgWithoutColor.matches("^.*" + highlightMatcher + ": .*")) || CodeUtilsConfig.getBoolean("highlightIgnoreSender")) {
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
                                newMsg = newMsg.substring(0, newMsgIter) + CodeUtilsConfig.getString("highlightPrefix").replaceAll("&", "§")
                                        + highlightMatcher + getColorCodes + newMsg.substring(newMsgIter).replaceFirst("^" + highlightMatcher, "");

                                newMsgIter = newMsgIter + CodeUtilsConfig.getString("highlightPrefix").length() + getColorCodes.toString().length();
                            }
                            newMsgIter++;
                        }
                        mc.player.sendMessage(TextUtil.colorCodesToTextComponent(newMsg), false);
                        if ((ConfigSounds.getByName(CodeUtilsConfig.getString("highlightSound")) != null) &&
                                (CodeUtilsConfig.getBoolean("highlightOwnSenderSound") || (!msgWithoutColor.matches("^.*" + highlightMatcher + ": .+")))) {
                            mc.player.playSound(ConfigSounds.getByName(CodeUtilsConfig.getString("highlightSound")), CodeUtilsConfig.getFloat("highlightSoundVolume"), 1);
                        }
                        cancel = true;
                    }
                }
            }
        }

        // hide join/leave messages
        if (CodeUtilsConfig.getBoolean("hideJoinLeaveMessages")
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
        if (CodeUtilsConfig.getBoolean("hideSessionSpy") && msgGetString.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=green")) {
            cancel = true;
        }

        // hide muted chat
        if (CodeUtilsConfig.getBoolean("hideMutedChat") && msgGetString.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=red")) {
            cancel = true;
        }

        // hide var scope messages (NOT WORKING RN)
        if (CodeUtilsConfig.getBoolean("hideVarScopeMessages")
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
        if (CodeUtilsConfig.getBoolean("hideMsgMatchingRegex") && msgGetString.replaceAll("§.", "").matches(CodeUtilsConfig.getString("hideMsgRegex"))) {
            cancel = true;
        }

        if (cancelTimeMsg && text.contains("» Set your player time to " + CodeUtilsConfig.getInteger("autotimeval") + ".") && text.startsWith("»")) {
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
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
