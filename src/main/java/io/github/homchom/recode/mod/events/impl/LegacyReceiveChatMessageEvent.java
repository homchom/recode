package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.*;
import io.github.homchom.recode.event.*;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.player.chat.*;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;

import java.util.regex.*;

public class LegacyReceiveChatMessageEvent {
    public LegacyReceiveChatMessageEvent() {
        RecodeEvents.ReceiveChatMessage.listen(this::run);
    }

    public static boolean pjoin = false;

    public static String tipPlayer = "";

    private void run(EventValidator result, Component message) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) {
            result.setValid(false);
            return;
        }

        String stripped = message.getString();

        boolean cancel = false;

        //PJoin command
        if (pjoin) {
            String msg = stripped.replaceAll("§.", "");
            if (msg.startsWith("                                       \n")) {
                if (msg.contains(" is currently at spawn\n")) {
                    ChatUtil.sendMessage("This player is not in a plot.", ChatType.FAIL);
                } else {
                    // PLOT ID
                    Pattern pattern = Pattern.compile("\\[\\d+]\n");
                    Matcher matcher = pattern.matcher(msg);
                    String id = "";
                    while (matcher.find()) {
                        id = matcher.group();
                    }
                    id = id.replaceAll("[\\[\\]\n]", "");

                    String cmd = "/join " + id;

                    if (cmd.matches("/join \\d+")) {
                        mc.player.commandSigned(cmd, null);
                    } else {
                        ChatUtil.sendMessage("Error while trying to join the plot.", ChatType.FAIL);
                    }

                }
                cancel = true;
                pjoin = false;
            }
        }

        String msgToString = message.toString();

        String msgWithColor = TextUtil.textComponentToColorCodes(message);
        String msgWithoutColor = msgWithColor.replaceAll("§.", "");

        // highlight name
        if (Config.getBoolean("highlight")) {
            String highlightMatcher = Config.getString("highlightMatcher").replaceAll("\\{name}", mc.player.getName().getString());

            if (( DFInfo.currentState.getMode() != LegacyState.Mode.PLAY && msgWithoutColor.matches("^[^0-z]+.*[a-zA-Z]+: .*"))
                    || (DFInfo.currentState.getMode() == LegacyState.Mode.PLAY && msgWithoutColor.matches("^.*[a-zA-Z]+: .*"))) {
                if ((!msgWithoutColor.matches("^.*" + highlightMatcher + ": .*")) || Config.getBoolean("highlightIgnoreSender")) {
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
                                newMsg = newMsg.substring(0, newMsgIter) + Config.getString("highlightPrefix").replaceAll("&", "§")
                                        + highlightMatcher + getColorCodes + newMsg.substring(newMsgIter).replaceFirst("^" + highlightMatcher, "");

                                newMsgIter = newMsgIter + Config.getString("highlightPrefix").length() + getColorCodes.toString().length();
                            }
                            newMsgIter++;
                        }
                        mc.player.displayClientMessage(TextUtil.colorCodesToTextComponent(newMsg), false);
                        if (Config.getBoolean("highlightOwnSenderSound") ||
                                (!msgWithoutColor.matches("^.*" + highlightMatcher + ": .+"))) {
                            ChatUtil.playSound(
                                    Config.getSound("highlightSound"), 1, Config.getFloat("highlightSoundVolume"));
                        }
                        cancel = true;
                    }
                }
            }
        }

        // hide join/leave messages
        if (Config.getBoolean("hideJoinLeaveMessages")
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

                && (stripped.endsWith(" joined.") || stripped.endsWith(" joined!") || stripped.endsWith(" left."))) {

            // cancel message
            cancel = true;
        }

        // hide session spy
        if (Config.getBoolean("hideSessionSpy") && stripped.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=green")) {
            cancel = true;
        }

        // hide muted chat
        if (Config.getBoolean("hideMutedChat") && stripped.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=red")) {
            cancel = true;
        }

        if (DFInfo.currentState.getMode() == LegacyState.Mode.DEV) {
            // hide var scope messages
            if (Config.getBoolean("hideVarScopeMessages") && stripped.startsWith("Scope set to ")) {
                cancel = true;
            }

            if (Config.getBoolean("autoClickEditMsgs") && stripped.startsWith("⏵ Click to edit variable: ")) {
                if (message.getStyle().getClickEvent().getAction() == Action.SUGGEST_COMMAND) {
                    String toOpen = message.getStyle().getClickEvent().getValue();
                    Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(new ChatScreen(toOpen)));
                }
            }
        }

        // hide msg matching regex
        if (Config.getBoolean("hideMsgMatchingRegex") && stripped.replaceAll("§.", "").matches(Config.getString("hideMsgRegex"))) {
            cancel = true;
        }

        if (Config.getBoolean("autoTip") && stripped.startsWith("⏵⏵ ")) {
            if (msgWithColor.matches("§x§a§a§5§5§f§f⏵⏵ §f§l\\w+§7 is using a §x§f§f§f§f§a§a§l2§x§f§f§f§f§a§a§lx§7 booster.")) {
                tipPlayer = stripped.split("§f§l")[1].split("§7")[0];
            } else if (msgWithColor.matches("§x§a§a§5§5§f§f⏵⏵ §7Use §x§f§f§f§f§a§a/tip§7 to show your appreciation and receive a §x§f§f§d§4§2§a□ token notch§7!")) {
                LegacyRecode.executor.submit(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception ignored) {}
                    mc.player.commandSigned("tip " + tipPlayer, null);
                });
            }
        }

        result.setValid(!cancel);
    }
}
