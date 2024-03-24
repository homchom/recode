package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.event.SimpleValidated;
import io.github.homchom.recode.hypercube.state.DF;
import io.github.homchom.recode.hypercube.state.PlotMode;
import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.multiplayer.ReceiveMessageEvent;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import io.github.homchom.recode.sys.util.TextUtil;
import io.github.homchom.recode.ui.text.TextInterop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegacyReceiveChatMessageEvent {
    public LegacyReceiveChatMessageEvent() {
        ReceiveMessageEvent.Chat.INSTANCE.register(this::run);
    }

    public static boolean pjoin = false;

    public void run(SimpleValidated<Component> context) {
        var message = context.getValue();
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) context.invalidate();

        boolean cancel = false;

        // TODO: temporary, migrate all code here
        String msgToString = PlainTextComponentSerializer.plainText().serialize(message);

        String msgWithColor = TextUtil.toLegacyCodes(TextInterop.toVanilla(message));
        String msgWithoutColor = msgWithColor.replaceAll("§.", "");

        //PJoin command
        if (pjoin) {
            if (msgWithoutColor.startsWith("                                       \n")) {
                if (msgWithoutColor.contains(" is currently at spawn\n")) {
                    ChatUtil.sendMessage("This player is not in a plot.", ChatType.FAIL);
                } else {
                    // PLOT ID
                    Pattern pattern = Pattern.compile("\\[\\d+]\n");
                    Matcher matcher = pattern.matcher(msgWithoutColor);
                    String id = "";
                    while (matcher.find()) {
                        id = matcher.group();
                    }
                    id = id.replaceAll("[\\[\\]\n]", "");

                    String cmd = "join " + id;

                    if (cmd.matches("join \\d+")) {
                        mc.player.connection.sendUnsignedCommand(cmd);
                    } else {
                        ChatUtil.sendMessage("Error while trying to join the plot.", ChatType.FAIL);
                    }

                }
                cancel = true;
                pjoin = false;
            }
        }

        // highlight name
        if (LegacyConfig.getBoolean("highlight")) {
            String highlightMatcher = LegacyConfig.getString("highlightMatcher").replaceAll("\\{name}", mc.player.getName().getString());
            if (DF.isInMode(DF.getCurrentDFState(), PlotMode.Dev.ID) && (msgWithoutColor.matches("^[^0-z]+.*[a-zA-Z]+: .*")
                    || msgWithoutColor.matches("^.*[a-zA-Z]+: .*"))) {
                if ((!msgWithoutColor.matches("^.*" + highlightMatcher + ": .*")) || LegacyConfig.getBoolean("highlightIgnoreSender")) {
                    if (msgWithoutColor.contains(highlightMatcher)) {
                        if (!msgWithoutColor.contains("» Joined game: ") && !msgWithoutColor.contains(" by " + highlightMatcher + ".")) {
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
                                    newMsg = newMsg.substring(0, newMsgIter) + LegacyConfig.getString("highlightPrefix").replaceAll("&", "§")
                                            + highlightMatcher + getColorCodes + newMsg.substring(newMsgIter).replaceFirst("^" + highlightMatcher, "");

                                    newMsgIter = newMsgIter + LegacyConfig.getString("highlightPrefix").length() + getColorCodes.toString().length();
                                }
                                newMsgIter++;
                            }
                            mc.player.displayClientMessage(TextUtil.colorCodesToTextComponent(newMsg), false);
                            if (LegacyConfig.getBoolean("highlightOwnSenderSound") ||
                                    (!msgWithoutColor.matches("^.*" + highlightMatcher + ": .+"))) {
                                ChatUtil.playSound(
                                        LegacyConfig.getSound("highlightSound"), 1, LegacyConfig.getFloat("highlightSoundVolume"));
                            }
                            cancel = true;
                        }
                    }
                }
            }
        }

        // hide join/leave messages
        if (LegacyConfig.getBoolean("hideJoinLeaveMessages")
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

                && (msgToString.endsWith(" joined.") || msgToString.endsWith(" joined!") || msgToString.endsWith(" left."))) {

            // cancel message
            cancel = true;
        }

        // hide session spy
        if (LegacyConfig.getBoolean("hideSessionSpy") && msgToString.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=green")) {
            cancel = true;
        }

        // hide muted chat
        if (LegacyConfig.getBoolean("hideMutedChat") && msgToString.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=red")) {
            cancel = true;
        }

        if (DF.isInMode(DF.getCurrentDFState(), PlotMode.Dev.ID)) {
            // hide var scope messages
            if (LegacyConfig.getBoolean("hideVarScopeMessages") && msgToString.startsWith("Scope set to ")) {
                cancel = true;
            }

            if (LegacyConfig.getBoolean("autoClickEditMsgs") && msgToString.startsWith("⏵ Click to edit variable: ")) {
                if (message.style().clickEvent().action() == ClickEvent.Action.SUGGEST_COMMAND) {
                    String toOpen = message.style().clickEvent().value();
                    Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(new ChatScreen(toOpen)));
                }
            }
        }

        // hide msg matching regex
        if (LegacyConfig.getBoolean("hideMsgMatchingRegex") && msgToString.replaceAll("§.", "").matches(LegacyConfig.getString("hideMsgRegex"))) {
            cancel = true;
        }

        if (cancel) context.invalidate();
    }
}
