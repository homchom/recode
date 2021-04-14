package io.github.codeutilities.events;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.config.ConfigSounds;
import io.github.codeutilities.dfrpc.DFDiscordRPC;
import io.github.codeutilities.gui.CPU_UsageText;
import io.github.codeutilities.util.DFInfo;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.codeutilities.util.chat.TextUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
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
            if (text.contains("\n§6You")) {
                dfrpcMsg = text.replaceAll("§.", "");
                cancel = true;
                showCancelMsg = false;
                DFDiscordRPC.locating = false;
            }
        }

        //LagSlayer enable/disable
        if (text.matches("^\\[LagSlayer\\] Now monitoring plot ID: .*$")) {
            CPU_UsageText.monitorPlotId = text.replaceAll("\\[LagSlayer\\] Now monitoring plot ID: ", "");
            CPU_UsageText.lagSlayerEnabled = true;
            if (cancelLagSlayerMsg) cancel = true;
        }
        if (text.matches("^\\[LagSlayer\\] Stop monitoring by typing /lagslayer again\\.$")) {
            if (cancelLagSlayerMsg) cancel = true;
            cancelLagSlayerMsg = false;
        }

        if (text.matches("^\\[LagSlayer\\] No longer monitoring plot ID: .*$")) {
            CPU_UsageText.monitorPlotId = "";
            CPU_UsageText.lagSlayerEnabled = false;
            if (cancelLagSlayerMsg) cancel = true;
            cancelLagSlayerMsg = false;
        }
        if (text.matches("^\\[LagSlayer\\] Please join a plot to monitor it with LagSlayer\\.$")) {
            CPU_UsageText.monitorPlotId = "";
            CPU_UsageText.lagSlayerEnabled = false;
            if (cancelLagSlayerMsg) cancel = true;
            cancelLagSlayerMsg = false;
        }
        if (text.matches("^\\[LagSlayer\\] You do not have permission to monitor this plot\\.$")) {
            CPU_UsageText.monitorPlotId = "";
            CPU_UsageText.lagSlayerEnabled = false;
            if (cancelLagSlayerMsg) cancel = true;
            cancelLagSlayerMsg = false;
        }

        //PJoin command
        if (pjoin) {
            String msg = text.replaceAll("§.", "");
            if (msg.startsWith("                                       \n")) {
                if (msg.contains(" is currently at spawn.\n")) {
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
        if (ModConfig.getConfig(ModConfig.class).highlight) {
            String highlightMatcher = ModConfig.getConfig(ModConfig.Highlight_Text.class).highlightMatcher.replaceAll("\\{name}", mc.player.getName().getString());

            if (( DFInfo.currentState != DFInfo.State.PLAY && msgWithoutColor.matches("^[^0-z]+.*[a-zA-Z]+: .*"))
                    || (DFInfo.currentState == DFInfo.State.PLAY && msgWithoutColor.matches("^.*[a-zA-Z]+: .*"))) {
                if ((!msgWithoutColor.matches("^.*" + highlightMatcher + ": .*")) || ModConfig.getConfig(ModConfig.class).highlightIgnoreSender) {
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
                                newMsg = newMsg.substring(0, newMsgIter) + ModConfig.getConfig(ModConfig.Highlight_Text.class).highlightPrefix.replaceAll("&", "§")
                                        + highlightMatcher + getColorCodes.toString() + newMsg.substring(newMsgIter).replaceFirst("^" + highlightMatcher, "");

                                newMsgIter = newMsgIter + ModConfig.getConfig(ModConfig.Highlight_Text.class).highlightPrefix.length() + getColorCodes.toString().length();
                            }
                            newMsgIter++;
                        }
                        mc.player.sendMessage(TextUtil.colorCodesToTextComponent(newMsg), false);
                        if ((ModConfig.getConfig(ModConfig.Highlight_Sound.class).highlightSound != ConfigSounds.None) &&
                                (ModConfig.getConfig(ModConfig.Highlight_Sound.class).highlightOwnSenderSound || (!msgWithoutColor.matches("^.*" + highlightMatcher + ": .+")))) {
                            mc.player.playSound(ModConfig.getConfig(ModConfig.Highlight_Sound.class).highlightSound.getSound(), ModConfig.getConfig(ModConfig.Highlight_Sound.class).highlightSoundVolume, 1);
                        }
                        cancel = true;
                    }
                }
            }
        }

        // hide join/leave messages
        if (ModConfig.getConfig(ModConfig.class).hideJoinLeaveMessages
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
        if (ModConfig.getConfig(ModConfig.Hiding_Staff.class).hideSessionSpy && msgGetString.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=green")) {
            cancel = true;
        }

        // hide muted chat
        if (ModConfig.getConfig(ModConfig.Hiding_Staff.class).hideMutedChat && msgGetString.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=red")) {
            cancel = true;
        }

        // hide var scope messages
        if (ModConfig.getConfig(ModConfig.class).hideVarScopeMessages
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
        if (ModConfig.getConfig(ModConfig.Hiding_Regex.class).hideMsgMatchingRegex && msgGetString.replaceAll("§.", "").matches(ModConfig.getConfig(ModConfig.Hiding_Regex.class).hideMsgRegex)) {
            cancel = true;
        }

        if (cancelTimeMsg && text.contains("» Set your player time to " + ModConfig.getConfig(ModConfig.Automation_Time.class).autotimeval + ".") && text.startsWith("»")) {
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